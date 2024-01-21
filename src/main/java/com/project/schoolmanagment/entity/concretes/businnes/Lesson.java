package com.project.schoolmanagment.entity.concretes.businnes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Lesson class represents a lesson.
 * It contains information about the lesson, such as the name,
 * credit score, compulsory status, and the lesson programs associated with it.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Lesson {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String lessonName;
  
  private Integer creditScore;
  
  private Boolean isCompulsory;
  
  @JsonIgnore
  @ManyToMany(mappedBy = "lessons", cascade = CascadeType.REMOVE)
  private Set<LessonProgram> lessonPrograms;
  

}
