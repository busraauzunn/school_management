package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository userRepository;
  private final UniquePropertyValidator uniquePropertyValidator;

  public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {
    //we need a validator for unique props.
    uniquePropertyValidator.checkDuplicate(
        userRequest.getUsername(),
        userRequest.getSsn(),
        userRequest.getPhoneNumber(),
        userRequest.getEmail());    
    return null;
  }
}
