package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.businnes.AddLessonProgramToTeacherRequest;
import com.project.schoolmanagment.payload.request.businnes.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.service.user.TeacherService;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teacher")
@RequiredArgsConstructor
public class TeacherController {
  
  private final TeacherService teacherService;


  @PreAuthorize("hasAnyAuthority('Admin')")
  @PostMapping("/save")
  public ResponseEntity<ResponseMessage<UserResponse>> saveTeacher(
      @RequestBody @Valid TeacherRequest teacherRequest){
    return ResponseEntity.ok(teacherService.saveTeacher(teacherRequest));
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @PutMapping("/update/{userId}")
  public ResponseMessage<UserResponse>updateTeacherByManagers(
      @RequestBody @Valid TeacherRequest teacherRequest,
      @PathVariable Long userId){
    return teacherService.updateTeacherByManagers(teacherRequest,userId);
  }
  
  @PreAuthorize("hasAnyAuthority('Teacher')")
  @GetMapping("/getAllStudentByAdvisorUsername")
  public List<StudentResponse>getAllStudentByAdvisorTeacher(HttpServletRequest httpServletRequest){
    return teacherService.getAllStudentByAdvisorTeacher(httpServletRequest);
  }


  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @PostMapping("/addLessonProgram")
  public ResponseMessage<UserResponse>chooseLesson(@RequestBody @Valid
  AddLessonProgramToTeacherRequest addLessonProgramToTeacherRequest){
    return teacherService.addLessonProgramToTeacher(addLessonProgramToTeacherRequest);
  }
  


  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @GetMapping("/deleteAdvisorTeacherById/{id}")
  public ResponseMessage<UserResponse>deleteTeacherById(@PathVariable Long id){
    return teacherService.changeAdvisorTeacherStatus(id);
  }


  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @GetMapping("/getAllAdvisorTeacher")
  public List<UserResponse>getAllAdvisorTeacher(){
    return teacherService.getAllAdvisorTeacher();
  }
  
  

}
