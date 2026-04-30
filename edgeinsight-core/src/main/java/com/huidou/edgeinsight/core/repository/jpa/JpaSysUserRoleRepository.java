package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysUserRole;
import com.huidou.edgeinsight.common.model.SysUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaSysUserRoleRepository extends JpaRepository<SysUserRole, SysUserRoleId> {

    @Query("SELECT r.roleId FROM SysUserRole r WHERE r.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM SysUserRole r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}