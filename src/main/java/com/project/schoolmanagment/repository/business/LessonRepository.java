package com.project.schoolmanagment.repository.business;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson,Long> {

	boolean existsByLessonNameEqualsIgnoreCase(String lessonName);
}
