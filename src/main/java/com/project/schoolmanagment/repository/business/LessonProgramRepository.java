package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface LessonProgramRepository extends JpaRepository<LessonProgram,Long> {

	//@Query("SELECT LessonProgram  FROM LessonProgram l WHERE l.users = null ")
	List<LessonProgram> findByUsers_IdNull();

	List<LessonProgram>findByUsers_IdNotNull();

	@Query("select l from LessonProgram l where l.id IN :whatEver")
	Set<LessonProgram> getLessonProgramByUsersUsername(Set<Long>whatEver);



}
