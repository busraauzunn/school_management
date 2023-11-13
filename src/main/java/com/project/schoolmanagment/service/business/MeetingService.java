package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exeption.BadRequestException;
import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.MeetingMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.MeetingRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.MeetingResponse;
import com.project.schoolmanagment.repository.business.MeetingRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.user.UserService;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {

	private final MeetingRepository meetingRepository;
	private final UserService userService;
	private final MethodHelper methodHelper;
	private final DateTimeValidator dateTimeValidator;
	private final MeetingMapper meetingMapper;


	public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest request, MeetingRequest meetingRequest) {
		String username = (String) request.getAttribute("username");

		//validate username
		User advisorTeacher = methodHelper.isUserExistByUsername(username);
		//validate is advisor teacher
		methodHelper.checkAdvisor(advisorTeacher);

		dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(), meetingRequest.getStopTime());

		checkMeetConflict(advisorTeacher.getId(),
							meetingRequest.getDate(),
							meetingRequest.getStartTime(),
							meetingRequest.getStopTime());

		List<User>students = userService.findUsersByIdArray(meetingRequest.getStudentIds());
		//validate are they really student
		for (User user:students){
			methodHelper.checkRole(user, RoleType.STUDENT);
		}

		Meet meet = meetingMapper.mapMeetRequestToMeet(meetingRequest);
		meet.setStudentList(students);
		meet.setAdvisoryTeacher(advisorTeacher);
		Meet savedMeet = meetingRepository.save(meet);

		return ResponseMessage.<MeetingResponse>builder()
				.message(SuccessMessages.MEET_SAVE)
				.object(meetingMapper.mapMeetToMeetingResponse(savedMeet))
				.httpStatus(HttpStatus.OK)
				.build();
	}


	private void checkMeetConflict(Long userId, LocalDate date, LocalTime startTime, LocalTime stopTime){
		List<Meet>meets;

		//try to understand if it is student or teacher and get all meetings
		if(Boolean.TRUE.equals(methodHelper.isUserExist(userId).getIsAdvisor())) {
			meets = meetingRepository.getByAdvisoryTeacher_IdEquals(userId);
		} else meets = meetingRepository.findByStudentList_IdEquals(userId);

		//TODO bug has been found -> when user try to update the meeting with only student ID.s it returns a conflict exception
		//conflict validation
		for (Meet meet :meets){
			LocalTime existingStartTime = meet.getStartTime();
			LocalTime existingStopTime = meet.getStopTime();

			if(meet.getDate().equals(date) &&
					(		(startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
							(stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
							(startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
							(startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))
					)
			){
				throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
			}
		}
	}


	public List<MeetingResponse> getAll() {
		return meetingRepository.findAll()
				.stream()
				.map(meetingMapper::mapMeetToMeetingResponse)
				.collect(Collectors.toList());
	}

	public ResponseMessage<MeetingResponse> getMeetingById(Long id) {
		return ResponseMessage.<MeetingResponse>builder()
						.message(SuccessMessages.MEET_FOUND)
						.httpStatus(HttpStatus.OK)
						.object(meetingMapper.mapMeetToMeetingResponse(isMeetingExist(id)))
						.build();
	}

	private Meet isMeetingExist(Long id){
		return meetingRepository
				.findById(id)
				.orElseThrow(()->new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE,id)));
	}

	public ResponseMessage deleteById(Long id) {
		Meet meet = isMeetingExist(id);
		meetingRepository.deleteById(meet.getId());
		return ResponseMessage.builder()
				.message(SuccessMessages.MEET_DELETE)
				.httpStatus(HttpStatus.OK)
				.build();
	}

	public ResponseMessage<MeetingResponse> updateMeeting(Long meetingId, MeetingRequest meetingRequest, HttpServletRequest request) {

		Meet meet = isMeetingExist(meetingId);
		//validate is teacher is updating his/her own meeting
		isMeetingAssignToThisTeacher(meet,request);
		//validate the time
		dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),
													meetingRequest.getStopTime());
		if(meet.getDate().equals(meetingRequest.getDate()) && meet.getStartTime().equals(meetingRequest.getStartTime()) &&
		meet.getStopTime().equals(meetingRequest.getStopTime())){
			//conflicts related to students
			for (Long studentId: meetingRequest.getStudentIds()){
				checkMeetConflict(studentId,
						meetingRequest.getDate(),
						meetingRequest.getStartTime(),
						meetingRequest.getStopTime());
			}
			//conflicts related to teacher
			checkMeetConflict(meet.getAdvisoryTeacher().getId(),
					meetingRequest.getDate(),
					meetingRequest.getStartTime(),
					meetingRequest.getStopTime());
		}
		List<User>students = userService.findUsersByIdArray(meetingRequest.getStudentIds());
		Meet updateMeet = meetingMapper.mapMeetUpdateRequestToMeet(meetingRequest,meetingId);
		//set missing properties
		updateMeet.setStudentList(students);
		updateMeet.setAdvisoryTeacher(meet.getAdvisoryTeacher());
		Meet savedMeeting = meetingRepository.save(updateMeet);
		return ResponseMessage.<MeetingResponse>builder()
				.message(SuccessMessages.MEET_UPDATE)
				.httpStatus(HttpStatus.OK)
				.object(meetingMapper.mapMeetToMeetingResponse(savedMeeting))
				.build();
	}


	private void isMeetingAssignToThisTeacher(Meet meet, HttpServletRequest request){
		String username = (String) request.getAttribute("username");
		User user = methodHelper.isUserExistByUsername(username);
		if(user.getUserRole().getRoleType().getName().equals("Teacher") &&
				(meet.getAdvisoryTeacher().getAdvisorTeacherId()!=(user.getId()))){
				throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
		}
	}

	public ResponseEntity<List<MeetingResponse>> getAllMeetByTeacher(HttpServletRequest request) {
		//get username from header/attribute
		String username = (String) request.getAttribute("username");
		//get user from db
		User advisorTeacher = methodHelper.isUserExistByUsername(username);
		//validate teacher is advisor or not
		methodHelper.checkAdvisor(advisorTeacher);

		List<MeetingResponse>meetResponseList =
				meetingRepository.findAll()
						.stream()
						.filter(x->x.getAdvisoryTeacher().getId()== advisorTeacher.getId())
						.map(meetingMapper::mapMeetToMeetingResponse)
						.collect(Collectors.toList());
		return ResponseEntity.ok(meetResponseList);
		//TODO query does not work as expected- CHECK
//		List<MeetingResponse>meetResponseList =
//				meetingRepository.getByAdvisoryTeacher_IdEquals(advisorTeacher.getAdvisorTeacherId())
//						.stream()
//						.map(meetingMapper::mapMeetToMeetingResponse)
//						.collect(Collectors.toList());
//		return ResponseEntity.ok(meetResponseList);
	}

	public ResponseEntity<List<MeetingResponse>> getAllMeetByStudent(HttpServletRequest request) {
		String username = (String) request.getAttribute("username");
		User student = methodHelper.isUserExistByUsername(username);
		List<MeetingResponse>meetResponseList =
				meetingRepository.findByStudentList_IdEquals(student.getId())
						.stream()
						.map(meetingMapper::mapMeetToMeetingResponse)
						.collect(Collectors.toList());
		return ResponseEntity.ok(meetResponseList);
	}
}
