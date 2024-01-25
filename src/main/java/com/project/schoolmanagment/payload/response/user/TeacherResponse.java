package com.project.schoolmanagment.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.payload.response.abstracts.BaseUserResponse;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(Include.NON_NULL)
public class TeacherResponse extends BaseUserResponse {
  
  private Set<LessonProgram>lessonProgramSet;
  private Boolean isAdvisorTeacher;

}
