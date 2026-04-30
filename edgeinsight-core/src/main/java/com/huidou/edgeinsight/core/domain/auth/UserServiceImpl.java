package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.SysRole;
import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.common.model.SysUserRole;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysRoleRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysUserRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysUserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final JpaSysUserRepository userRepository;
    private final JpaSysRoleRepository roleRepository;
    private final JpaSysUserRoleRepository userRoleRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(JpaSysUserRepository userRepository,
                           JpaSysRoleRepository roleRepository,
                           JpaSysUserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public SysUserVO createUser(Object reqObj) {
        SysUserInsertReq req = (SysUserInsertReq) reqObj;

        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new BusinessException("Username already exists");
        }

        SysUser user = new SysUser();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user = userRepository.save(user);

        // Assign roles
        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            for (Long roleId : req.getRoleIds()) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                userRoleRepository.save(ur);
            }
        }

        return toVO(user);
    }

    @Override
    @Transactional
    public SysUserVO updateUser(Long id, Object reqObj) {
        SysUserUpdateReq req = (SysUserUpdateReq) reqObj;

        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (req.getRealName() != null) user.setRealName(req.getRealName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getEmail() != null) user.setEmail(req.getEmail());

        userRepository.save(user);
        return toVO(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (Boolean.TRUE.equals(user.getIsSystem())) {
            throw new BusinessException("Cannot delete system built-in account");
        }

        userRoleRepository.deleteByUserId(id);
        userRepository.delete(user);
    }

    @Override
    public SysUserVO findById(Long id) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        return toVO(user);
    }

    @Override
    public Page<SysUserVO> findAll(Object queryObj) {
        SysUserQueryReq query = (SysUserQueryReq) queryObj;
        Pageable pageable = query.toPageable();
        Page<SysUser> page = userRepository.findAll(query.getKeyword(),
                query.getStatus() != null ? SysUser.UserStatus.valueOf(query.getStatus()) : null,
                pageable);
        return page.map(this::toVO);
    }

    @Override
    @Transactional
    public void updatePassword(Long id, String oldPassword, String newPassword, boolean isAdmin) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (!isAdmin && oldPassword != null && !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        SysUser user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        Long currentUserId = com.huidou.edgeinsight.api.security.SecurityContextHolder.getCurrentUser().getUserId();
        if (id.equals(currentUserId)) {
            throw new BusinessException("Cannot deactivate your own account");
        }

        user.setStatus(SysUser.UserStatus.valueOf(status));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        userRoleRepository.deleteByUserId(userId);

        if (roleIds != null) {
            for (Long roleId : roleIds) {
                if (!roleRepository.existsById(roleId)) {
                    throw new NotFoundException("Role not found: " + roleId);
                }
                SysUserRole ur = new SysUserRole();
                ur.setUserId(userId);
                ur.setRoleId(roleId);
                userRoleRepository.save(ur);
            }
        }
    }

    private SysUserVO toVO(SysUser user) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setPhone(user.getPhone());
        vo.setEmail(user.getEmail());
        vo.setStatus(user.getStatus().name());
        vo.setLastLoginAt(user.getLastLoginAt());
        vo.setCreatedAt(user.getCreatedAt());

        // Load roles
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        List<SysRole> roles = roleRepository.findAllById(roleIds);
        List<SysUserVO.RoleVO> roleVOs = roles.stream().map(r -> {
            SysUserVO.RoleVO rv = new SysUserVO.RoleVO();
            rv.setId(r.getId());
            rv.setRoleName(r.getRoleName());
            return rv;
        }).collect(Collectors.toList());
        vo.setRoles(roleVOs);

        return vo;
    }
}