package com.mavs.userservice.repository;

import com.mavs.userservice.model.SecurityUserDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityUserDetailsRepository extends CrudRepository<SecurityUserDetails, Integer> {

    SecurityUserDetails findByUsername(String username);
}
