package com.huidou.edgeinsight.core.repository.spi;

import com.huidou.edgeinsight.common.model.SysUser;

import java.util.Optional;

public interface SysUserRepository {

    Optional<SysUser> findByUsername(String username);

    Optional<SysUser> findByUsernameWithRoles(String username);

    java.util.Set<String> findPermissionCodesByUsername(String username);

    SysUser save(SysUser user);
}