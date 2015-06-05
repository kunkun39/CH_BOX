package com.changhong.system.domain;

import org.springframework.security.core.GrantedAuthority;

/**
 * User: Jack Wang
 * Date: 14-7-14
 * Time: 上午10:27
 */
public class Role implements GrantedAuthority {

    private String role;

    public Role(String role) {
        this.role = role;
    }

    public String getAuthority() {
        return this.role;
    }
}
