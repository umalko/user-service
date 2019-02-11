package com.mavs.userservice.controller;

import com.mavs.userservice.controller.dto.UserDto;
import com.mavs.userservice.exception.ResourceNotFoundException;
import com.mavs.userservice.model.User;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // TODO: add new activity UPDATE_USER
//    @PutMapping
//    @ResponseStatus(HttpStatus.OK)
//    public void update(@RequestBody User user) {
//        Preconditions.checkNotNull(user);
//        userService.update(user);
//    }

    // TODO: add new activity DELETE_USER
//    @DeleteMapping("/{id}")
//    @ResponseStatus(HttpStatus.OK)
//    public void delete(@PathVariable("id") Integer id) {
//        userService.delete(id);
//    }

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

