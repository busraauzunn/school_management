package com.project.schoolmanagment.controller.businnes;

import com.project.schoolmanagment.payload.request.businnes.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.service.businnes.MeetingService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetingController {

  private final MeetingService meetingService;

  @PreAuthorize("hasAnyAuthority('Teacher')")
  @PostMapping("/save")
  public ResponseMessage<MeetingResponse>saveMeeting(HttpServletRequest httpServletRequest,
      @RequestBody @Valid MeetingRequest meetingRequest){
    return meetingService.saveMeeting(httpServletRequest,meetingRequest);
  }
  
  
  
  
}
