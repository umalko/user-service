package com.mavs.userservice.model;

import com.google.common.collect.Sets;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "security_user_details")
public class SecurityUserDetails implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_details_authorities", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)

    @Builder.Default
    private Set<Authority> authorities = Sets.newHashSet(Authority.USER);
    private String password;
    private String username;

    @Builder.Default
    private boolean accountNonExpired = Boolean.TRUE;
    @Builder.Default
    private boolean accountNonLocked = Boolean.TRUE;
    @Builder.Default
    private boolean credentialsNonExpired = Boolean.TRUE;
    @Builder.Default
    private boolean enabled = Boolean.TRUE;
}
