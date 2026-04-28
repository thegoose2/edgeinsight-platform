package com.huidou.edgeinsight.core.domain.auth;

import java.util.List;

public interface RoleService {

    Object createRole(Object role);

    Object updateRole(Long id, Object role);

    void deleteRole(Long id);

    Object findById(Long id);

    List<Object> findAll();

    void assignPermissions(Long roleId, List<Long> permissionIds);
}
