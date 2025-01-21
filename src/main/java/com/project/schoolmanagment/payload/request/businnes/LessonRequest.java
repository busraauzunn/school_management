package com.project.schoolmanagment.payload.request.businnes;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonRequest {


  @NotNull(message = "Please enter Lesson name")
  @Size(min = 2, max = 16, message = "Lesson name should be at least 2 characters")
  @Pattern(regexp = "\\A(?!\\s*\\Z).+" ,message="Lesson name must consist of the characters .")
  private String lessonName;

  @NotNull(message = "Please enter credit score")
  private Integer creditScore;

  @NotNull(message = "Please enter isCompulsory")
  private Boolean isCompulsory;
}
