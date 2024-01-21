package com.project.schoolmanagment.entity.concretes.businnes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.project.schoolmanagment.entity.enums.Term;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The EducationTerm class represents an education term.
 * It contains information about the term, start and end dates,
 * last registration date, and the lesson programs associated with the term.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EducationTerm {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Enumerated(EnumType.STRING)
  private Term term;
  
  @Column(name = "start_date")
  @JsonFormat(shape = Shape.STRING,pattern ="yyyy-MM-dd" )
  private LocalDate startDate;

  @Column(name = "end_date")
  @JsonFormat(shape = Shape.STRING,pattern ="yyyy-MM-dd" )
  private LocalDate endDate;

  @Column(name = "last_registration_date")
  @JsonFormat(shape = Shape.STRING,pattern ="yyyy-MM-dd" )
  private LocalDate lastRegistrationDate;
  
  @OneToMany(mappedBy = "educationTerm",cascade = CascadeType.ALL)
  @JsonProperty(access = Access.WRITE_ONLY)
  private List<LessonProgram> lessonProgram;
  

}
