package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysRolePermission;
import com.huidou.edgeinsight.common.model.SysRolePermissionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSysRolePermissionRepository extends JpaRepository<SysRolePermission, SysRolePermissionId> {

    @Query("SELECT rp.permId FROM SysRolePermission rp WHERE rp.roleId = :roleId")
    List<Long> findPermIdsByRoleId(@Param("roleId") Long roleId);

    @Modifying
    @Query("DELETE FROM SysRolePermission rp WHERE rp.roleId = :roleId")
    void deleteByRoleId(@Param("roleId") Long roleId);

    long countByRoleId(Long roleId);
}