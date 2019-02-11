package com.mavs.userservice.controller;

import com.google.common.base.Preconditions;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.controller.dto.UserDto;
import com.mavs.userservice.exception.ResourceNotFoundException;
import com.mavs.userservice.model.User;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RefreshScope
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDto> findAll() {
        return userService.findAll().stream().map(this::transformUserModelToDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable("id") Integer id) {
        log.info("requested ID: {}", id);
        return transformUserOptionalModelToDto(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public UserDto findByName(@PathVariable("username") String username) {
        return transformUserOptionalModelToDto(userService.findByName(username));
    }

    @GetMapping("/email/{email}")
    public UserDto findByEmail(@PathVariable("email") String email) {
        return transformUserOptionalModelToDto(userService.findByEmail(email));
    }

    @PostMapping()
    public String register(@RequestBody @Valid RegisterUserDto registerUserDto, BindingResult result) {
        if (!result.hasErrors()) {
            userService.save(registerUserDto);
            return "User was created";
        }
        return "User wasn't created! Wrong params!";
    }

    // TODO: add new activity UPDATE_USER
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody User user) {
        Preconditions.checkNotNull(user);
        userService.update(user);
    }

    // TODO: add new activity DELETE_USER
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Integer id) {
        userService.delete(id);
    }

    private UserDto transformUserModelToDto(User user) {
        return UserDto.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .phone(user.getPhone())
                .build();
    }

    private UserDto transformUserOptionalModelToDto(Optional<User> userOptional) {
        if (userOptional.isPresent()) {
            return transformUserModelToDto(userOptional.get());
        }
        throw new ResourceNotFoundException();
    }
}

