package com.mavs.userservice.controller;

import com.mavs.userservice.controller.dto.LoginUserInfoDto;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.model.User;
import com.mavs.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginUserInfoDto loginUserInfoDto, BindingResult result) {
        if (!result.hasErrors()) {
            Optional<User> loginUser = userService.findByName(loginUserInfoDto.getUsername());
            if (loginUser.isPresent() && userService.isUserPasswordValid(loginUserInfoDto.getPassword(),
                    loginUser.get().getSecurityUserDetails().getPassword())) {
                return ResponseEntity.ok("User was logged in!");
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/registration")
    public String register(@RequestBody @Valid RegisterUserDto registerUserDto, BindingResult result) {
        if (!result.hasErrors()) {
            userService.registerNewUser(registerUserDto);
            return "User was created";
        }
        return "User wasn't created! Wrong params!";
    }
}
