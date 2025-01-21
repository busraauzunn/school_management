package com.project.schoolmanagment.service.businnes;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.exception.BadRequestException;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.EducationTermMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.businnes.EducationTermRequest;
import com.project.schoolmanagment.payload.response.businnes.EducationTermResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.repository.businnes.EducationTermRepository;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationTermService {
  
  private final EducationTermRepository educationTermRepository;
  
  private final EducationTermMapper educationTermMapper;
  
  private final PageableHelper pageableHelper;

  public ResponseMessage<EducationTermResponse> saveEducationTerm(
      EducationTermRequest educationTermRequest) {
    validateEducationTermDates(educationTermRequest);
    EducationTerm educationTerm = educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest);
    
    EducationTerm savedEducationTerm = educationTermRepository.save(educationTerm);
    return ResponseMessage.<EducationTermResponse>builder()
        .message(SuccessMessages.EDUCATION_TERM_SAVE)
        .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
        .httpStatus(HttpStatus.CREATED)
        .build();
  }
  
  private void  validateEducationTermDates(EducationTermRequest educationTermRequest){
    validateEducationTermsDatesForRequest(educationTermRequest);
    //only one education term can exist in a year
    if(educationTermRepository.existByTermAndYear(
        educationTermRequest.getTerm(),educationTermRequest.getStartDate().getYear())){
          throw new ResourceNotFoundException(ErrorMessages.EDUCATION_TERM_IS_ALREADY_EXIST_BY_TERM_AND_YEAR_MESSAGE);
    }
    
    //validate not to have any conflict with other education terms
    if(educationTermRepository.findByYear(educationTermRequest.getStartDate().getYear())
        .stream()
        .anyMatch(educationTerm ->
            (educationTerm.getStartDate().equals(educationTermRequest.getStartDate()) 
                || (educationTerm.getStartDate().isBefore(educationTermRequest.getStartDate())
                && educationTerm.getEndDate().isAfter(educationTermRequest.getStartDate())) 
                || (educationTerm.getStartDate().isBefore(educationTermRequest.getEndDate())
                && educationTerm.getEndDate().isAfter(educationTermRequest.getEndDate()))
                || (educationTerm.getStartDate().isAfter(educationTermRequest.getStartDate()) 
                && educationTerm.getEndDate().isBefore(educationTermRequest.getEndDate()))))) {
      throw new BadRequestException(ErrorMessages.EDUCATION_TERM_CONFLICT_MESSAGE);
    }
    
  }
  
  private void validateEducationTermsDatesForRequest(EducationTermRequest educationTermRequest){
    //registration > startDate
    if(educationTermRequest.getLastRegistrationDate().isAfter(educationTermRequest.getStartDate())){
      throw new ResourceNotFoundException(
          ErrorMessages.EDUCATION_START_DATE_IS_EARLIER_THAN_LAST_REGISTRATION_DATE
      );
    }    
    // endDate > startDate
    if(educationTermRequest.getEndDate().isBefore(educationTermRequest.getStartDate())){
      throw new ResourceNotFoundException(ErrorMessages.EDUCATION_END_DATE_IS_EARLIER_THAN_START_DATE);
    }
  }


  public EducationTermResponse findById(Long id) {
    EducationTerm educationTerm = isEducationTermExist(id);
    return educationTermMapper.mapEducationTermToEducationTermResponse(educationTerm);    
  }
  
  public EducationTerm isEducationTermExist(Long id){
    return educationTermRepository.findById(id).orElseThrow(()->
        new ResourceNotFoundException(String.format(ErrorMessages.EDUCATION_TERM_NOT_FOUND_MESSAGE,id)));
  }

  public Page<EducationTermResponse> getAllByPage(int page, int size, String sort, String type) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);    
    return educationTermRepository
        .findAll(pageable)
        .map(educationTermMapper::mapEducationTermToEducationTermResponse);
  }

  public ResponseMessage deleteById(Long id) {
    isEducationTermExist(id);
    educationTermRepository.deleteById(id);
    return ResponseMessage.builder()
        .message(SuccessMessages.EDUCATION_TERM_DELETE)
        .httpStatus(HttpStatus.OK)
        .build();
    
  }

  public ResponseMessage<EducationTermResponse> updateEducationTerm(Long id,
      EducationTermRequest educationTermRequest) {
    isEducationTermExist(id);

    validateEducationTermsDatesForRequest(educationTermRequest);
    
    EducationTerm educationTermToSave = educationTermMapper.mapEducationTermRequestToEducationTerm(educationTermRequest);
    educationTermToSave.setId(id);
    
    EducationTerm savedEducationTerm = educationTermRepository.save(educationTermToSave);
    
    return ResponseMessage.<EducationTermResponse>builder()
        .message(SuccessMessages.EDUCATION_TERM_UPDATE)
        .httpStatus(HttpStatus.OK)
        .returnBody(educationTermMapper.mapEducationTermToEducationTermResponse(savedEducationTerm))
        .build();
  }

  public List<EducationTermResponse> getAllEducationTerms() {

    return educationTermRepository
            .findAll()
            .stream()
            .map(educationTermMapper::mapEducationTermToEducationTermResponse)
            .collect(Collectors.toList());
  }
}
