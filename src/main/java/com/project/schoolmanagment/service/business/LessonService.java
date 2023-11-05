package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.payload.mappers.LessonMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.LessonResponse;
import com.project.schoolmanagment.repository.business.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final LessonMapper lessonMapper;

	/**
	 *
	 * @param lessonRequest DTO to save a lesson
	 * @return response message with the lessonResponse DTO
	 */
	public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
		//only one lesson should exist according to name of the lesson
		isLessonExistByLessonName(lessonRequest.getLessonName());
		Lesson savedLesson = lessonRepository.save(lessonMapper.mapLessonRequestToLesson(lessonRequest));
		return ResponseMessage.<LessonResponse>builder()
				.object(lessonMapper.mapLessonToLessonResponse(savedLesson))
				.message(SuccessMessages.LESSON_SAVE)
				.httpStatus(HttpStatus.CREATED)
				.build();
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


	public ResponseMessage deleteLessonById(Long id) {
		return null;
	}
}
