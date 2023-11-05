package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.LessonResponse;
import com.project.schoolmanagment.repository.business.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;

	/**
	 *
	 * @param lessonRequest DTO to save a lesson
	 * @return response message with the lessonResponse DTO
	 */
	public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
		//only one lesson should exist according to name of the lesson


	}

	/**
	 * exception handler method for lesson name
	 * @param lessonName to search
	 * @return true if lesson does not exist.
	 */
	private boolean isLessonExistByLessonName(String lessonName){
		boolean lessonExist = lessonRepository.existsByLessonNameEqualsIgnoreCase(lessonName);
		if(lessonExist){
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonName));
		} else {
			return true;
		}
	}



}
