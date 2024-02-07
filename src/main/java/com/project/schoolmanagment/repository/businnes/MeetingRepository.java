package com.project.schoolmanagment.repository.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Meet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingRepository extends JpaRepository<Meet,Long> {

}
