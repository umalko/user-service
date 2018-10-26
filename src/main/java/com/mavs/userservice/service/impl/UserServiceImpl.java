package com.mavs.userservice.service.impl;

import com.google.common.collect.Lists;
import com.mavs.userservice.controller.dto.RegisterUserDto;
import com.mavs.userservice.exception.ResourceWasNotSavedException;
import com.mavs.userservice.model.SecurityUserDetails;
import com.mavs.userservice.model.User;
import com.mavs.userservice.repository.UserRepository;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@Cacheable(value = "users")
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
    public Optional<User> findById(Integer id) {
        log.info("Find user by id: {}", id);
        return userRepository.findById(id);
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
                                .password(registerUserDto.getPassword())
                                .build()).build();
        return Optional.of(userRepository.save(user));
    }

    @Override
    public void update(User user) {
        userRepository.findById(user.getId()).ifPresent(dbUser -> {
            BeanUtils.copyProperties(user, dbUser);
            userRepository.save(dbUser);
        });
    }

    @Override
    public void delete(Integer id) {
        userRepository.deleteById(id);
    }
}
