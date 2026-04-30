package com.huidou.edgeinsight.common.model;

import java.io.Serializable;

public class SysUserRoleId implements Serializable {
    private Long userId;
    private Long roleId;

    public SysUserRoleId() {}
    public SysUserRoleId(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}