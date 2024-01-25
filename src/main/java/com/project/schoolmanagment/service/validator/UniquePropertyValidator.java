package com.project.schoolmanagment.service.validator;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.request.abstracts.AbstractUserRequest;
import com.project.schoolmanagment.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {
  
  private final UserRepository userRepository;

  
  public void checkUniqueProperties(User user, AbstractUserRequest userRequest){
    
    String updatedUsername = "";
    String updatedSsn = "";
    String updatedEmail = "";
    String updatedPhone = "";
    boolean isChanged = false;
    
    //we are checking that if we change the unique properties
    if(!user.getUsername().equalsIgnoreCase(userRequest.getUsername())){
      updatedUsername = userRequest.getUsername();
      isChanged = true;
    }
    if (!user.getSsn().equalsIgnoreCase(userRequest.getSsn())) {
      updatedSsn = userRequest.getSsn();
      isChanged = true;
    }
    if (!user.getEmail().equalsIgnoreCase(userRequest.getEmail())) {
      updatedEmail = userRequest.getEmail();
      isChanged = true;
    }
    if (!user.getPhoneNumber().equalsIgnoreCase(userRequest.getPhoneNumber())) {
      updatedPhone = userRequest.getPhoneNumber();
      isChanged = true;
    }    
    if(isChanged){
      checkDuplicate(updatedUsername,updatedSsn,updatedPhone,updatedEmail);
    }       
  } 
  
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
