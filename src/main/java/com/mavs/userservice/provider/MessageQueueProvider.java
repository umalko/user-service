package com.mavs.userservice.provider;

import com.mavs.userservice.model.User;

public interface MessageQueueProvider {

    void sendToUserTopic(User user);
}
