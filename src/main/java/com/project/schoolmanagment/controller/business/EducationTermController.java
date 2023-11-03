package com.project.schoolmanagment.controller.business;

import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.service.business.EducationTermService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/educationTerms")
@RequiredArgsConstructor
public class EducationTermController {

	private final EducationTermService educationTermService;

	@PostMapping("/save")
	@PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
	public ResponseMessage<>saveEducationTerm (){

	}
}
