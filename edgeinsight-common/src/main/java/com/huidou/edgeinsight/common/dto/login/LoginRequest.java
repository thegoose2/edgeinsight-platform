package com.huidou.edgeinsight.common.dto.login;

import lombok.Data;

import javax.persistence.Column;

@Data
public class LoginRequest {

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
