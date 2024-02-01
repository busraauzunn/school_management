package com.project.schoolmanagment.controller.user;

import com.project.schoolmanagment.payload.messages.SuccessMessages;
import com.project.schoolmanagment.payload.request.authentication.LoginRequest;
import com.project.schoolmanagment.payload.request.authentication.UpdatePasswordRequest;
import com.project.schoolmanagment.payload.response.authentication.AuthResponse;
import com.project.schoolmanagment.service.user.AuthenticationService;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
  
  private final AuthenticationService authenticationService;
  
  @PostMapping("/login")
  public ResponseEntity<AuthResponse>authenticateUser(@RequestBody @Valid LoginRequest request){
    return ResponseEntity.ok(authenticationService.authenticateUser(request));
  }


  @PreAuthorize("hasAnyAuthority('Admin','Dean','ViceDean','Teacher','Student')")
  @PatchMapping("/updatePassword")
  public ResponseEntity<String> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
      HttpServletRequest httpServletRequest){
    authenticationService.updatePassword(updatePasswordRequest,httpServletRequest);
    return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_RESPONSE_MESSAGE);
  }

}
