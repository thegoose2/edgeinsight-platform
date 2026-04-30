package com.huidou.edgeinsight.common.dto.login;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class LoginResponseUserInfo {

    private Long userId;
    private String username;
    private String realName;
    private List<String> roles;
    private List<String> perms;
}