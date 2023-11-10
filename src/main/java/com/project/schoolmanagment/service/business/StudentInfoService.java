package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.StudentInfoResponse;
import com.project.schoolmanagment.repository.business.StudentInfoRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

	private final StudentInfoRepository studentInfoRepository;
	private final MethodHelper methodHelper;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;

	@Value("${midterm.exam.impact.percentage}")
	private Double midtermExamPercentage;
	@Value("${final.exam.impact.percentage}")
	private Double finalExamPercentage;

	public ResponseMessage<StudentInfoResponse> saveStudentInfo(HttpServletRequest request, StudentInfoRequest studentInfoRequest) {

		String teacherUsername = (String) request.getAttribute("username");
		// get student
		User student = methodHelper.isUserExist(studentInfoRequest.getStudentId());
		// validate user is a student
		methodHelper.checkRole(student, RoleType.STUDENT);
		// get teacher
		User teacher = methodHelper.isUserExistByUsername(teacherUsername);
		// get lesson
		Lesson lesson = lessonService.isLessonExistById(studentInfoRequest.getLessonId());
		//get educationTerm
		EducationTerm educationTerm = educationTermService.isEducationTermExist(studentInfoRequest.getEducationTermId());
		//REQUREMENT -> a student may have only one studentInfo related to one lesson
		isDuplicatedLessonAndInfo(studentInfoRequest.getStudentId(), lesson.getLessonName());
		//calculate the average note and get the suitable note

		Note note = checkLetterGrade(calculateAverageNote(  studentInfoRequest.getMidtermExam(),
															studentInfoRequest.getFinalExam()));

		// map DTO -> domainObject


	}

	private void isDuplicatedLessonAndInfo(Long studentId, String lessonName){
		boolean isLessonDuplicationExist =
				studentInfoRepository.getAllByStudentId_Id(studentId)
						.stream()
						.anyMatch(info->info.getLesson().getLessonName().equalsIgnoreCase(lessonName));
		if(isLessonDuplicationExist){
			throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_LESSON_MESSAGE,lessonName));
		}
	}

	//%40 - %60
	private Double calculateAverageNote(Double midtermExam,Double finalExam){
		return (midtermExam*midtermExamPercentage) + (finalExam*finalExamPercentage);
	}

	private Note checkLetterGrade(Double average){

		if(average<50.0) {
			return Note.FF;
		} else if (average<60) {
			return Note.DD;
		} else if (average<65) {
			return Note.CC;
		} else if (average<70) {
			return  Note.CB;
		} else if (average<75) {
			return  Note.BB;
		} else if (average<80) {
			return Note.BA;
		} else {
			return Note.AA;
		}
	}






}
