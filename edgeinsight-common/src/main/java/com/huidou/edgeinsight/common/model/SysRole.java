package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;

import javax.persistence.*;

@Entity
@Table(name = "sys_role")
public class SysRole extends AuditableEntity {

    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    @Column(name = "role_name", nullable = false)
    private String roleName;

    @Column(name = "description")
    private String description;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoleStatus status = RoleStatus.ACTIVE;

    public enum RoleStatus {
        ACTIVE, INACTIVE
    }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getIsSystem() { return isSystem; }
    public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }
    public RoleStatus getStatus() { return status; }
    public void setStatus(RoleStatus status) { this.status = status; }
}