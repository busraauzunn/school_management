package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentInfoRepository extends JpaRepository<StudentInfo,Long> {





}
