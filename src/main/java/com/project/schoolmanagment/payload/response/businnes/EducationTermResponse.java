package com.project.schoolmanagment.payload.response.businnes;

import com.project.schoolmanagment.entity.enums.Term;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationTermResponse {


  private Long id;
  private Term term;
  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDate lastRegistrationDate;

}
