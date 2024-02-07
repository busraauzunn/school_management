package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.businnes.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.service.user.StudentService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
  
  private final StudentService studentService;


  @PreAuthorize("hasAnyAuthority('Admin')")
  @PostMapping("/save")
  public ResponseEntity<ResponseMessage<StudentResponse>>saveStudent(
      @RequestBody @Valid StudentRequest studentRequest){
    return ResponseEntity.ok(studentService.saveStudent(studentRequest));
  }


  @PreAuthorize("hasAnyAuthority('Student')")
  @PostMapping("/addLessonProgramToStudent")
  public ResponseMessage<StudentResponse>addLessonProgram(HttpServletRequest httpServletRequest,
      @RequestBody @Valid ChooseLessonProgramRequest lessonProgramRequest){
    return studentService.addLessonProgram(httpServletRequest,lessonProgramRequest);
  }

}
