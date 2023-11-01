package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exeption.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.request.user.UserRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.abstracts.BaseUserResponse;
import com.project.schoolmanagment.payload.response.abstracts.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final UniquePropertyValidator uniquePropertyValidator;
	private final UserMapper userMapper;
	private final UserRoleService userRoleService;
	private final PageableHelper pageableHelper;
	private final MethodHelper methodHelper;

	public ResponseMessage<UserResponse>saveUser(UserRequest userRequest, String userRole){
		//handle uniqueness exceptions
		uniquePropertyValidator.checkDuplicate(
				userRequest.getUsername(),
				userRequest.getSsn(),
				userRequest.getPhoneNumber(),
				userRequest.getEmail());


		//map DTO -> Entity (domain object)
		User user = userMapper.mapUserRequestToUser(userRequest);


		//get correct role from DB and set it to the user
		if(userRole.equalsIgnoreCase(RoleType.ADMIN.name())){
			//we are setting a superUser that can not be deleted
			if(Objects.equals(userRequest.getUsername(),"Admin")){
				user.setBuiltIn(true);
			}
			//give the role of admin to this user
			user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
		} else if(userRole.equalsIgnoreCase("Dean")){
			user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
		} else if (userRole.equalsIgnoreCase("ViceDean")) {
			user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
		} else {
			throw new ResourceNotFoundException(String.format(
					ErrorMessages.NOT_FOUND_USER_USER_ROLE_MESSAGE,userRole));
		}
		user.setIsAdvisor(false);
		User savedUser = userRepository.save(user);
		return ResponseMessage.<UserResponse>builder()
				.message(SuccessMessages.USER_CREATE)
				.object(userMapper.mapUserToUserResponse(savedUser))
				.build();
	}

	/**
	 *
	 * @param userId for query
	 * @return mapped DTO BaseUserResponse
	 */
	public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
		//exception handling in case of not existing user in DB
		User user = userRepository.findById(userId).orElseThrow(()->
				new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,userId)));

		//depends on the user type we need to call correct mappers
		BaseUserResponse baseUserResponse;
		if(user.getUserRole().getRoleType() == RoleType.STUDENT){
			baseUserResponse = userMapper.mapUserToStudentResponse(user);
		} else if (user.getUserRole().getRoleType() == RoleType.TEACHER) {
			baseUserResponse = userMapper.mapUserToTeacherResponse(user);
		} else {
			baseUserResponse = userMapper.mapUserToUserResponse(user);
		}
		return ResponseMessage.<BaseUserResponse>builder()
				.message(SuccessMessages.USER_FOUND)
				.httpStatus(HttpStatus.OK)
				.object(baseUserResponse)
				.build();
	}

	public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type, String userRole) {
		Pageable pageable = pageableHelper.getPageableWithProperties(page,size,sort,type);
		return userRepository.findByUserByRole(userRole,pageable)
				.map(userMapper::mapUserToUserResponse);
	}

	public List<UserResponse> getUserByName(String userName) {
		return userRepository.getUserByNameContaining(userName)
				.stream()
				.map(userMapper::mapUserToUserResponse)
				.collect(Collectors.toList());
	}

	// Normally we should return String not Response entity. Response entity class should be created in controller level.
	public ResponseEntity<String> updateUserForUsers(UserRequestWithoutPassword userRequestWithoutPassword, HttpServletRequest request) {
		String userName = (String) request.getAttribute("username");

		User user = userRepository.findByUsername(userName);

		//we need to check if this user can be changed
		methodHelper.isUserBuiltIn(user);
		//we need to check are we changing the unique properties
		uniquePropertyValidator.checkUniqueProperties(user,userRequestWithoutPassword);

		//implementation without using mapper builders
		user.setUsername(userRequestWithoutPassword.getUsername());
		user.setBirthDay(userRequestWithoutPassword.getBirthDay());
		user.setEmail(userRequestWithoutPassword.getEmail());
		user.setPhoneNumber(userRequestWithoutPassword.getPhoneNumber());
		user.setBirthPlace(userRequestWithoutPassword.getBirthPlace());
		user.setGender(userRequestWithoutPassword.getGender());
		user.setName(userRequestWithoutPassword.getName());
		user.setSurname(userRequestWithoutPassword.getSurname());
		user.setSsn(userRequestWithoutPassword.getSsn());
		userRepository.save(user);
		String message = SuccessMessages.USER_UPDATE;
		return  ResponseEntity.ok(message);
	}
}
