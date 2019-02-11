package com.mavs.userservice.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mavs.activity.dto.ActivityDto;
import com.mavs.activity.dto.ActivityUserDto;
import com.mavs.activity.model.ActivityProcessType;
import com.mavs.activity.util.ActivityUtil;
import com.mavs.userservice.controller.dto.UserDto;
import com.mavs.userservice.model.UserActivity;
import com.mavs.userservice.service.UserActivityService;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RabbitListener(queues = "NEW_USER_PROFILE_QUEUE")
public class UserConsumerListener {

    private final UserActivityService activityService;
    private final UserService userService;

    public UserConsumerListener(UserActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    @RabbitHandler
    public void onReceive(@Payload String jsonObject) {
        transformJsonObject(jsonObject, new TypeReference<ActivityDto<ActivityUserDto>>() {
        }).ifPresent(activityDto ->
                ActivityUtil.convertToActivity((ActivityDto<ActivityUserDto>) activityDto).ifPresent(activity -> {
                    activityService.save(new UserActivity(activity));
                    transformJsonObject(activity.getJsonObject(), new TypeReference<UserDto>() {
                    }).ifPresent(userDto -> userService.save((UserDto) userDto));

                    activity.setProcessType(ActivityProcessType.PROCESSED);
                    activityService.save(new UserActivity(activity));
                }));
    }

    private Optional<Object> transformJsonObject(String jsonObject, TypeReference typeReference) {
        try {
            Object object = new ObjectMapper().readValue(jsonObject, typeReference);
            log.info("Activity object comes to Notification Service: {}", object);
            return Optional.of(object);
        } catch (IOException e) {
            log.error("Activity oject wasn't parsed correctly! Object: " + jsonObject, e);
        }
        return Optional.empty();
    }
}
