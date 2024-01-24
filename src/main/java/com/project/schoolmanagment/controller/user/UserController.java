package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.request.user.UserRequest;
import com.project.schoolmanagment.payload.response.businnes.ResponseMessage;
import com.project.schoolmanagment.payload.response.user.UserResponse;
import com.project.schoolmanagment.service.user.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  

}
