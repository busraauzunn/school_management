package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.MeetingMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.MeetingRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.user.UserService;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {
  private final MeetingRepository meetingRepository;
  private final UserService userService;
  private final MethodHelper methodHelper;
  private final DateTimeValidator dateTimeValidator;
  private final MeetingMapper meetingMapper;

  public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest httpServletRequest,
      MeetingRequest meetingRequest) {
    
    String username = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());
    
    //validate meeting conflicts
    checkMeetingConflicts(meetingRequest.getStudentIds(),
        teacher.getId(), 
        meetingRequest.getDate(),
        meetingRequest.getStartTime(),
        meetingRequest.getStopTime());
    
    List<User>students = methodHelper.getUserList(meetingRequest.getStudentIds());
    Meet meet = meetingMapper.mapMeetingRequestToMeet(meetingRequest);
    meet.setStudentList(students);
    meet.setAdvisoryTeacher(teacher);
    Meet savedMeet = meetingRepository.save(meet);
    return ResponseMessage.<MeetingResponse>builder()
        .message(SuccessMessages.MEET_SAVE)
        .returnBody(meetingMapper.mapMeetToMeetingResponse(savedMeet))
        .httpStatus(HttpStatus.OK)
        .build();
    
    
  }
  
  private void checkMeetingConflicts(List<Long>studentIdList,Long teacherId, LocalDate meetingDate, LocalTime startTime, LocalTime stopTime){

    List<Meet>existingMeetings = new ArrayList<>();
    for (Long id:studentIdList){
      //check student really exist + is a student
      methodHelper.checkRole(methodHelper.isUserExist(id), RoleType.STUDENT);
      existingMeetings.addAll(meetingRepository.findByStudentList_IdEquals(id));
    }    
    existingMeetings.addAll(meetingRepository.getByAdvisoryTeacher_IdEquals(teacherId));
    
    for (Meet meet:existingMeetings){
      LocalTime existingStartTime = meet.getStartTime();
      LocalTime existingStopTime = meet.getStopTime();
      
      if(meet.getDate().equals(meetingDate) && (		
          (startTime.isAfter(existingStartTime) && startTime.isBefore(existingStopTime)) ||
          (stopTime.isAfter(existingStartTime) && stopTime.isBefore(existingStopTime)) ||
          (startTime.isBefore(existingStartTime) && stopTime.isAfter(existingStopTime)) ||
          (startTime.equals(existingStartTime) || stopTime.equals(existingStopTime))
      )) {
        throw new ConflictException(ErrorMessages.MEET_HOURS_CONFLICT);
      }
    }
    
  }


  public ResponseMessage<MeetingResponse> updateMeeting(MeetingRequest meetingRequest,
      Long meetingId, HttpServletRequest request) {
    Meet meet = isMeetingExistById(meetingId);
    //validating teacher and meeting are matched
    isMeetingMatchedWithTeacher(meet,request);
    dateTimeValidator.checkTimeWithException();
    
    
    
  }
  
  
  public Meet isMeetingExistById(Long id){
    return meetingRepository.findById(id)
        .orElseThrow(
            ()->new ResourceNotFoundException(String.format(ErrorMessages.MEET_NOT_FOUND_MESSAGE,id)));
  }
  
  private void isMeetingMatchedWithTeacher(Meet meet, HttpServletRequest httpServletRequest){
    String username = (String) httpServletRequest.getAttribute("username");    
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);    
    if(!meet.getAdvisoryTeacher().getId().equals(teacher.getAdvisorTeacherId())){
      throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
    }
  }
}
