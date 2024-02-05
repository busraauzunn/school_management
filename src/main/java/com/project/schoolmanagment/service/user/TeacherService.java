package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {
  
  private final UserRepository userRepository;
  private final UserRoleService userRoleService;
  private final UniquePropertyValidator uniquePropertyValidator;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final LessonProgramService lessonProgramService;
  private final MethodHelper methodHelper;
  private final DateTimeValidator dateTimeValidator;
  

  public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {

    Set<LessonProgram>lessonProgramSet = 
        lessonProgramService.getLessonProgramById(teacherRequest.getLessonsProgramIdList());
    
    
    
    
    
  }
}
