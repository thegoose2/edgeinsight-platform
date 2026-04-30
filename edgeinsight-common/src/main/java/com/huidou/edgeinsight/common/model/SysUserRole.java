package com.huidou.edgeinsight.common.model;

import javax.persistence.*;

@Entity
@Table(name = "sys_user_role")
@IdClass(SysUserRoleId.class)
public class SysUserRole {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}