package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.LessonProgramMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonProgramResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.LessonProgramRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  private final PageableHelper pageableHelper;
  private final MethodHelper methodHelper;

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

  public List<LessonProgramResponse> getAllUnassigned() {    
    return lessonProgramRepository.findByUsers_IdNull()
        .stream()
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
        .collect(Collectors.toList());    
  }

  public List<LessonProgramResponse> getAllAssigned() {
    return lessonProgramRepository.findByUsers_IdNotNull()
        .stream()
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
        .collect(Collectors.toList());
  }

  public LessonProgramResponse getLessonProgramById(Long id) {
    return lessonProgramMapper.mapLessonProgramToLessonProgramResponse(isLessonProgramExist(id));
  }
  
  
  private LessonProgram isLessonProgramExist(Long id){
    return lessonProgramRepository.findById(id)
        .orElseThrow(()->
            new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE,id)));
  }


  public ResponseMessage deleteById(Long id) {
    isLessonProgramExist(id);
    lessonProgramRepository.deleteById(id);    
    return ResponseMessage.builder()
        .message(SuccessMessages.LESSON_PROGRAM_DELETE)
        .httpStatus(HttpStatus.OK)
        .build();    
  }

  public Page<LessonProgramResponse> findLessonProgramByPage(int page, int size, String sort, String type) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);    
    return lessonProgramRepository
        .findAll(pageable)
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse);
  }
  
  
  public Set<LessonProgram>getLessonProgramById(Set<Long>lessonIdSet){
    Set<LessonProgram>lessonProgramSet = lessonProgramRepository.getLessonProgramByIdList(lessonIdSet);
    if(lessonProgramSet.isEmpty()){
      throw new BadRequestException(ErrorMessages.NOT_FOUND_LESSON_PROGRAM_MESSAGE_WITHOUT_ID_INFO);
    }
    return lessonProgramSet;
  }

  public Set<LessonProgramResponse> getAllLessonProgramByTeacherUsername(
      HttpServletRequest httpServletRequest) {
    String username = (String) httpServletRequest.getAttribute("username");
    return lessonProgramRepository.getLessonProgramByUsername(username)
        .stream()
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
        .collect(Collectors.toSet());    
  }

  public Set<LessonProgramResponse> getAllByTeacherId(Long teacherId) {
    //check if user exist
    User teacher = methodHelper.isUserExist(teacherId);
    //check if this ID is a teacher
    methodHelper.checkRole(teacher, RoleType.TEACHER);
    
    return lessonProgramRepository.findByUsers_IdEquals(teacherId)
        .stream()
        .map(lessonProgramMapper::mapLessonProgramToLessonProgramResponse)
        .collect(Collectors.toSet());
  }
}
