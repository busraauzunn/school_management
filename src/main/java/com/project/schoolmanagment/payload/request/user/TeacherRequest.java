package com.project.schoolmanagment.payload.request.user;

import com.project.schoolmanagment.payload.request.abstracts.BaseUserRequest;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class TeacherRequest extends BaseUserRequest {

  @NotNull(message = "Please select Lesson Program")
  private Set<Long> lessonsProgramIdList;

  @NotNull(message = "Please select isAdvisor Teacher")
  private Boolean isAdvisorTeacher;
  
}
