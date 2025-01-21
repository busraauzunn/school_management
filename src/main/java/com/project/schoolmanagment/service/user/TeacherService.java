package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.AddLessonProgramToTeacherRequest;
import com.project.schoolmanagment.payload.request.businnes.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
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

  public ResponseMessage<UserResponse> changeAdvisorTeacherStatus(Long id) {   
    
    User teacher = methodHelper.isUserExist(id);
    methodHelper.checkRole(teacher,RoleType.TEACHER);
    methodHelper.checkIsAdvisor(teacher);
    
    teacher.setIsAdvisor(false);
    userRepository.save(teacher);

    List<User>allStudents = userRepository.findByAdvisorTeacherId(id);
    if(!allStudents.isEmpty()){
      allStudents.forEach(students ->students.setAdvisorTeacherId(null));
    }
    
    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.ADVISOR_TEACHER_DELETE)
        .returnBody(userMapper.mapUserToUserResponse(teacher))
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public List<UserResponse> getAllAdvisorTeacher() {    
    return userRepository.findAllByAdvisorTeacher()
        .stream()
        .map(userMapper::mapUserToUserResponse)
        .collect(Collectors.toList());
    
  }

  public ResponseMessage<UserResponse> updateTeacherByManagers(TeacherRequest teacherRequest,
      Long userId) {
    
    User teacher = methodHelper.isUserExist(userId);
    methodHelper.checkRole(teacher,RoleType.TEACHER);
    Set<LessonProgram>lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsProgramIdList());
    User teacherToSave = userMapper.mapUserRequestToUser(teacherRequest);
    //we are setting teacher custom properties
    teacherToSave.setId(teacher.getId());
    teacherToSave.setLessonProgramList(lessonPrograms);
    teacherToSave.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
    
    User savedTeacher = userRepository.save(teacherToSave);
    
    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.TEACHER_UPDATE)
        .returnBody(userMapper.mapUserToUserResponse(savedTeacher))
        .httpStatus(HttpStatus.OK)
        .build();    
  }

  public List<StudentResponse> getAllStudentByAdvisorTeacher(
      HttpServletRequest httpServletRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    User teacher = methodHelper.loadUserByName(username);
    methodHelper.checkIsAdvisor(teacher);
    return userRepository.findByAdvisorTeacherId(teacher.getId())
        .stream()
        .map(userMapper::mapUserToStudentResponse)
        .collect(Collectors.toList());  
  }

  public ResponseMessage<UserResponse> addLessonProgramToTeacher(
      AddLessonProgramToTeacherRequest addLessonProgramToTeacherRequest) {
    User teacher = methodHelper.isUserExist(addLessonProgramToTeacherRequest.getTeacherId());
    methodHelper.checkRole(teacher,RoleType.TEACHER);
    //existing ones
    Set<LessonProgram>existingLessonPrograms = teacher.getLessonProgramList();
    //requested ones
    Set<LessonProgram>requestedLessonPrograms = lessonProgramService.getLessonProgramById(addLessonProgramToTeacherRequest.getLessonProgramId());
    dateTimeValidator.checkLessonPrograms(existingLessonPrograms,requestedLessonPrograms);
    existingLessonPrograms.addAll(requestedLessonPrograms);
    teacher.setLessonProgramList(existingLessonPrograms);
    User updatedTeacher = userRepository.save(teacher);
    
    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
        .httpStatus(HttpStatus.OK)
        .returnBody(userMapper.mapUserToUserResponse(updatedTeacher))
        .build();   
  }
}
