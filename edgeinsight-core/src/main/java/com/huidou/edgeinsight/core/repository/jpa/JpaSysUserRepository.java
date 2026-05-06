package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSysUserRepository extends JpaRepository<SysUser, Long>, SysUserRepository {

    Optional<SysUser> findByUsername(String username);

    @Query("SELECT DISTINCT u FROM SysUser u " +
           "LEFT JOIN FETCH u.roles r " +
           "WHERE u.username = :username")
    Optional<SysUser> findByUsernameWithRoles(@Param("username") String username);

    @Query("SELECT DISTINCT p.permCode FROM SysUser u " +
           "JOIN u.roles r " +
           "JOIN r.permissions p " +
           "WHERE u.username = :username")
    java.util.Set<String> findPermissionCodesByUsername(@Param("username") String username);
}