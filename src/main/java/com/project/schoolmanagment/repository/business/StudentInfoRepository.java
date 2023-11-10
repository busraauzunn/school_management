package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentInfoRepository extends JpaRepository<StudentInfo,Long> {


	//@Query("select s from StudentInfo s where StudentInfo.student.id = :studentId")
	List<StudentInfo>getAllByStudentId_Id(Long studentId);

	boolean existsByIdEquals(Long id);



}
