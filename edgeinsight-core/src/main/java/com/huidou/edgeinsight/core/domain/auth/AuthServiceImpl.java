package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.api.security.JwtTokenProvider;
import com.huidou.edgeinsight.common.dto.LoginReq;
import com.huidou.edgeinsight.common.dto.LoginVO;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.UnauthorizedException;
import com.huidou.edgeinsight.common.model.SysPermission;
import com.huidou.edgeinsight.common.model.SysRole;
import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysPermissionRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysRolePermissionRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysRoleRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysUserRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysUserRoleRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final JpaSysUserRepository userRepository;
    private final JpaSysRoleRepository roleRepository;
    private final JpaSysUserRoleRepository userRoleRepository;
    private final JpaSysRolePermissionRepository rolePermissionRepository;
    private final JpaSysPermissionRepository permissionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthServiceImpl(JpaSysUserRepository userRepository,
                           JpaSysRoleRepository roleRepository,
                           JpaSysUserRoleRepository userRoleRepository,
                           JpaSysRolePermissionRepository rolePermissionRepository,
                           JpaSysPermissionRepository permissionRepository,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public LoginVO login(String username, String password) {
        SysUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        if (user.getStatus() == SysUser.UserStatus.INACTIVE) {
            throw new UnauthorizedException("User account is inactive");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // Get roles
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        List<SysRole> roles = roleRepository.findAllById(roleIds);
        List<String> roleCodes = roles.stream().map(SysRole::getRoleCode).collect(Collectors.toList());

        // Get permissions (union of all roles)
        List<String> perms = roleIds.stream()
                .flatMap(roleId -> rolePermissionRepository.findPermIdsByRoleId(roleId).stream())
                .distinct()
                .map(permissionRepository::findById)
                .filter(java.util.Optional::isPresent)
                .map(p -> p.get().getPermCode())
                .collect(Collectors.toList());

        // Build UserInfo and generate token
        JwtTokenProvider.UserInfo userInfo = new JwtTokenProvider.UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setRealName(user.getRealName());
        userInfo.setRoles(roleCodes);
        userInfo.setPerms(perms);

        String token = jwtTokenProvider.generateToken(userInfo);

        // Build response
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setExpireAt(LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("+8"))
                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".000Z");

        LoginVO.UserInfoVO userInfoVO = new LoginVO.UserInfoVO();
        userInfoVO.setUserId(user.getId());
        userInfoVO.setUsername(user.getUsername());
        userInfoVO.setRealName(user.getRealName());
        userInfoVO.setRoles(roleCodes);
        userInfoVO.setPerms(perms);
        vo.setUserInfo(userInfoVO);

        return vo;
    }

    @Override
    public void logout(String token) {
        // JWT is stateless, client should discard the token
        // Server can optionally maintain a blacklist (not implemented for now)
    }

    @Override
    public LoginVO.UserInfoVO getUserInfo() {
        JwtTokenProvider.UserInfo currentUser = com.huidou.edgeinsight.api.security.SecurityContextHolder.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Not authenticated");
        }

        LoginVO.UserInfoVO vo = new LoginVO.UserInfoVO();
        vo.setUserId(currentUser.getUserId());
        vo.setUsername(currentUser.getUsername());
        vo.setRealName(currentUser.getRealName());
        vo.setRoles(currentUser.getRoles());
        vo.setPerms(currentUser.getPerms());
        return vo;
    }
}