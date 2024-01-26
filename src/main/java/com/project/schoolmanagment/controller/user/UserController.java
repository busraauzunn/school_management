package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.request.user.UserRequestWithoutPassword;
import com.project.schoolmanagment.payload.response.abstracts.BaseUserResponse;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.service.user.UserService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  
  private final UserService userService;
  
  
  @PostMapping("/save/{userRole}")
  public ResponseEntity<ResponseMessage<UserResponse>> saveUser(
      // single field validation in controller level
      @RequestBody @Valid UserRequest userRequest,
      @PathVariable String userRole){
    return ResponseEntity.ok(userService.saveUser(userRequest,userRole));
  }

  @GetMapping("/getAllUsersByPage/{userRole}")
  public ResponseEntity<Page<UserResponse>> getUserByPage(
      @PathVariable String userRole,
      @RequestParam (value = "page", defaultValue = "0") int page,
      @RequestParam (value = "size", defaultValue = "10") int size,
      @RequestParam (value = "sort", defaultValue = "name") String sort,
      @RequestParam (value = "type", defaultValue = "desc") String type){
    Page<UserResponse>userResponse = userService.getUsersByPage(page,size,sort,type,userRole);
    return new ResponseEntity<>(userResponse, HttpStatus.OK);
  }
  
  @GetMapping("/getUserById/{userId}")
  public ResponseMessage<BaseUserResponse>getUserById(@PathVariable Long userId){
    return userService.getUserById(userId);
  }
  
  @GetMapping("/getUserByName")
  public List<UserResponse>getUserByName(@RequestParam (name = "name") String userName) {
    return userService.getUserByName(userName);
  }
  
  @PatchMapping("/updateUser")
  public ResponseEntity<String>updateUser(@RequestBody @Valid
      UserRequestWithoutPassword userRequestWithoutPassword,
      HttpServletRequest request) {
    return ResponseEntity.ok(userService.updateUser(userRequestWithoutPassword,request));
  }
  
  @PutMapping("/update/{userId}")
  public ResponseMessage<BaseUserResponse>updateAdminDeanViceDeanByAdmin(
      @RequestBody @Valid UserRequest userRequest,
      @PathVariable Long userId){
    return userService.updateAdminDeanViceDeanByAdmin(userId,userRequest);
  }
  
  @DeleteMapping("/delete/{id}")
  public ResponseEntity<String>deleteUserById(@PathVariable Long id,
      HttpServletRequest httpServletRequest){
    return ResponseEntity.ok(userService.deleteUserById(id,httpServletRequest));
  }
  
  
  

}
