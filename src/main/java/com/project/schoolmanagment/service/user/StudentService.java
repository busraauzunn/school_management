package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
  
  private final UserRepository userRepository;
  private final UserRoleService userRoleService;
  private final UniquePropertyValidator uniquePropertyValidator;
  private final PasswordEncoder passwordEncoder;
  private final LessonProgramService lessonProgramService;
  private final DateTimeValidator dateTimeValidator;
  private final MethodHelper methodHelper;
  private final UserMapper userMapper;

  public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {

    //do we really have a user with this ID
    User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
    // check if this user advisor teacher
    methodHelper.checkIsAdvisor(advisorTeacher);
    
    uniquePropertyValidator.checkDuplicate(
        studentRequest.getUsername(),
        studentRequest.getSsn(),
        studentRequest.getPhoneNumber(),
        studentRequest.getEmail());
    
    User student = userMapper.mapUserRequestToUser(studentRequest);
    //set missing properties
    student.setAdvisorTeacherId(advisorTeacher.getId());
    student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
    student.setActive(true);
    student.setIsAdvisor(false);
    
  }
}
