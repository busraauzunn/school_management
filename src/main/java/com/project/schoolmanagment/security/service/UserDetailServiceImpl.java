package com.project.schoolmanagment.security.service;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The UserDetailServiceImpl class implements the UserDetailsService interface and is responsible
 * for loading user details by username.
 */
@Service
@AllArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
  
  private final MethodHelper methodHelper;

  /**
   * Loads user details by username.
   *
   * @param username the username of the user
   * @return the UserDetails object with user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user = methodHelper.loadUserByName(username);
    
    return new UserDetailsImpl(
        user.getId(),
        user.getUsername(), 
        user.getName(),
        user.getIsAdvisor(),
        user.getPassword(),
        user.getSsn(),
        user.getUserRole().getRoleType().getName());
  }
}
