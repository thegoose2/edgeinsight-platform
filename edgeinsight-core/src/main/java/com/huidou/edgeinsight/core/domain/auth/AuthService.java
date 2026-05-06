package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.login.LoginRequest;
import com.huidou.edgeinsight.common.dto.login.LoginResponseUserInfo;
import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public LoginResponseUserInfo login(LoginRequest request) {
        SysUser user = sysUserRepository.findByUsernameWithRolesAndPermissions(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));

        if (user.getStatus() != SysUser.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("用户已被禁用");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("用户名或密码错误");
        }

        user.setLastLoginAt(LocalDateTime.now());
        sysUserRepository.save(user);

        List<String> roles = user.getUserRoles().stream()
                .map(ur -> ur.getRole().getRoleCode())
                .collect(Collectors.toList());

        Set<String> permissions = user.getUserRoles().stream()
                .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                .map(rp -> rp.getPermission().getPermCode())
                .collect(Collectors.toSet());

        return LoginResponseUserInfo.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roles(roles)
                .perms(permissions.stream().collect(Collectors.toList()))
                .build();
    }
}
