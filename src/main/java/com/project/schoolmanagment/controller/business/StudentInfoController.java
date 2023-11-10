package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.StudentInfoResponse;
import com.project.schoolmanagment.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

	private final StudentInfoService studentInfoService;

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('TEACHER')")
	public ResponseMessage<StudentInfoResponse>saveStudentInfo(HttpServletRequest request,
	                                                           @RequestBody @Valid StudentInfoRequest studentInfoRequest){
			return studentInfoService.saveStudentInfo(request,studentInfoRequest);
	}

	@DeleteMapping("/delete/{studentInfoId}")
	@PreAuthorize("hasAnyAuthority('ADMIN','TEACHER')")
	public ResponseMessage delete (@PathVariable Long studentInfoId){
		return studentInfoService.deleteById(studentInfoId);
	}


	@GetMapping("/getAllStudentInfoByPage")
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER','ASISTANT_MANAGER')")
	public Page<StudentInfoResponse> getStudentInfoByPage(
			@RequestParam(value = "page") int page,
			@RequestParam(value = "size") int size,
			@RequestParam(value = "sort") String sort,
			@RequestParam(value = "type") String type){
		return studentInfoService.getStudentInfoByPage(page,size,sort,type);
	}





}
