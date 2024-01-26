package com.project.schoolmanagment.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The UserDetailsImpl class implements the UserDetails interface and represents 
 * an implementation of user details for authentication and authorization purposes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {
  
  private Long id;
  
  private String username;
  
  private String name;
  
  private Boolean isAdvisor;
  
  @JsonIgnore
  private String password;
  
  private String ssn;
  
  private List<GrantedAuthority>authorities;

  /**
   * Initializes a new instance of the UserDetailsImpl class with the specified parameters.
   *
   * @param id the unique identifier of the user
   * @param username the username of the user
   * @param name the name of the user
   * @param isAdvisor a boolean value indicating if the user is an advisor
   * @param password the password of the user
   * @param ssn the social security number of the user
   * @param role the role of the user
   */
  public UserDetailsImpl(Long id, String username, String name, Boolean isAdvisor, String password,
      String ssn, String role) {
    this.id = id;
    this.username = username;
    this.name = name;
    this.isAdvisor = isAdvisor;
    this.password = password;
    this.ssn = ssn;
    List<GrantedAuthority>grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new SimpleGrantedAuthority(role));
    this.authorities = grantedAuthorities;
  }

  /**
   * Returns the authorities granted to the user.
   *
   * @return the authorities granted to the user
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
