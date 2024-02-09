package com.project.schoolmanagment.repository.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInfoRepository extends JpaRepository<StudentInfo,Long> {
  
  
  @Query("select (count (s)>0) from StudentInfo s where s.student.id= ?1 and s.lesson.lessonName = ?2")
  boolean giveMeDuplications(Long studentId,String lessonName);
  
  
  @Query("SELECT s from StudentInfo s where s.student.id = ?1")
  List<StudentInfo>findByStudentId(Long studentId);

}
