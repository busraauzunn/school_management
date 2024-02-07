package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.payload.request.businnes.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.MeetingRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.user.UserService;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingService {
  private final MeetingRepository meetingRepository;
  private final UserService userService;
  private final MethodHelper methodHelper;
  private final DateTimeValidator dateTimeValidator;

  public ResponseMessage<MeetingResponse> saveMeeting(HttpServletRequest httpServletRequest,
      MeetingRequest meetingRequest) {
    
    String username = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    dateTimeValidator.checkTimeWithException(meetingRequest.getStartTime(),meetingRequest.getStopTime());
    
    //validate meeting conflicts
    
    
    
  }
}
