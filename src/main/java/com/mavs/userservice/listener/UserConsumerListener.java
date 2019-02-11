package com.mavs.userservice.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mavs.activity.dto.ActivityDto;
import com.mavs.activity.dto.ActivityUserDto;
import com.mavs.activity.model.ActivityProcessType;
import com.mavs.activity.service.ActivityService;
import com.mavs.activity.util.ActivityUtil;
import com.mavs.userservice.controller.dto.UserDto;
import com.mavs.userservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@EnableRabbit
@Configuration
@RabbitListener(queues = "NEW_USER_PROFILE_QUEUE")
public class UserConsumerListener {

    private final ActivityService activityService;
    private final UserService userService;

    public UserConsumerListener(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    @RabbitHandler
    public void onReceive(@Payload String jsonObject) {
        transformJsonObject(jsonObject, new TypeReference<ActivityDto<ActivityUserDto>>() {
        }).ifPresent(activityDto ->
                ActivityUtil.convertToActivity((ActivityDto<ActivityUserDto>) activityDto).ifPresent(activity -> {
                    activityService.save(activity);
                    transformJsonObject(activity.getJsonObject(), new TypeReference<UserDto>() {
                    }).ifPresent(userDto -> userService.save((UserDto) userDto));

                    activity.setProcessType(ActivityProcessType.PROCESSED);
                    activityService.save(activity);
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
