package com.huidou.edgeinsight.common.dto.login;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
public class LoginRequest {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public LoginRequest() {}

    public LoginRequest build(String username, String password) {
        return LoginRequest.builder()
            .username(username)
            .password(password)
            .build();
    }

}