package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.payload.mappers.LessonProgramMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonProgramResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.LessonProgramRepository;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LessonProgramService {
  
  private final LessonProgramRepository lessonProgramRepository;
  private final LessonService lessonService;
  private final EducationTermService educationTermService;
  private final DateTimeValidator dateTimeValidator;
  private final LessonProgramMapper lessonProgramMapper;

  public ResponseMessage<LessonProgramResponse> saveLessonProgram(
      LessonProgramRequest lessonProgramRequest) {
    //get lessons from lesson service
    Set<Lesson> lessons = lessonService.getLessonByIdSet(lessonProgramRequest.getLessonIdList());
    //get education term from education term service
    EducationTerm educationTerm = educationTermService.isEducationTermExist(
        lessonProgramRequest.getEducationTermId());
    
    //validate start and stopTime
    dateTimeValidator.checkTimeWithException(lessonProgramRequest.getStartTime(),
        lessonProgramRequest.getStopTime());
    LessonProgram lessonProgram = 
        lessonProgramMapper.mapLessonProgramRequestToLessonProgram(
            lessonProgramRequest,lessons,educationTerm);
    
    LessonProgram savedLessonProgram = lessonProgramRepository.save(lessonProgram);
    return ResponseMessage.<LessonProgramResponse>builder()
        .message(SuccessMessages.LESSON_PROGRAM_SAVE)
        .returnBody(lessonProgramMapper.mapLessonProgramToLessonProgramResponse(savedLessonProgram))
        .httpStatus(HttpStatus.CREATED)
        .build();
  }

  public List<LessonProgramResponse> getAll() {    
    return lessonProgramRepository.findAll()
        .stream()
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
        .collect(Collectors.toList());
  }
}
