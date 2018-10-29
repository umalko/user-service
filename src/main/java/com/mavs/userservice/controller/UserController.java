package com.mavs.userservice.controller;

import com.google.common.base.Preconditions;
import com.mavs.userservice.exception.ResourceNotFoundException;
import com.mavs.userservice.model.User;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Value("${app.users.key:default}")
    private String mySecret;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.warn("-----mySecret: {}", mySecret);
        return userService.findAll();
    }

    @GetMapping("/id/{id}")
    public User findById(@PathVariable("id") Integer id) {
        return userService.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    @GetMapping("/username/{username}")
    public User findByName(@PathVariable("username") String username) {
        return userService.findByName(username).orElseThrow(ResourceNotFoundException::new);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestBody User user) {
        Preconditions.checkNotNull(user);
        userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Integer id) {
        userService.delete(id);
    }
}

