package com.project.schoolmanagment.service.helper;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ConflictException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.repository.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * The MethodHelper class provides helper methods related to user operations.
 * It contains methods for user existence check, built-in check, and loading user data by username.
 */
@Component
@RequiredArgsConstructor
public class MethodHelper {
  
  public final UserRepository userRepository;  
  
  /**
   * Checks if a user exists in the system based on their user ID.
   *
   * @param userId The ID of the user to check.
   * @return The User object if the user exists.
   * @throws ResourceNotFoundException If the user does not exist.
   */
  public User isUserExist(Long userId){
    return userRepository.findById(userId)
        .orElseThrow(
            ()->new ResourceNotFoundException(String.format(
                ErrorMessages.NOT_FOUND_USER_MESSAGE,userId)));
  }
  
  /**
   * Checks if the user is built-in.
   *
   * @param user The User object to check.
   * @throws BadRequestException If the user is built-in.
   */
  public void checkBuiltIn(User user){
    if(user.getBuiltIn()){
      throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
    }
  }
  
  /**
   * Loads a user by their username.
   *
   * @param username The username of the user to load.
   * @return The User object if the user exists.
   * @throws UsernameNotFoundException If the user does not exist.
   */
  public User loadUserByName(String username){
    User user = userRepository.findByUsername(username);
    if(user==null){
      throw new UsernameNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE_USERNAME, username));
    }
    return user;
  }
  
  public void checkIsAdvisor(User user){
    if(!user.getIsAdvisor()){
      throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_ADVISOR_MESSAGE,user.getId()));
    }
  }
  
  public void checkRole(User user, RoleType roleType){
    if(!user.getUserRole().getRoleType().equals(roleType)){
      throw new ConflictException(ErrorMessages.NOT_HAVE_EXPECTED_ROLE_USER);
    }
  }
  
  
  public List<User>getUserList(List<Long> idList){
    return userRepository.findByIdList(idList);
  }

}
