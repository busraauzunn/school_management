package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.service.business.StudentInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/studentInfo")
@RequiredArgsConstructor
public class StudentInfoController {

	private final StudentInfoService studentInfoService;

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('TEACHER')")
	public ResponseMessage<>saveStudentInfo(){

	}

}
