package com.project.schoolmanagment.entity.concretes.user;

import com.project.schoolmanagment.entity.enums.RoleType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The UserRole class represents a role assigned to a user in the system.
 * It contains information about the role, such as the ID, role type, and role name.
 */
@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRole {  
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Enumerated(EnumType.STRING)
  private RoleType roleType;
  
  private String roleName;

}
