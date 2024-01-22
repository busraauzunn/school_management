package com.project.schoolmanagment.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.enums.Gender;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The User class represents a user in the system.
 * It contains information about the user, such as their username,
 * social security number, name, surname, date of birth, birth place,
 * password, phone number, email, built-in status, mother's name,
 * father's name, student number, active status, advisor status,
 * advisor teacher ID, gender, user role, lesson programs and meeting attendance.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_user")
public class User {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;  
  
  @Column(unique = true)
  private String username;
  
  @Column(unique = true)
  private String ssn;
  
  private String name;
  
  private String surname;
  
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate birthDay;
  
  private String birthPlace;
  
  @JsonProperty(access = Access.WRITE_ONLY)
  private String password;

  @Column(unique = true)
  private String phoneNumber;

  @Column(unique = true)
  private String email;
  
  private Boolean builtIn;
  
  private String motherName;
  
  private String fatherName;
  
  private int studentNumber;
  
  private boolean isActive;
  
  private Boolean isAdvisor;
  
  private Long advisorTeacherId;
  
  @Enumerated(EnumType.STRING)
  private Gender gender;  
  
  @OneToOne
  @JsonProperty(access = Access.WRITE_ONLY)
  private UserRole userRole;
  
  @OneToMany(mappedBy = "teacher",cascade = CascadeType.REMOVE)
  private List<StudentInfo>studentInfos;
  
  @ManyToMany
  @JoinTable(name = "user_lessonProgram",
              joinColumns = @JoinColumn(name = "user_id"),
              inverseJoinColumns = @JoinColumn(name = "lesson_program_id"))   
  private Set<LessonProgram>lessonProgramList;
  
  
  @JsonIgnore
  @ManyToMany(mappedBy = "studentList")
  private List<Meet>meetList;
  
  

}
