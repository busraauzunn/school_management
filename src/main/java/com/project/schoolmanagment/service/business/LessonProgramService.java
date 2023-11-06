package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.repository.business.LessonProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

	private final LessonProgramRepository lessonProgramRepository;
}
