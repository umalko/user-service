package com.mavs.userservice.service;

import com.mavs.common.dto.UserDto;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Integer id);

    Optional<User> findByName(String name);

    Optional<User> save(UserDto userDto);

    Optional<User> save(RegisterUserDto registerUserDto);

    void update(User user);

    void delete(Integer id);

    Optional<User> findByEmail(String email);
}
