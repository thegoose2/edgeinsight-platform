package com.huidou.edgeinsight.common.model;

import javax.persistence.*;

@Entity
@Table(name = "sys_role_permission")
@IdClass(SysRolePermissionId.class)
public class SysRolePermission {

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Id
    @Column(name = "perm_id")
    private Long permId;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getPermId() { return permId; }
    public void setPermId(Long permId) { this.permId = permId; }
}