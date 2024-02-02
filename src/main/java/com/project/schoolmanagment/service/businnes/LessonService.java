package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.LessonRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonService {
  
  private final LessonRepository lessonRepository;
  public final LessonMapper lessonMapper;

  public ResponseMessage<LessonResponse> saveLesson(LessonRequest lessonRequest) {
    //lessons must be unique
    isLessonExistByLessonName(lessonRequest.getLessonName());
    // map DTO -> entity
    Lesson lesson = lessonMapper.mapLessonRequestToLesson(lessonRequest);
    Lesson savedLesson = lessonRepository.save(lesson);
    
    return ResponseMessage.<LessonResponse>builder()
        .returnBody(lessonMapper.mapLessonToLessonResponse(savedLesson))
        .message(SuccessMessages.LESSON_SAVE)
        .httpStatus(HttpStatus.CREATED)
        .build();
  }
  
  private void isLessonExistByLessonName(String lessonName){ 
    if(lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).isPresent()){
      throw new ResourceNotFoundException(String.format(ErrorMessages.ALREADY_CREATED_LESSON_MESSAGE,lessonName));
    }       
  }
  
  private Lesson isLessonExistById(Long id){
    return lessonRepository.findById(id).orElseThrow(()->
        new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,id)));
  }

  public ResponseMessage deleteById(Long id) {
    isLessonExistById(id);
    lessonRepository.deleteById(id);
    return ResponseMessage.builder()
        .message(SuccessMessages.LESSON_DELETE)
        .httpStatus(HttpStatus.OK)
        .build();    
  }

  public ResponseMessage<LessonResponse> getLessonByLessonName(String lessonName) {
    if(lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).isPresent()){
      return ResponseMessage.<LessonResponse>builder()
          .message(SuccessMessages.LESSON_FOUND)
          .returnBody(lessonMapper.mapLessonToLessonResponse(lessonRepository.getByLessonNameEqualsIgnoreCase(lessonName).get()))
          .build();
    } else {
      return ResponseMessage.<LessonResponse>builder()
          .message(String.format(ErrorMessages.NOT_FOUND_LESSON_MESSAGE,lessonName))
          .httpStatus(HttpStatus.NOT_FOUND)
          .build();
    }
  }
}
