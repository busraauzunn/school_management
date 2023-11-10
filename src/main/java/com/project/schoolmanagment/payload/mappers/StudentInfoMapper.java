package com.project.schoolmanagment.payload.mappers;

import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import org.springframework.stereotype.Component;

@Component
public class StudentInfoMapper {

	public StudentInfo mapStudentInfoRequestToStudentInfo(StudentInfoRequest studentInfoRequest,
	                                                      Note note,
	                                                      Double average){
		return StudentInfo.builder()
				.infoNote(studentInfoRequest.getInfoNote())
				.absentee(studentInfoRequest.getAbsentee())
				.midtermExam(studentInfoRequest.getMidtermExam())
				.finalExam(studentInfoRequest.getFinalExam())
				.examAverage(average)
				.letterGrade(note)
				.build();
	}



}
