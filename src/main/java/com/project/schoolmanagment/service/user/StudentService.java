package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.concretes.user.UserRole;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.StudentRequest;
import com.project.schoolmanagment.payload.request.user.StudentRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.StudentResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class StudentService {

	private final UserRepository userRepository;
	private final MethodHelper methodHelper;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final UserRoleService userRoleService;

	public ResponseMessage<StudentResponse> saveStudent(StudentRequest studentRequest) {
		//check DB is this user (advisor teacher) really exist
		User advisorTeacher = methodHelper.isUserExist(studentRequest.getAdvisorTeacherId());
		//check DB is this user really an advisor teacher
		methodHelper.checkAdvisor(advisorTeacher);
		//validate unique props.
		uniquePropertyValidator.checkDuplicate(studentRequest.getUsername(),
												studentRequest.getSsn(),
												studentRequest.getPhoneNumber(),
												studentRequest.getEmail());
		//mapping to domain entity
		User student = userMapper.mapStudentRequestToUser(studentRequest);
		student.setAdvisorTeacherId(advisorTeacher.getAdvisorTeacherId());
		student.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
		student.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
		student.setActive(true);
		student.setIsAdvisor(false);
		//student numbers start with 1000
		//each student has his/her own number
		student.setStudentNumber(getLastNumber());

		return ResponseMessage.<StudentResponse>builder()
				.object(userMapper.mapUserToStudentResponse(userRepository.save(student)))
				.message(SuccessMessages.STUDENT_SAVE)
				.build();
	}

	private int getLastNumber(){
		if(!userRepository.findUsersByRole(RoleType.STUDENT)){
			//in case of first student
			return 1000;
		}
		return userRepository.getMaxStudentNumber() + 1;
	}


	public ResponseEntity<String> updateStudentWithoutPassword(StudentRequestWithoutPassword studentRequestWithoutPassword, HttpServletRequest request) {
		String userName = (String) request.getAttribute("username");
		//fetch user information from DB
		User student = userRepository.findByUsername(userName);
		//validate props for uniqueness
		uniquePropertyValidator.checkUniqueProperties(student,studentRequestWithoutPassword);
		//ordinary way of mapping
		student.setUsername(studentRequestWithoutPassword.getUsername());
		student.setMotherName(studentRequestWithoutPassword.getMotherName());
		student.setFatherName(studentRequestWithoutPassword.getFatherName());
		student.setBirthDay(studentRequestWithoutPassword.getBirthDay());
		student.setEmail(studentRequestWithoutPassword.getEmail());
		student.setPhoneNumber(studentRequestWithoutPassword.getPhoneNumber());
		student.setBirthPlace(studentRequestWithoutPassword.getBirthPlace());
		student.setGender(studentRequestWithoutPassword.getGender());
		student.setName(studentRequestWithoutPassword.getName());
		student.setSurname(studentRequestWithoutPassword.getSurname());
		student.setSsn(studentRequestWithoutPassword.getSsn());

		userRepository.save(student);
		return ResponseEntity.ok(SuccessMessages.STUDENT_UPDATE);
	}

	public ResponseMessage<StudentResponse> updateStudentForManagers(Long id, StudentRequest studentRequest) {
		//validate if we have this id in DB
		User student = methodHelper.isUserExist(id);
		//validate if it is really a student
		methodHelper.checkRole(student,RoleType.STUDENT);
		//validate unique properties
		uniquePropertyValidator.checkUniqueProperties(student,studentRequest);
		//map DTO to user
		User studentFromMapper = userMapper.mapStudentRequestToUpdatedUser(studentRequest,id);
		//mapping the rest of the properties
		studentFromMapper.setPassword(passwordEncoder.encode(studentRequest.getPassword()));
		studentFromMapper.setAdvisorTeacherId(studentRequest.getAdvisorTeacherId());
		//we do not let the user to update student number as we do not have this prop in studentRequest DTO
		studentFromMapper.setStudentNumber(student.getStudentNumber());
		studentFromMapper.setUserRole(userRoleService.getUserRole(RoleType.STUDENT));
		studentFromMapper.setActive(true);

		return ResponseMessage.<StudentResponse>builder()
				.object(userMapper.mapUserToStudentResponse(userRepository.save(studentFromMapper)))
				.message(SuccessMessages.STUDENT_UPDATE)
				.httpStatus(HttpStatus.OK)
				.build();
	}
}
