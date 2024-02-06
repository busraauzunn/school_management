package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
  

  @Transactional
  public ResponseMessage<UserResponse> saveTeacher(TeacherRequest teacherRequest) {

    Set<LessonProgram>lessonProgramSet = 
        lessonProgramService.getLessonProgramById(teacherRequest.getLessonsProgramIdList());
    
    uniquePropertyValidator.checkDuplicate(
        teacherRequest.getUsername(),
        teacherRequest.getSsn(),
        teacherRequest.getPhoneNumber(),
        teacherRequest.getEmail());

    User teacher = userMapper.mapUserRequestToUser(teacherRequest);
    //set extra properties that exist in teacher
    teacher.setIsAdvisor(teacherRequest.getIsAdvisorTeacher());
    teacher.setLessonProgramList(lessonProgramSet);
    teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
    
    User savedTeacher = userRepository.save(teacher);
    
    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.TEACHER_SAVE)
        .httpStatus(HttpStatus.CREATED)
        .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
        .build();
  }
}
