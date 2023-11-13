package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.LessonRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.LessonResponse;
import com.project.schoolmanagment.repository.business.LessonRepository;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

	private final LessonRepository lessonRepository;
	private final LessonMapper lessonMapper;
	private final PageableHelper pageableHelper;

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

	public Lesson isLessonExistById(Long id){
		return lessonRepository.findById(id).orElseThrow(()->
				new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id)));
	}


	public ResponseMessage deleteLessonById(Long id) {
		isLessonExistById(id);
		lessonRepository.deleteById(id);
		return ResponseMessage.builder()
				.message(SuccessMessages.LESSON_DELETE)
				.httpStatus(HttpStatus.OK)
				.build();
	}

	public ResponseMessage<LessonResponse> getLessonByName(String lessonName) {
		if(lessonRepository.getLessonByLessonName(lessonName).isPresent()){
			Lesson lesson = lessonRepository.getLessonByLessonName(lessonName).get();
			return ResponseMessage.<LessonResponse>builder()
					.message(SuccessMessages.LESSON_FOUND)
					.object(lessonMapper.mapLessonToLessonResponse(lesson))
					.build();
		} else {
			return ResponseMessage.<LessonResponse>builder()
					.message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,lessonName))
					.build();
		}
	}

	public Page<LessonResponse> getLessonByPage(int page, int size, String sort, String type) {
		Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
		return lessonRepository.findAll(pageable)
				.map(lessonMapper::mapLessonToLessonResponse);
	}

	public Set<Lesson> getAllLessonByLessonId(Set<Long> idSet) {
		return idSet.stream()
				.map(this::isLessonExistById)
				.collect(Collectors.toSet());
	}

	public LessonResponse updateLesson(Long lessonId, LessonRequest lessonRequest) {
		//validation-1 is this lesson really exist
		Lesson lesson = isLessonExistById(lessonId);
		//validation-2 if you are updating the name, is this exist in DB
		//step-1 are you really changing the name of the lesson
		//step-2 if step-1 is the case, is this lesson name exist in DB
		if(!lesson.getLessonName().equals(lessonRequest.getLessonName())
		&& lessonRepository.existsByLessonNameEqualsIgnoreCase(lessonRequest.getLessonName())){
			throw new ConflictException(
					String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonRequest.getLessonName())
			);
		}
		Lesson updatedLesson = lessonMapper.mapLessonRequestToLesson(lessonRequest);
		//lesson programs is not suitable for put them in mapper.
		//because while we are creating a lesson, we are not specifying lesson program.
		updatedLesson.setLessonId(lesson.getLessonId());
		updatedLesson.setLessonPrograms(lesson.getLessonPrograms());
		Lesson savedLesson = lessonRepository.save(updatedLesson);
		return lessonMapper.mapLessonToLessonResponse(savedLesson);
	}

    public ResponseMessage<LessonResponse> getLessonById(Long id) {

		Lesson lesson  = isLessonExistById(id);

		return ResponseMessage.<LessonResponse>builder()
				.object(lessonMapper.mapLessonToLessonResponse(lesson))
				.message(SuccessMessages.LESSON_FOUND)
				.build();

    }


	public List<LessonResponse> getAllLessons() {
		List<Lesson> lessons = lessonRepository.findAll();

		if(lessons.isEmpty()){
			throw new ResourceNotFoundException(ErrorMessages.NO_LESSONS);
		}

		return lessons.stream()
				.map(lessonMapper::mapLessonToLessonResponse)
				.collect(Collectors.toList());
	}
}
