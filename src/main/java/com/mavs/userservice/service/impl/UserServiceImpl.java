package com.mavs.userservice.service.impl;

import com.google.common.collect.Lists;
import com.mavs.common.dto.UserDto;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.exception.ResourceWasNotSavedException;
import com.mavs.userservice.model.User;
import com.mavs.userservice.repository.UserRepository;
import com.mavs.userservice.service.AuthActivityService;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthActivityService authActivityService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository, AuthActivityService authActivityService) {
        this.userRepository = userRepository;
        this.authActivityService = authActivityService;
    }

    @Override
    public List<User> findAll() {
        return Lists.newArrayList(userRepository.findAll());
    }

    @Override
    @Cacheable(value = "users", key = "#userId", unless = "#result == null")
    public Optional<User> findById(Integer userId) {
        log.info("Find user by id: {}", userId);
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findByName(String name) {
        return userRepository.findByUsername(name);
    }

    @Override
    public Optional<User> save(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new ResourceWasNotSavedException();
        }
        User user = transformToUser(userDto);
        User savedUser = userRepository.save(user);
        return Optional.of(savedUser);
    }

    @Override
    public Optional<User> save(RegisterUserDto registerUserDto) {
        if (userRepository.findByUsername(registerUserDto.getUsername()).isPresent() ||
                userRepository.findByEmail(registerUserDto.getEmail()).isPresent()) {
            throw new ResourceWasNotSavedException();
        }

        User savedUser = userRepository.save(transformRegisteredUserDtoToUser(registerUserDto));
        authActivityService.processNewUserActivities(savedUser);
        return Optional.of(savedUser);
    }

    @Override
    @CachePut(value = "users", key = "#user.id", unless = "#result == null")
    public void update(User user) {
        userRepository.findById(user.getId()).ifPresent(dbUser -> {
            BeanUtils.copyProperties(user, dbUser);
            userRepository.save(dbUser);
        });
    }

    @Override
    @CacheEvict(value = "users", key = "#userId")
    public void delete(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    private User transformToUser(UserDto userDto) {
        return User.builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .phone(userDto.getPhone())
                .build();
    }

    private User transformRegisteredUserDtoToUser(RegisterUserDto registerUserDto) {
        return User.builder()
                .email(registerUserDto.getEmail())
                .username(registerUserDto.getUsername())
                .password(encoder.encode(registerUserDto.getPassword()))
                .build();
    }
}
