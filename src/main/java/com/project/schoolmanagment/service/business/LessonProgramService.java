package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonProgramMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.LessonProgramResponse;
import com.project.schoolmanagment.repository.business.LessonProgramRepository;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class LessonProgramService {

	private final LessonProgramRepository lessonProgramRepository;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final DateTimeValidator dateTimeValidator;
	private final LessonProgramMapper lessonProgramMapper;

	public ResponseMessage<LessonProgramResponse> saveLessonProgram(LessonProgramRequest lessonProgramRequest) {
		//validate if these lessonId.s really exist
		Set<Lesson>lessons = lessonService.getAllLessonByLessonId(lessonProgramRequest.getLessonIdList());
		//validate if this education term exist
		EducationTerm educationTerm = educationTermService.isEducationTermExist(lessonProgramRequest.getEducationTermId());

		if(lessons.isEmpty()){
			throw new ResourceNotFoundException(ErrorMessages.NOT_FOUND_LESSON_IN_LIST);
		}
		//validation of start-time and stop-time
		dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(),
												lessonProgramRequest.getStopTime());
		//mapping
		LessonProgram lessonProgram = lessonProgramMapper.mapLessonProgramRequestToLessonProgram(lessonProgramRequest,lessons,educationTerm);
		LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
		return ResponseMessage.<LessonProgramResponse>builder()
				.message(SuccessMessages.LESSON_PROGRAM_SAVE)
				.object(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
				.httpStatus(HttpStatus.CREATED)
				.build();
	}
}
