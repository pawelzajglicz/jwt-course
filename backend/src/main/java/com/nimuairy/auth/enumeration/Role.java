package com.nimuairy.auth.enumeration;

import com.nimuairy.auth.domain.Authority;

import java.util.Set;

import static com.nimuairy.auth.constant.Authorities.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_USER(SUPER_ADMIN_AUTHORITIES);

    private Set<Authority> authorities;

    Role(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Authority>getAuthorities() {
        return authorities;
    }
}
