package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.repository.user.UserRoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The UserRoleService class provides methods for interacting with the UserRole entities.
 */
@Service
@RequiredArgsConstructor
public class UserRoleService {
  
  private final UserRoleRepository userRoleRepository;
  
  /**
   * Retrieves the user role based on the given role type.
   *
   * @param roleType the type of the role
   * @return the user role with the specified role type
   * @throws ResourceNotFoundException if the role is not found
   */
  public UserRole getUserRole( RoleType roleType){
    return userRoleRepository.findByEnumRoleEquals(roleType)
        .orElseThrow(
            ()-> new ResourceNotFoundException(ErrorMessages.ROLE_NOT_FOUND)
        );
  }
  
  public List<UserRole>getAllUserRole(){
    return userRoleRepository.findAll();
  }
  
  

}
