package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.payload.mappers.MeetingMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.MeetingRepository;
import com.project.schoolmanagment.service.helper.MeetingHelper;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.user.UserService;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final MeetingHelper meetingHelper;
  private final PageableHelper pageableHelper;

  public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest httpServletRequest,
      MeetingRequest meetingRequest) {
    
    String username = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());
    
    //validate meeting conflicts
    meetingHelper.checkMeetingConflicts(meetingRequest.getStudentIds(),
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


  public ResponseMessage<MeetingResponse> updateMeeting(MeetingRequest meetingRequest,
      Long meetingId, HttpServletRequest request) {
    Meet meet = meetingHelper.isMeetingExistById(meetingId);
    //validating teacher and meeting are matched
    meetingHelper.isMeetingMatchedWithTeacher(meet,request);
    dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());
    meetingHelper.checkMeetingConflicts(meetingRequest.getStudentIds(),
        meet.getAdvisoryTeacher().getId(),
        meetingRequest.getDate(),
        meetingRequest.getStartTime(),
        meetingRequest.getStopTime());
    
    List<User>students = methodHelper.getUserList(meetingRequest.getStudentIds());
    Meet meetToUpdate = meetingMapper.mapMeetingRequestToMeet(meetingRequest);
    //need to set missing parameters
    meetToUpdate.setStudentList(students);
    meetToUpdate.setAdvisoryTeacher(meet.getAdvisoryTeacher());
    meetToUpdate.setId(meetingId);
    Meet updatedMeet = meetingRepository.save(meetToUpdate);
    return ResponseMessage.<MeetingResponse>builder()
        .message(SuccessMessages.MEET_SAVE)
        .returnBody(meetingMapper.mapMeetToMeetingResponse(updatedMeet))
        .httpStatus(HttpStatus.OK)
        .build();
  }


  public List<MeetingResponse> getAll() {
    return meetingRepository.findAll()
        .stream()
        .map(meetingMapper::mapMeetToMeetingResponse)
        .collect(Collectors.toList());   
  }

  public ResponseMessage deleteById(Long id) {
    meetingHelper.isMeetingExistById(id);    
    meetingRepository.deleteById(id);
    return ResponseMessage.builder()
        .message(SuccessMessages.MEET_DELETE)
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public List<MeetingResponse> getAllMeetingsByLoggedInTeacher(
      HttpServletRequest httpServletRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    return meetingRepository.getByAdvisoryTeacher_IdEquals(teacher.getId())
        .stream()
        .map(meetingMapper::mapMeetToMeetingResponse)
        .collect(Collectors.toList());    
  }

  public List<MeetingResponse> getAllMeetingsByLoggedInStudent(
      HttpServletRequest httpServletRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    User loggedInStudent = methodHelper.loadUserByName(username);
    return meetingRepository.findByStudentList_IdEquals(loggedInStudent.getId())
        .stream()
        .map(meetingMapper::mapMeetToMeetingResponse)
        .collect(Collectors.toList());
  }

  public Page<MeetingResponse> getAllByPage(int page, int size) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
    return meetingRepository.findAll(pageable).map(meetingMapper::mapMeetToMeetingResponse);    
  }


  public Page<MeetingResponse> getAllMeetByPageByTeacher(int page, int size,
      HttpServletRequest request) {
    String username = (String) request.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
    return meetingRepository.findByAdvisoryTeacher_IdEquals(teacher.getId(), pageable)
        .map(meetingMapper::mapMeetToMeetingResponse);
  }
}
