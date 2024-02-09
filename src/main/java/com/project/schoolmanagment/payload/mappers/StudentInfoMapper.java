package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.payload.request.businnes.StudentInfoRequest;
import com.project.schoolmanagment.payload.request.businnes.StudentInfoUpdateRequest;
import com.project.schoolmanagment.payload.response.businnes.StudentInfoResponse;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@RequiredArgsConstructor
public class StudentInfoMapper {
  
  private final UserMapper userMapper;
  
  
  public StudentInfo mapStudentInfoRequestToStudentInfo(StudentInfoRequest studentInfoRequest,
      Note note, Double average){
    return StudentInfo.builder()
        .infoNote(studentInfoRequest.getInfoNote())
        .absentee(studentInfoRequest.getAbsentee())
        .midtermExam(studentInfoRequest.getMidtermExam())
        .finalExam(studentInfoRequest.getFinalExam())
        .examAverage(average)
        .letterGrade(note)
        .build();
  }
  
  public StudentInfoResponse mapStudentInfoToStudentInfoResponse(StudentInfo studentInfo){
    return StudentInfoResponse.builder()
        .lessonName(studentInfo.getLesson().getLessonName())
        .creditScore(studentInfo.getLesson().getCreditScore())
        .isCompulsory(studentInfo.getLesson().getIsCompulsory())
        .educationTerm(studentInfo.getEducationTerm().getTerm())
        .id(studentInfo.getId())
        .absentee(studentInfo.getAbsentee())
        .midtermExam(studentInfo.getMidtermExam())
        .finalExam(studentInfo.getFinalExam())
        .infoNote(studentInfo.getInfoNote())
        .note(studentInfo.getLetterGrade())
        .average(studentInfo.getExamAverage())
        .studentResponse(userMapper.mapUserToStudentResponse(studentInfo.getStudent()))
        .build();
  }
  
  public StudentInfo mapStudentInfoUpdateRequestToStudentInfo(
      StudentInfoUpdateRequest studentInfoUpdateRequest,
      Long studentInfoRequestId,
      Lesson lesson,
      EducationTerm educationTerm,
      Note note,
      Double average){
    
    return StudentInfo.builder()
        .id(studentInfoRequestId)
        .infoNote(studentInfoUpdateRequest.getInfoNote())
        .midtermExam(studentInfoUpdateRequest.getMidtermExam())
        .finalExam(studentInfoUpdateRequest.getFinalExam())
        .absentee(studentInfoUpdateRequest.getAbsentee())
        .lesson(lesson)
        .educationTerm(educationTerm)
        .examAverage(average)
        .letterGrade(note)
        .build();
  }
  
  

}
