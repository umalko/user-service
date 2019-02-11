package com.mavs.userservice.repository;

import com.mavs.userservice.model.UserActivity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CRUD Activity Repository
 */
@Repository
public interface UserActivityRepository extends PagingAndSortingRepository<UserActivity, String> {

    List<UserActivity> findByType(String type);

}
