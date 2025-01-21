package com.project.schoolmanagment.controller.businnes;

import com.project.schoolmanagment.payload.request.businnes.LessonProgramRequest;
import com.project.schoolmanagment.payload.response.businnes.LessonProgramResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.service.businnes.LessonProgramService;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lessonPrograms")
@RequiredArgsConstructor
public class LessonProgramController {
  
  private final LessonProgramService lessonProgramService;

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @PostMapping("/save")
  public ResponseMessage<LessonProgramResponse>saveLessonProgram(
      @RequestBody @Valid LessonProgramRequest lessonProgramRequest){
    return lessonProgramService.saveLessonProgram(lessonProgramRequest);
  }


  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
  @GetMapping("/getAll")
  public List<LessonProgramResponse>getAllLessonProgramByList(){
    return lessonProgramService.getAll();
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
  @GetMapping("/getAllUnassigned")
  public List<LessonProgramResponse>getAllUnassigned(){
    return lessonProgramService.getAllUnassigned();
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
  @GetMapping("/getAllAssigned")
  public List<LessonProgramResponse>getAllAssigned(){
    return lessonProgramService.getAllAssigned();
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @GetMapping("/getById/{id}")
  public LessonProgramResponse getById(@PathVariable Long id){
    return lessonProgramService.getLessonProgramById(id);
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @DeleteMapping("/delete/{id}")
  public ResponseMessage deleteById(@PathVariable Long id){
    return lessonProgramService.deleteById(id);
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Student','Teacher')")
  @GetMapping("/findLessonProgramByPage")
  public Page<LessonProgramResponse> findLessonProgramByPage(
      @RequestParam(value = "page",defaultValue = "0") int page,
      @RequestParam(value = "size",defaultValue = "10") int size,
      @RequestParam(value = "sort",defaultValue = "day") String sort,
      @RequestParam(value = "type",defaultValue = "desc") String type){
    return lessonProgramService.findLessonProgramByPage(page,size,sort,type);
  }

 // @PreAuthorize("hasAnyAuthority('Teacher')")
  //@GetMapping("/getAllLessonProgramByTeacher")
  //public Set<LessonProgramResponse>getAllLessonProgramByTeacherUsername(HttpServletRequest httpServletRequest){
   // return lessonProgramService.getAllLessonProgramByUsername(httpServletRequest );
  //}

  //@PreAuthorize("hasAnyAuthority('Student')")
  //@GetMapping("/getAllLessonProgramByStudent")
  //public Set<LessonProgramResponse>getAllLessonProgramByStudent(HttpServletRequest httpServletRequest){
  //  return lessonProgramService.getAllLessonProgramByUsername(httpServletRequest);
  //}

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @GetMapping("/getAllLessonProgramByTeacherId/{teacherId}")
  public Set<LessonProgramResponse>getAllByTeacherId(@PathVariable Long teacherId){
    return lessonProgramService.getAllByTeacherId(teacherId);
  }

  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean')")
  @GetMapping("/getAllLessonProgramByStudentId/{student}")
  public Set<LessonProgramResponse>getAllByStudentId(@PathVariable Long student){
    return lessonProgramService.getAllByStudentId(student);
  }
  
  
  
  

}
