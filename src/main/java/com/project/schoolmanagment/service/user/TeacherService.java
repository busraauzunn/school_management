package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.businnes.LessonProgram;
import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.ChooseLessonTeacherRequest;
import com.project.schoolmanagment.payload.request.user.TeacherRequest;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.TeacherResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.business.LessonProgramService;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.DateTimeValidator;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TeacherService {

	private final UserRepository userRepository;
	private final UserRoleService userRoleService;
	private final LessonProgramService lessonProgramService;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final MethodHelper methodHelper;
	private final DateTimeValidator dateTimeValidator;

	public ResponseMessage<TeacherResponse> saveTeacher(TeacherRequest teacherRequest) {
		//validate lesson program set
		Set<LessonProgram>lessonProgramSet = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsProgramIdList());

		//validate unique properties
		uniquePropertyValidator.checkDuplicate(teacherRequest.getUsername(),
												teacherRequest.getSsn(),
												teacherRequest.getPhoneNumber(),
												teacherRequest.getEmail());

		//mapping to domain object
		User teacher = userMapper.mapTeacherRequestToUser(teacherRequest);
		//map missing properties
		teacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
		teacher.setLessonProgramList(lessonProgramSet);
		teacher.setPassword(passwordEncoder.encode(teacher.getPassword()));
		//is advisory teacher
		teacher.setIsAdvisor(teacherRequest.isAdvisorTeacher());
		User savedTeacher = userRepository.save(teacher);
		return ResponseMessage.<TeacherResponse>builder()
				.message(SuccessMessages.TEACHER_SAVE)
				.object(userMapper.mapUserToTeacherResponse(savedTeacher))
				.build();
	}

	public ResponseMessage<TeacherResponse> updateTeacherForManagers(TeacherRequest teacherRequest, Long userId) {
		//validate if this user exist
		User user = methodHelper.isUserExist(userId);
		//validate the role
		methodHelper.checkRole(user,RoleType.TEACHER);
		//new lesson program list
		Set<LessonProgram>lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonsProgramIdList());
		//validate unique properties
		uniquePropertyValidator.checkUniqueProperties(user,teacherRequest);
		//mapping to domain object
		User updatedTeacher = userMapper.mapTeacherRequestToUser(teacherRequest);
		//setting the missing properties
		updatedTeacher.setId(user.getId());
		updatedTeacher.setPassword(passwordEncoder.encode(teacherRequest.getPassword()));
		updatedTeacher.setLessonProgramList(lessonPrograms);
		updatedTeacher.setUserRole(userRoleService.getUserRole(RoleType.TEACHER));
		//saving
		User savedTeacher = userRepository.save(updatedTeacher);
		//returning
		return ResponseMessage.<TeacherResponse>builder()
				.message(SuccessMessages.TEACHER_UPDATE)
				.object(userMapper.mapUserToTeacherResponse(savedTeacher))
				.httpStatus(HttpStatus.OK)
				.build();
	}

	public ResponseMessage<TeacherResponse> addLessonProgram(ChooseLessonTeacherRequest teacherRequest) {
		//validate if teacher really exists
		User teacher = methodHelper.isUserExist(teacherRequest.getTeacherId());
		//validate if this user is really a teacher
		methodHelper.checkRole(teacher,RoleType.TEACHER);
		//the lesson programs that wanted to be updated
		Set<LessonProgram>lessonPrograms = lessonProgramService.getLessonProgramById(teacherRequest.getLessonProgramId());//4,8,9
		Set<LessonProgram>teacherExistingLessonProgram = teacher.getLessonProgramList();//1,2,3
		//just validating the new ones
		dateTimeValidator.checkDuplicateLessonPrograms(lessonPrograms);
		teacherExistingLessonProgram.addAll(lessonPrograms);
		//validating all lesson programs
		dateTimeValidator.checkDuplicateLessonPrograms(teacherExistingLessonProgram);
		//if no validation issue we are setting the teacher's lesson program
		teacher.setLessonProgramList(teacherExistingLessonProgram);

		User updatedTeacher = userRepository.save(teacher);

		return ResponseMessage.<TeacherResponse>builder()
				.message(SuccessMessages.LESSON_PROGRAM_ADD_TO_TEACHER)
				.httpStatus(HttpStatus.OK)
				.object(userMapper.mapUserToTeacherResponse(updatedTeacher))
				.build();
	}
}
