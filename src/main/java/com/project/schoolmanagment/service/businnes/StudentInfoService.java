package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.StudentInfoMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.StudentInfoRequest;
import com.project.schoolmanagment.payload.request.businnes.StudentInfoUpdateRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import com.project.schoolmanagment.repository.businnes.StudentInfoRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentInfoService {
  
  private final MethodHelper methodHelper;
  private final StudentInfoRepository studentInfoRepository;
  private final LessonService lessonService;
  private final EducationTermService educationTermService;
  private final StudentInfoMapper studentInfoMapper;
  private final PageableHelper pageableHelper;
  @Value("${midterm.exam.impact.percentage}")
  private Double midtermExamPercentage;
  @Value("${final.exam.impact.percentage}")
  private Double finalExamPercentage;

  public Page<StudentInfoResponse> getAllByTeacher(HttpServletRequest httpServletRequest,
      int page, int size) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
    String username = (String) httpServletRequest.getAttribute("username");
    return studentInfoRepository.findByTeacherUsername(username,pageable)
        .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
  }

  public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest httpServletRequest,
      StudentInfoRequest studentInfoRequest) {
    
    String teacherUsername = (String) httpServletRequest.getAttribute("username");

    //validate user by id
    User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
    methodHelper.checkRole(student, RoleType.STUDENT);
    User teacher = methodHelper.loadUserByName(teacherUsername);
    //validate and fetch lesson
    Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
    //validate and fetch education term
    EducationTerm educationTerm = educationTermService.isEducationTermExist(
        studentInfoRequest.getEducationTermId());
    validateLessonDuplication(studentInfoRequest.getStudentId(), lesson.getLessonName());
    Note note = checkLetterGrade(calculateExamAverage(studentInfoRequest.getMidtermExam(),
        studentInfoRequest.getFinalExam()));

    StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(
        studentInfoRequest,
        note,
        calculateExamAverage(studentInfoRequest.getMidtermExam(),
            studentInfoRequest.getFinalExam())
    );
    //set missing properties
    studentInfo.setStudent(student);
    studentInfo.setEducationTerm(educationTerm);
    studentInfo.setTeacher(teacher);
    studentInfo.setLesson(lesson);
    StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);
    
    return ResponseMessage.<StudentInfoResponse>builder()
        .message(SuccessMessages.STUDENT_INFO_SAVE)
        .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
        .httpStatus(HttpStatus.OK)
        .build();
  }
  
  //each student can have only one studentInfo entry related to a lesson
  private void validateLessonDuplication(Long studentId,String lessonName){
    if(studentInfoRepository.giveMeDuplications(studentId,lessonName)){
      throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonName));
    }
        
  }
  
  private Double calculateExamAverage(Double midtermExam,Double finalExam){
    return (midtermExam * midtermExamPercentage) + (finalExam * finalExamPercentage);
  }

  private Note checkLetterGrade(Double average){
    if(average<50.0) {
      return Note.FF;
    } else if (average<60) {
      return Note.DD;
    } else if (average<65) {
      return Note.CC;
    } else if (average<70) {
      return  Note.CB;
    } else if (average<75) {
      return  Note.BB;
    } else if (average<80) {
      return Note.BA;
    } else {
      return Note.AA;
    }
  }
  
  public StudentInfo isStudentInfoExist(Long id){
    Optional<StudentInfo> studentInfoOptional = studentInfoRepository.findById(id);
    if (studentInfoOptional.isPresent()) {
      return studentInfoOptional.get();
    } else {
      throw new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND,id));
    }
  }

  public ResponseMessage<StudentInfoResponse> updateStudentInfo(
      StudentInfoUpdateRequest studentInfoUpdateRequest, Long studentInfoId) {
    //validate lesson existence
    Lesson lesson = lessonService.isLessonExistById(studentInfoUpdateRequest.getLessonId());
    
    StudentInfo studentInfo = isStudentInfoExist(studentInfoId);
    //validate education term existence
    EducationTerm educationTerm = educationTermService.isEducationTermExist(studentInfoUpdateRequest.getEducationTermId());
    
    Double noteAverage = calculateExamAverage(studentInfoUpdateRequest.getMidtermExam(),
        studentInfoUpdateRequest.getFinalExam());
    
    Note note = checkLetterGrade(noteAverage);
    
    StudentInfo studentInfoToUpdate = studentInfoMapper.mapStudentInfoUpdateRequestToStudentInfo(
        studentInfoUpdateRequest,
        studentInfoId,
        lesson,
        educationTerm,
        note,
        noteAverage);
    //we are not updating teacher and student
    studentInfoToUpdate.setStudent(studentInfo.getStudent());
    studentInfoToUpdate.setTeacher(studentInfo.getTeacher());
    StudentInfo updatedStudentInfo = studentInfoRepository.save(studentInfoToUpdate);
    return ResponseMessage.<StudentInfoResponse>builder()
        .message(SuccessMessages.STUDENT_INFO_UPDATE)
        .httpStatus(HttpStatus.OK)
        .returnBody(studentInfoMapper.mapStudentInfoToStudentInfoResponse(updatedStudentInfo))
        .build();
  }

  public ResponseMessage deleteStudentInfo(Long studentInfo) {
    isStudentInfoExist(studentInfo);
    studentInfoRepository.deleteById(studentInfo);
    return ResponseMessage.builder()
        .message(SuccessMessages.STUDENT_INFO_DELETE)
        .httpStatus(HttpStatus.OK)
        .build();
  }

  public Page<StudentInfoResponse> findStudentInfoByPage(int page, int size, String sort,
      String type) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
    return studentInfoRepository.findAll(pageable)
        .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
  }

  public StudentInfoResponse findStudentInfoById(Long studentInfoId) {    
    return studentInfoMapper.mapStudentInfoToStudentInfoResponse(isStudentInfoExist(studentInfoId));    
  }

  public List<StudentInfoResponse> getByStudentId(Long studentId) {
    User student = methodHelper.isUserExist(studentId);
    methodHelper.checkRole(student,RoleType.STUDENT);    
    return studentInfoRepository.findByStudentId(studentId)
        .stream().map(studentInfoMapper::mapStudentInfoToStudentInfoResponse)
        .collect(Collectors.toList());
  }

  public Page<StudentInfoResponse> getAllByStudent(HttpServletRequest httpServletRequest, int page,
      int size) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page,size);
    String username = (String) httpServletRequest.getAttribute("username");
    return studentInfoRepository.findByStudentUsername(username,pageable)
        .map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
  }
}
