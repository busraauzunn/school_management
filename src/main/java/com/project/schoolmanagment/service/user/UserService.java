package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.ResourceNotFoundException;
import com.project.schoolmanagment.payload.mappers.UserMapper;
import com.project.schoolmanagment.payload.messages.ErrorMessages;
import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.request.user.UserRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.abstracts.BaseUserResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.repository.user.UserRepository;
import com.project.schoolmanagment.service.helper.MethodHelper;
import com.project.schoolmanagment.service.helper.PageableHelper;
import com.project.schoolmanagment.service.validator.UniquePropertyValidator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  
  private final UserRepository userRepository;
  private final UniquePropertyValidator uniquePropertyValidator;
  private final UserMapper userMapper;
  private final UserRoleService userRoleService;
  private final PageableHelper pageableHelper;
  private final MethodHelper methodHelper;

  public ResponseMessage<UserResponse> saveUser(UserRequest userRequest, String userRole) {
    //we need a validator for unique props.
    uniquePropertyValidator.checkDuplicate(
        userRequest.getUsername(),
        userRequest.getSsn(),
        userRequest.getPhoneNumber(),
        userRequest.getEmail());    
    //we need to map DTO -> entity
    User user = userMapper.mapUserRequestToUser(userRequest);
    //analise the role and set it to the entity
    if(userRole.equalsIgnoreCase(RoleType.ADMIN.getName())){
      //if username is Admin then we set this user buildIn -> TRUE
      if(Objects.equals(userRequest.getUsername(),"Admin")){
        user.setBuiltIn(true);
      }
      //since role information is kept in another table, 
      // we need to have another repo and service to call the role
      user.setUserRole(userRoleService.getUserRole(RoleType.ADMIN));
    } else if (userRole.equalsIgnoreCase("Dean")) {
      user.setUserRole(userRoleService.getUserRole(RoleType.MANAGER));
    } else if (userRole.equalsIgnoreCase("ViceDean")) {
      user.setUserRole(userRoleService.getUserRole(RoleType.ASSISTANT_MANAGER));
    } else {
      throw new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_USER_ROLE_MESSAGE,userRole));
    }

    User savedUser = userRepository.save(user);
    
    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.USER_CREATE)
        .object(userMapper.mapUserToUserResponse(savedUser))
        .build();
 
  }

  public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type,
      String userRole) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);    
    return userRepository.findByUserByRole(userRole,pageable)
        //map entity to response DTO
        .map(userMapper::mapUserToUserResponse);
  }

  public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
    //need to check if user exist with this id
    User user = userRepository.findById(userId).orElseThrow(()->
        new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,userId)));
    
    return ResponseMessage.<BaseUserResponse>builder()
        .message(SuccessMessages.USER_FOUND)
        .object(userMapper.mapUserToUserResponse(user))
        .httpStatus(HttpStatus.OK)
        .build();
    
  }

  public List<UserResponse> getUserByName(String userName) {    
    return userRepository.getUserByNameContaining(userName)
        .stream()
        .map(userMapper::mapUserToUserResponse)
        .collect(Collectors.toList());   
  }

  public String updateUser(UserRequestWithoutPassword userRequest,
      HttpServletRequest request) {
    
    String userName = (String) request.getHeader("username");    
    User user = userRepository.findByUsername(userName);    
    //we need to check if user is builtIn
    methodHelper.checkBuiltIn(user);
    
    //uniqueness control
    uniquePropertyValidator.checkUniqueProperties(user,userRequest);
    //classic mappings instead of builder mappers
    user.setName(userRequest.getName());
    user.setSurname(userRequest.getSurname());
    user.setUsername(userRequest.getUsername());
    user.setBirthDay(userRequest.getBirthDay());
    user.setBirthPlace(userRequest.getBirthPlace());
    user.setEmail(userRequest.getEmail());
    user.setPhoneNumber(userRequest.getPhoneNumber());
    user.setGender(userRequest.getGender());
    user.setSsn(userRequest.getSsn());
    
    userRepository.save(user);
    return SuccessMessages.USER_UPDATE;
  }

  public ResponseMessage<BaseUserResponse> updateAdminDeanViceDeanByAdmin(Long userId,
      UserRequest userRequest) {
       
    //check user if really exist
    User user = methodHelper.isUserExist(userId);
    //check user is built in
    methodHelper.checkBuiltIn(user);
    
    
  }
}
