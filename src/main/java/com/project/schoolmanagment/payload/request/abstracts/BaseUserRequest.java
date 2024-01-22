package com.project.schoolmanagment.payload.request.abstracts;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserRequest extends AbstractUserRequest{

  @NotNull(message = "Please enter your password")
  @Size(min = 8,max = 60,message = "Your password should be at least 8 chars and maximum 60 chars")
  private String password;
  
  private Boolean buildIn = false;
  
  
}
