package com.project.schoolmanagment.controller.businnes;

import com.project.schoolmanagment.payload.request.businnes.MeetingRequest;
import com.project.schoolmanagment.payload.response.businnes.MeetingResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.service.businnes.MeetingService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @PreAuthorize("hasAnyAuthority('Teacher')")
  @PostMapping("/update/{meetingId}")
  public ResponseMessage<MeetingResponse>updateMeeting(
      @RequestBody @Valid MeetingRequest meetingRequest,
      @PathVariable Long meetingId,
      HttpServletRequest request){
    return meetingService.updateMeeting(meetingRequest,meetingId,request);
  }

  @PreAuthorize("hasAnyAuthority('Admin')")
  @GetMapping("/getAll")
  public List<MeetingResponse>getAll(){
    return meetingService.getAll();
  }

  @PreAuthorize("hasAnyAuthority('Admin','Teacher')")
  @DeleteMapping("/delete/{id}")
  public ResponseMessage deleteById(@PathVariable Long id){
    return meetingService.deleteById(id);
  }

  @PreAuthorize("hasAnyAuthority('Teacher')")
  @GetMapping("/getAllByTeacher")
  public ResponseEntity<List<MeetingResponse>>getAllMeetingsByLoggedInTeacher(HttpServletRequest httpServletRequest){
    return ResponseEntity.ok(meetingService.getAllMeetingsByLoggedInTeacher(httpServletRequest));
  }

  @PreAuthorize("hasAnyAuthority('Student')")
  @GetMapping("/getAllByStudent")
  public ResponseEntity<List<MeetingResponse>>getAllMeetingsByLoggedInStudent(HttpServletRequest httpServletRequest){
    return ResponseEntity.ok(meetingService.getAllMeetingsByLoggedInStudent(httpServletRequest));
  }
  
  @PreAuthorize("hasAnyAuthority('Admin')")
  @GetMapping("/getAllMeetByPage")
  public Page<MeetingResponse>getAllByPage(
      @RequestParam(value = "page") int page,
      @RequestParam(value = "size") int size){
    return meetingService.getAllByPage(page,size);
  }


  @PreAuthorize("hasAnyAuthority('Teacher')")
  @GetMapping("/getAllMeetByPageByTeacher")
  public Page<MeetingResponse>getAllMeetByPageByTeacher(
      HttpServletRequest request,
      @RequestParam(value = "page") int page,
      @RequestParam(value = "size") int size){
    return meetingService.getAllMeetByPageByTeacher(page,size,request);
  }
  
  
  
  
  
}
