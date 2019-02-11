package com.mavs.userservice.service;


import com.mavs.userservice.model.User;

public interface AuthActivityService {

    void processNewUserActivities(User savedUser);
}
