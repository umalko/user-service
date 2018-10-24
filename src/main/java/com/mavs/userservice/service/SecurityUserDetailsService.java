package com.mavs.userservice.service;

import com.mavs.userservice.model.SecurityUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface SecurityUserDetailsService extends UserDetailsService {

    SecurityUserDetails save(SecurityUserDetails userDetails);
}
