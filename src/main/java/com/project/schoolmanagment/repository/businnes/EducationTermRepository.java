package com.project.schoolmanagment.repository.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.enums.Term;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface EducationTermRepository extends JpaRepository<EducationTerm,Long> {
  
  
  @Query("SELECT (count (e) > 0) FROM EducationTerm e WHERE e.term=?1 and extract(YEAR FROM e.startDate) = ?2 ")
  boolean existByTermAndYear(Term term, int year);
  
  
  //find all education terms in a year
  @Query("SELECT e FROM EducationTerm e WHERE extract(YEAR FROM e.startDate) = ?1 ")
  List<EducationTerm>findByYear(int year);

}




