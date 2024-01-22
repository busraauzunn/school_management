package com.project.schoolmanagment.service.validator;

import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {
  
  private final UserRepository userRepository;
  
  
  public void checkDuplicate(String username, String ssn, String phone, String email){
    //we can check all props in one query
    //userRepository.findByUsernameOrSsnOrPhoneOrEmail(username, ssn, phone, email);
    
    if(userRepository.existsByUsername(username)){
      throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_USERNAME,username));
    }
    if(userRepository.existsBySsn(ssn)){
      throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_SSN,ssn));
    }
    if(userRepository.existsByEmail(email)){
      throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL,email));
    }
    if(userRepository.existsByPhoneNumber(phone)){
      throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE_NUMBER,phone));
    }
    
  }

}
