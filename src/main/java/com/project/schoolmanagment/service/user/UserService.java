package com.project.schoolmanagment.service.user;

import com.project.schoolmanagment.entity.concretes.user.User;
import com.project.schoolmanagment.entity.enums.RoleType;
import com.project.schoolmanagment.exception.BadRequestException;
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

  /**
   * Saves a user with the specified user request and user role.
   *
   * @param userRequest The UserRequest object containing the user information.
   * @param userRole    The role of the user.
   * @return A ResponseMessage object containing the status, message, and the saved user information.
   * @throws ResourceNotFoundException If the specified user role does not exist.
   */
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
    if (userRole.equalsIgnoreCase(RoleType.ADMIN.getName())) {
      //if username is Admin then we set this user buildIn -> TRUE
      if (Objects.equals(userRequest.getUsername(), "Admin")) {
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
      throw new ResourceNotFoundException(
          String.format(ErrorMessages.NOT_FOUND_USER_USER_ROLE_MESSAGE, userRole));
    }

    User savedUser = userRepository.save(user);

    return ResponseMessage.<UserResponse>builder()
        .message(SuccessMessages.USER_CREATE)
        .returnBody(userMapper.mapUserToUserResponse(savedUser))
        .build();

  }

  /**
   * Retrieves a page of users based on the specified page number, size, sort, type, and user role.
   *
   * @param page      The page number to retrieve. Must be greater than or equal to 0.
   * @param size      The number of items per page. Must be greater than 0.
   * @param sort      The name of the field to sort the results by.
   * @param type      The type of sorting. "asc" for ascending, "desc" for descending.
   * @param userRole  The role of the user.
   * @return A page of UserResponse objects containing the user information.
   */
  public Page<UserResponse> getUsersByPage(int page, int size, String sort, String type,
      String userRole) {
    Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
    return userRepository.findByUserByRole(userRole, pageable)
        //map entity to response DTO
        .map(userMapper::mapUserToUserResponse);
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param userId The ID of the user to retrieve.
   * @return A ResponseMessage object containing the user information.
   * @throws ResourceNotFoundException If the user with the given ID does not exist.
   */
  public ResponseMessage<BaseUserResponse> getUserById(Long userId) {
    //need to check if user exist with this id
    User user = userRepository.findById(userId).orElseThrow(() ->
        new ResourceNotFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE, userId)));

    return ResponseMessage.<BaseUserResponse>builder()
        .message(SuccessMessages.USER_FOUND)
        .returnBody(userMapper.mapUserToUserResponse(user))
        .httpStatus(HttpStatus.OK)
        .build();

  }

  /**
   * Retrieves a list of users by their username.
   *
   * @param userName The username of the user to search for.
   * @return A list of UserResponse objects containing the user information.
   */
  public List<UserResponse> getUserByName(String userName) {
    return userRepository.getUserByNameContaining(userName)
        .stream()
        .map(userMapper::mapUserToUserResponse)
        .collect(Collectors.toList());
  }

  /**
   * Updates the user information.
   *
   * @param userRequest The UserRequestWithoutPassword object containing the updated user information.
   * @param request The HttpServletRequest object.
   * @return The success message indicating that the user has been updated.
   */
  public String updateUser(UserRequestWithoutPassword userRequest,
      HttpServletRequest request) {

    String userName = (String) request.getAttribute("username");
    User user = userRepository.findByUsername(userName);
    //we need to check if user is builtIn
    methodHelper.checkBuiltIn(user);

    //uniqueness control
    uniquePropertyValidator.checkUniqueProperties(user, userRequest);
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

  /**
   * Updates the admin, dean, or vice dean user by an admin.
   *
   * @param userId       The ID of the user to be updated.
   * @param userRequest  The UserRequest object containing the updated user information.
   * @return A ResponseMessage object with the updated user information.
   */
  public ResponseMessage<BaseUserResponse> updateAdminDeanViceDeanByAdmin(Long userId,
      UserRequest userRequest) {

    //check user if really exist
    //entity-1 comes from DB
    User user = methodHelper.isUserExist(userId);
    //check user is built in
    methodHelper.checkBuiltIn(user);

    uniquePropertyValidator.checkUniqueProperties(user, userRequest);
    //entity-2 created by mappers from DTO
    User userToSave = userMapper.mapUserRequestToUser(userRequest);
    userToSave.setId(user.getId());
    userToSave.setUserRole(user.getUserRole());
    //entity-3 return type of save operation
    User savedUser = userRepository.save(userToSave);
    //time to return BaseUserResponse DTO to controller
    return ResponseMessage.<BaseUserResponse>builder()
        .message(SuccessMessages.USER_UPDATE_MESSAGE)
        .httpStatus(HttpStatus.OK)
        .returnBody(userMapper.mapUserToUserResponse(savedUser))
        .build();
  }

  /**
   * Deletes a user by their ID.
   *
   * @param id                The ID of the user to be deleted.
   * @param httpServletRequest The HttpServletRequest object.
   * @return A success message indicating that the user has been deleted.
   * @throws BadRequestException         If the logged-in user does not have the permission to delete the user.
   * @throws ResourceNotFoundException  If the user to be deleted does not exist.
   */
  public String deleteUserById(Long id, HttpServletRequest httpServletRequest) {
    User user = methodHelper.isUserExist(id);

    //username of logged in person
    String userName = (String) httpServletRequest.getAttribute("username");

    User loggedInUser = userRepository.findByUsername(userName);

    RoleType loggedInUserRole = loggedInUser.getUserRole().getRoleType();
    RoleType deletedUserRole = user.getUserRole().getRoleType();
    if (loggedInUser.getBuiltIn()) {
      //buildIn users can not neither be updated nor deleted.
      throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
      //manager can only delete teacher/student/viceDean
    } else if (loggedInUserRole == RoleType.MANAGER) {
      if (!(deletedUserRole == RoleType.TEACHER ||
          deletedUserRole == RoleType.STUDENT ||
          deletedUserRole == RoleType.ASSISTANT_MANAGER)) {
        throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
      }
      //assistan manager can only delete teacher/student
    } else if (loggedInUserRole == RoleType.ASSISTANT_MANAGER) {
      if (!(deletedUserRole == RoleType.TEACHER ||
          deletedUserRole == RoleType.STUDENT)) {
        throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
      }
    }
    userRepository.deleteById(id);
    return SuccessMessages.USER_DELETE;
  }
  
  public List<User> getAllUsers(){
    return userRepository.findAll();
  }

}
