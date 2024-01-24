package com.project.schoolmanagment.repository.user;

import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.RoleType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
  
  @Query("SELECT r FROM UserRole r WHERE r.roleType = ?1")
  Optional<UserRole> findByEnumRoleEquals(RoleType roleType);

}
