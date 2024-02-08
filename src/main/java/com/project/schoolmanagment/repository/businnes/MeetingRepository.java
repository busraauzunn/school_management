package com.project.schoolmanagment.repository.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meet,Long> {
  
  List<Meet>findByStudentList_IdEquals(Long studentId);
  
  List<Meet>getByAdvisoryTeacher_IdEquals(Long advisoryTeacherId);
  
  Page<Meet>findByAdvisoryTeacher_IdEquals(Long advisoryTeacherId, Pageable pageable);
  
  
  

}
