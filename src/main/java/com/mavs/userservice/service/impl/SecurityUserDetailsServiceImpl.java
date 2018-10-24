package com.mavs.userservice.service.impl;

import com.mavs.userservice.model.SecurityUserDetails;
import com.mavs.userservice.repository.SecurityUserDetailsRepository;
import com.mavs.userservice.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SecurityUserDetailsServiceImpl implements SecurityUserDetailsService {

    private final SecurityUserDetailsRepository securityUserDetailsRepository;

    @Autowired
    public SecurityUserDetailsServiceImpl(SecurityUserDetailsRepository securityUserDetailsRepository) {
        this.securityUserDetailsRepository = securityUserDetailsRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return securityUserDetailsRepository.findByUsername(username);
    }

    @Override
    public SecurityUserDetails save(SecurityUserDetails userDetails) {
         return securityUserDetailsRepository.save(userDetails);
    }
}
