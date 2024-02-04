package com.project.schoolmanagment.payload.response.businnes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.enums.Day;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class LessonProgramResponse {

  private Long lessonProgramId;
  private Day day;
  private LocalTime startTime;
  private LocalTime stopTime;
  private Set<Lesson>lessonName;
  private EducationTerm educationTerm;
  private Set<TeacherResponse>teachers;
  private Set<StudentResponse>student;
}
