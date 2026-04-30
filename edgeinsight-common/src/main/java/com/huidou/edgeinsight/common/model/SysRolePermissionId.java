package com.huidou.edgeinsight.common.model;

import java.io.Serializable;

public class SysRolePermissionId implements Serializable {
    private Long roleId;
    private Long permId;

    public SysRolePermissionId() {}
    public SysRolePermissionId(Long roleId, Long permId) {
        this.roleId = roleId;
        this.permId = permId;
    }

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getPermId() { return permId; }
    public void setPermId(Long permId) { this.permId = permId; }
}