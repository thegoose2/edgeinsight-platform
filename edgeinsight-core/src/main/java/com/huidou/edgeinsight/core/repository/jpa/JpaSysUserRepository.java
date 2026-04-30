package com.huidou.edgeinsight.core.repository.jpa;

import com.huidou.edgeinsight.common.model.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaSysUserRepository extends JpaRepository<SysUser, Long> {

    Optional<SysUser> findByUsername(String username);

    @Query("SELECT u FROM SysUser u WHERE " +
           "(:keyword IS NULL OR u.username LIKE %:keyword% OR u.realName LIKE %:keyword%) " +
           "AND (:status IS NULL OR u.status = :status)")
    Page<SysUser> findAll(@Param("keyword") String keyword,
                          @Param("status") SysUser.UserStatus status,
                          Pageable pageable);
}