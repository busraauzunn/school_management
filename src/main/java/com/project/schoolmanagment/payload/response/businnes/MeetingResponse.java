package com.project.schoolmanagment.payload.response.businnes;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.project.schoolmanagment.entity.concretes.user.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class MeetingResponse {

  private Long id;
  private String description;
  private LocalDate date;
  private LocalTime startTime;
  private LocalTime stopTime;
  private Long advisorTeacherId;
  private String teacherName;
  private String teacherSsn;
  private String username;
  private List<User> students;
  
  
}
