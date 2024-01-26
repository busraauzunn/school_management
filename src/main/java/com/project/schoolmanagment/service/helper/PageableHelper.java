package com.project.schoolmanagment.service.helper;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * The PageableHelper class provides helper methods for creating Pageable objects based on the given parameters.
 */
@Component
@RequiredArgsConstructor
public class PageableHelper {
  
  /**
   * Retrieves a Pageable object with the specified properties.
   *
   * @param page The page number to retrieve. Must be greater than or equal to 0.
   * @param size The number of items per page. Must be greater than 0.
   * @param sort The name of the field to sort the results by.
   * @param type The type of sorting. "asc" for ascending, "desc" for descending.
   * @return The created Pageable object.
   */
  public Pageable getPageableWithProperties(int page,
      int size, String sort, String type){
    Pageable pageable = PageRequest.of(page,size, Sort.by(sort).ascending());
    if(Objects.equals(type,"desc")){
      pageable = PageRequest.of(page,size, Sort.by(sort).descending());
    }    
    return pageable;    
  }
  
  
  
  

}
