package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.payload.request.abstracts.BaseUserRequest;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
  
  private final PasswordEncoder passwordEncoder;
  
  public User mapUserRequestToUser(BaseUserRequest userRequest){
    return User.builder()
        .username(userRequest.getUsername())
        .name(userRequest.getName())
        .surname(userRequest.getSurname())
        .password(passwordEncoder.encode(userRequest.getPassword()))
        .ssn(userRequest.getSsn())
        .birthDay(userRequest.getBirthDay())
        .birthPlace(userRequest.getBirthPlace())
        .phoneNumber(userRequest.getPhoneNumber())
        .gender(userRequest.getGender())
        .email(userRequest.getEmail())
        .builtIn(userRequest.getBuildIn())
        //since we will not save teacher with these ent-point
        .isAdvisor(false)
        .build();
  }
  
  public UserResponse mapUserToUserResponse(User user){
    return UserResponse.builder()
        .userId(user.getId())
        .username(user.getUsername())
        .name(user.getName())
        .surname(user.getSurname())
        .phoneNumber(user.getPhoneNumber())
        .gender(user.getGender())
        .birthDay(user.getBirthDay())
        .birthPlace(user.getBirthPlace())
        .ssn(user.getSsn())
        .email(user.getEmail())
        .userRole(user.getUserRole().getRoleType().name())
        .build();
  }
  
  
  
  

}
