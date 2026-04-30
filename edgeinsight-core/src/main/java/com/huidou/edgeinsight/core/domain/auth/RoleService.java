package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.SysRoleVO;

import java.util.List;

public interface RoleService {

    SysRoleVO createRole(Object role);

    SysRoleVO updateRole(Long id, Object role);

    void deleteRole(Long id);

    SysRoleVO findById(Long id);

    List<SysRoleVO> findAll();

    void assignPermissions(Long roleId, List<Long> permissionIds);
}