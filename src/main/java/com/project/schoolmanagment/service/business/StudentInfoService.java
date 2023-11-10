package com.project.schoolmanagment.service.business;

import com.project.schoolmanagment.entity.concretes.businnes.EducationTerm;
import com.project.schoolmanagment.entity.concretes.businnes.Lesson;
import com.project.schoolmanagment.entity.concretes.businnes.StudentInfo;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.Note;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exeption.ConflictException;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.StudentInfoMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.business.StudentInfoRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.business.StudentInfoResponse;
import com.project.schoolmanagment.repository.business.StudentInfoRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentInfoService {

	private final StudentInfoRepository studentInfoRepository;
	private final MethodHelper methodHelper;
	private final LessonService lessonService;
	private final EducationTermService educationTermService;
	private final StudentInfoMapper studentInfoMapper;
	private final PageableHelper pageableHelper;

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
		Double averageNote = calculateAverageNote(  studentInfoRequest.getMidtermExam(),
				studentInfoRequest.getFinalExam());

		Note note = checkLetterGrade(averageNote);

		// map DTO -> domainObject
		StudentInfo studentInfo = studentInfoMapper.mapStudentInfoRequestToStudentInfo(
				studentInfoRequest,
				note,
				averageNote);
		//set missing properties
		studentInfo.setStudent(student);
		studentInfo.setEducationTerm(educationTerm);
		studentInfo.setTeacher(teacher);
		studentInfo.setLesson(lesson);
		StudentInfo savedStudentInfo = studentInfoRepository.save(studentInfo);
		return ResponseMessage.<StudentInfoResponse>builder()
				.message(SuccessMessages.STUDENT_INFO_SAVE)
				.object(studentInfoMapper.mapStudentInfoToStudentInfoResponse(savedStudentInfo))
				.httpStatus(HttpStatus.OK)
				.build();
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


	public ResponseMessage deleteById(Long studentInfoId) {
		//validate if studentInfoExist
		StudentInfo studentInfo = isStudentInfoExist(studentInfoId);
		studentInfoRepository.deleteById(studentInfoId);
		return ResponseMessage.builder()
				.message(SuccessMessages.STUDENT_INFO_DELETE)
				.httpStatus(HttpStatus.OK)
				.build();
	}


	//practising purposes, we have added one more query which is in reality not needed at all
	public StudentInfo isStudentInfoExist(Long id){
		boolean isExist = studentInfoRepository.existsByIdEquals(id);
		if(!isExist){
			throw new ResourceNotFoundException(String.format(ErrorMessages.STUDENT_INFO_NOT_FOUND,id));
		} else {
			return studentInfoRepository.findById(id).get();
		}
	}

	public Page<StudentInfoResponse> getStudentInfoByPage(int page, int size, String sort, String type) {
		Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
		return studentInfoRepository
				.findAll(pageable)
				.map(studentInfoMapper::mapStudentInfoToStudentInfoResponse);
	}
}
