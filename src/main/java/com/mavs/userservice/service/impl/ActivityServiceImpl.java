package com.mavs.userservice.service.impl;

import com.mavs.activity.dto.ActivityAuthUserDto;
import com.mavs.activity.dto.ActivityDto;
import com.mavs.activity.dto.ActivityUserDto;
import com.mavs.activity.model.Activity;
import com.mavs.activity.model.ActivityProcessType;
import com.mavs.activity.model.ActivityType;
import com.mavs.activity.provider.ActivityMessageQueueProvider;
import com.mavs.activity.util.ActivityUtil;
import com.mavs.userservice.model.User;
import com.mavs.userservice.model.UserActivity;
import com.mavs.userservice.service.AuthActivityService;
import com.mavs.userservice.service.UserActivityService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ActivityServiceImpl implements AuthActivityService {

    private final UserActivityService userActivityService;
    private final ActivityMessageQueueProvider activityMessageQueueProvider;

    public ActivityServiceImpl(UserActivityService userActivityService, ActivityMessageQueueProvider activityMessageQueueProvider) {
        this.userActivityService = userActivityService;
        this.activityMessageQueueProvider = activityMessageQueueProvider;
    }

    @Async
    @Override
    public void processNewUserActivities(User savedUser) {
        processNewUserActivity(savedUser, ActivityType.NEW_USER_NOTIFICATION);
        processNewUserActivity(savedUser, ActivityType.NEW_USER_AUTH);
    }

    @Async
    public void processNewUserActivity(User savedUser, ActivityType activityType) {
        ActivityUserDto activityUserDto = buildActivityUserDto(savedUser);
        ActivityUtil.buildActivity(activityUserDto, activityType).ifPresent(activity -> {
            Activity savedActivity = userActivityService.save(new UserActivity(activity));
            ActivityDto activityDto = ActivityUtil.buildActivityDto(savedActivity, activityUserDto);
            activityMessageQueueProvider.produceActivity(activityDto);

            savedActivity.setProcessType(ActivityProcessType.SENT);
            userActivityService.update(new UserActivity(savedActivity));
        });
    }

    private ActivityAuthUserDto buildActivityUserDto(User user) {
        return new ActivityAuthUserDto(user.getEmail(), user.getPhone(), user.getUsername(), user.getPassword());
    }

}
