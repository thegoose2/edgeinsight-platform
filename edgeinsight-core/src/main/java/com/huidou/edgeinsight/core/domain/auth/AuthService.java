package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.LoginVO;

public interface AuthService {

    LoginVO login(String username, String password);

    void logout(String token);

    LoginVO.UserInfoVO getUserInfo();
}