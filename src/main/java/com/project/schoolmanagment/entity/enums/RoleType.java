package com.project.schoolmanagment.entity.enums;

import lombok.Getter;

/**
 * The RoleType enum represents the different types of roles available in the system.
 * Each role type has a unique name associated with it.
 */
@Getter
public enum RoleType {
  
  ADMIN("Admin"),
  TEACHER("Teacher"),
  STUDENT("Student"),
  MANAGER("Dean"),
  ASSISTANT_MANAGER("ViceDean");
  
  public final String name;

  RoleType(String name) {
    this.name = name;
  }
}
