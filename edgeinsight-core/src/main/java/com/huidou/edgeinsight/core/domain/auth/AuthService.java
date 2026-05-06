package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.login.LoginRequest;
import com.huidou.edgeinsight.common.dto.login.LoginResponseUserInfo;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final SysUserRepository sysUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(SysUserRepository sysUserRepository, PasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public LoginContext login(LoginRequest request) {
        SysUser user = sysUserRepository.findByUsernameWithRoles(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("用户名错误"));

        if (user.getStatus() != SysUser.UserStatus.ACTIVE) {
            throw new UnauthorizedException("用户已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("密码错误");
        }

        List<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleCode())
                .collect(Collectors.toList());

        Set<String> permissions = sysUserRepository.findPermissionCodesByUsername(request.getUsername());

        LoginResponseUserInfo userInfo = LoginResponseUserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .perms(permissions.stream().collect(Collectors.toList()))
                .build();

        return new LoginContext(userInfo);
    }

    public static class LoginContext {
        private final LoginResponseUserInfo userInfo;

        public LoginContext(LoginResponseUserInfo userInfo) {
            this.userInfo = userInfo;
        }

        public LoginResponseUserInfo getUserInfo() {
            return userInfo;
        }
    }
}