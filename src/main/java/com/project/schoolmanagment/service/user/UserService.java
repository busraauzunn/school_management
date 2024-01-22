package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository userRepository;

  public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {
    //we need a validator for unique props.
    return null;
  }
}
