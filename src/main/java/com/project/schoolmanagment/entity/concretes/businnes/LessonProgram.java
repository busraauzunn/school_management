package com.project.schoolmanagment.entity.concretes.businnes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Day;
import java.time.LocalTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreRemove;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The LessonProgram class represents a lesson program.
 * It contains information about the lesson program, such as the day, start time, stop time,
 * associated lessons, education term, and users enrolled in the program.
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LessonProgram {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Enumerated(EnumType.STRING)
  private Day day;
  
  @JsonFormat(shape = Shape.STRING,pattern = "HH:mm",timezone = "US")
  private LocalTime startTime;

  @JsonFormat(shape = Shape.STRING,pattern = "HH:mm",timezone = "US")
  private LocalTime stopTime;
  
  //manyToMany relations have always third table
  @ManyToMany
  //we are specifying the table and column names
  @JoinTable(
      name = "lesson_program_lesson",
      joinColumns = @JoinColumn(name = "lessonprogram_id"),
      inverseJoinColumns = @JoinColumn(name = "lesson_id")
  )
  private Set<Lesson>lessons;
  
  //TODO learn about all cascade ops. cases
  @ManyToOne(cascade = CascadeType.PERSIST)
  private EducationTerm educationTerm;
  
  @JsonProperty(access = Access.READ_ONLY)
  @ManyToMany(mappedBy = "lessonProgramList",fetch = FetchType.EAGER)
  private Set<User>users;
  
  //we are fixing the issue related to "what will happen 
  // if we delete a lessonProgram
  //while it exist for a user
  @PreRemove
  private void removeLessonFromUser(){
    users.forEach(user -> user.getLessonProgramList().remove(this));
  }
  

}
