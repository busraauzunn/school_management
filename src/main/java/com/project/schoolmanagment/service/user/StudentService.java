package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.ChooseLessonProgramRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.request.user.StudentUpdateRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import java.util.Set;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    student.setStudentNumber(getLastNumber());
    
    return ResponseMessage.<StudentResponse>builder()
        .returnBody(userMapper.mapUserToStudentResponse(userRepository.save(student)))
        .message(SuccessMessages.STUDENT_SAVE)
        .httpStatus(HttpStatus.OK)
        .build();
  }
  
  
  private int getLastNumber(){
    if(!userRepository.findStudent(RoleType.STUDENT)){
      //first student
      return 1000;
    }
    return userRepository.getMaxStudentNumber() + 1;
  }

  public ResponseMessage<StudentResponse> addLessonProgram(HttpServletRequest httpServletRequest,
      ChooseLessonProgramRequest lessonProgramRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    User loggedInStudent = methodHelper.loadUserByName(username);

    Set<LessonProgram>lessonProgramSet = lessonProgramService.getLessonProgramById(lessonProgramRequest.getLessonProgramId());
    
    Set<LessonProgram>existingLessonPrograms = loggedInStudent.getLessonProgramList();
    
    dateTimeValidator.checkLessonPrograms(existingLessonPrograms,lessonProgramSet);
    
    existingLessonPrograms.addAll(lessonProgramSet);
    loggedInStudent.setLessonProgramList(existingLessonPrograms);
    
    User savedStudent = userRepository.save(loggedInStudent);
    
    return ResponseMessage.<StudentResponse>builder()
        .message(SuccessMessages.LESSON_PROGRAM_ADD_TO_STUDENT)
        .returnBody(userMapper.mapUserToStudentResponse(savedStudent))
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public ResponseEntity<String> updateStudent(
      StudentUpdateRequestWithoutPassword studentUpdateRequestWithoutPassword,
      HttpServletRequest request) {
    
    String username = (String) request.getAttribute("username");
    //check if user exist
    User student = methodHelper.loadUserByName(username);
    //handle duplications
    uniquePropertyValidator.checkUniqueProperties(student,studentUpdateRequestWithoutPassword);
    
    //classical mapper usage
    student.setMotherName(studentUpdateRequestWithoutPassword.getMotherName());
    student.setFatherName(studentUpdateRequestWithoutPassword.getFatherName());
    student.setBirthPlace(studentUpdateRequestWithoutPassword.getBirthPlace());
    student.setBirthDay(studentUpdateRequestWithoutPassword.getBirthDay());
    student.setEmail(studentUpdateRequestWithoutPassword.getEmail());
    student.setPhoneNumber(studentUpdateRequestWithoutPassword.getPhoneNumber());
    student.setGender(studentUpdateRequestWithoutPassword.getGender());
    student.setName(studentUpdateRequestWithoutPassword.getName());
    student.setSurname(studentUpdateRequestWithoutPassword.getSurname());
    student.setSsn(studentUpdateRequestWithoutPassword.getSsn());
    
    userRepository.save(student);
    
    return ResponseEntity.ok(SuccessMessages.STUDENT_UPDATE);
    
  }

  public ResponseMessage<StudentResponse> updateStudentByManagers(Long userId,
      StudentRequest studentRequest) {
    //validate user existence
    User student = methodHelper.isUserExist(userId);
    //validate user role
    methodHelper.checkRole(student, RoleType.STUDENT);
    uniquePropertyValidator.checkUniqueProperties(student,studentRequest);
    User studentForUpdate = userMapper.mapUserRequestToUser(studentRequest);
    //set missing mapper properties
    studentForUpdate.setMotherName(studentRequest.getMotherName());
    studentForUpdate.setFatherName(studentRequest.getFatherName());
    //we need to be sure requested advisor teacher id is correct
    User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
    methodHelper.checkRole(advisorTeacher,RoleType.TEACHER);
    methodHelper.checkIsAdvisor(advisorTeacher);
    studentForUpdate.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
    studentForUpdate.setActive(true);
    studentForUpdate.setStudentNumber(student.getStudentNumber());
    studentForUpdate.setId(student.getId());
    
    return ResponseMessage.<StudentResponse>builder()
        .message(SuccessMessages.STUDENT_UPDATE)
        .returnBody(userMapper.mapUserToStudentResponse(userRepository.save(studentForUpdate)))
        .httpStatus(HttpStatus.OK)
        .build();
     
  }

  public ResponseMessage changeStatus(Long id, boolean status) {
    
    User student = methodHelper.isUserExist(id);
    methodHelper.checkRole(student,RoleType.STUDENT);
    student.setActive(status);
    return ResponseMessage.builder()
        .message("Student is " + (status ? "active" : "passive"))
        .httpStatus(HttpStatus.OK)
        .build();
  }
}
