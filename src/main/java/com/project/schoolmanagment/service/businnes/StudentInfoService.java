package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.request.businnes.StudentInfoRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import com.project.schoolmanagment.repository.businnes.StudentInfoRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentInfoService {
  
  private final MethodHelper methodHelper;
  private final StudentInfoRepository studentInfoRepository;
  private final LessonService lessonService;
  private final EducationTermService educationTermService;
  @Value("${midterm.exam.impact.percentage}")
  private Double midtermExamPercentage;
  @Value("${final.exam.impact.percentage}")
  private Double finalExamPercentage;

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
}
