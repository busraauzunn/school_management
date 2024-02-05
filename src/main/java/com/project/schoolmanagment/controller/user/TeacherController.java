package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.service.user.TeacherService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {
  
  private final TeacherService teacherService;


  @PreAuthorize("hasAnyAuthority('Dean')")
  @PostMapping("/save")
  public ResponseEntity<ResponseMessage<TeacherResponse>> saveTeacher(
      @RequestBody @Valid TeacherRequest teacherRequest){
    return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
  }

}
