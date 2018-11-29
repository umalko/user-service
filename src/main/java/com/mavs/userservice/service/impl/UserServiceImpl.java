package com.mavs.userservice.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.exception.ResourceWasNotSavedException;
import com.mavs.userservice.model.Authority;
import com.mavs.userservice.model.SecurityUserDetails;
import com.mavs.userservice.model.User;
import com.mavs.userservice.repository.UserRepository;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public Optional<User> registerNewUser(RegisterUserDto registerUserDto) {
        if (userRepository.findByUsername(registerUserDto.getUsername()).isPresent()) {
            throw new ResourceWasNotSavedException();
        }

        User user = User.builder()
                .email(registerUserDto.getEmail())
                .username(registerUserDto.getUsername())
                .securityUserDetails(
                        SecurityUserDetails.builder()
                                .username(registerUserDto.getUsername())
                                .password(encryptPassword(registerUserDto.getPassword()))
                                .authorities(Sets.newHashSet(Authority.USER, Authority.ADMIN))
                                .build()).build();
        return Optional.of(userRepository.save(user));
    }

    @Override
    public boolean isUserPasswordValid(String userPassword, String encryptedPassword) {
        return new BasicPasswordEncryptor().checkPassword(userPassword, encryptedPassword);
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

    private String encryptPassword(String password) {
        return password;
//        return new BCryptPasswordEncoder().encode(password);
    }
}
