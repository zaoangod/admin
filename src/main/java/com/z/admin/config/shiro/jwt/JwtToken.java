package com.z.admin.config.shiro.jwt;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Description: JwtToken
 * @Date: 2019-11-14 15:26
 * @Author: jy
 */
public class JwtToken implements AuthenticationToken {

    private static final long   serialVersionUID = 1L;
    private              String token;

    public JwtToken(String token) {
        this.token = token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}