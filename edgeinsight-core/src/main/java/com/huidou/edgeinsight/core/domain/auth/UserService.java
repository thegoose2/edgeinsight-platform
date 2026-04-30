package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.SysUserVO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {

    SysUserVO createUser(Object user);

    SysUserVO updateUser(Long id, Object user);

    void deleteUser(Long id);

    SysUserVO findById(Long id);

    Page<SysUserVO> findAll(Object query);

    void updatePassword(Long id, String oldPassword, String newPassword, boolean isAdmin);

    void updateStatus(Long id, String status);

    void assignRoles(Long userId, List<Long> roleIds);
}