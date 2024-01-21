package com.project.schoolmanagment.entity.concretes.businnes;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.project.schoolmanagment.entity.concretes.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Meet class represents a meeting.
 * It contains information about the meeting, such as the description, date, start time,
 * stop time, advisory teacher, and students attending the meeting.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Meet {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  private String description;
  
  @JsonFormat(shape = Shape.STRING,pattern = "yyyy-MM-dd")
  private LocalDate date;

  @JsonFormat(shape = Shape.STRING,pattern = "HH:mm",timezone = "US")
  private LocalTime startTime;

  @JsonFormat(shape = Shape.STRING,pattern = "HH:mm",timezone = "US")
  private LocalTime stopTime;
  
  @ManyToOne(cascade = CascadeType.PERSIST)
  private User advisoryTeacher;
  
  @ManyToMany
  @JoinTable(
      name = "meet_student_table",
      joinColumns = @JoinColumn(name = "meet_id"),
      inverseJoinColumns = @JoinColumn(name = "student_id")
  )
  private List<User>studentList;
  
  
}
