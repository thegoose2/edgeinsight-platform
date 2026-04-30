package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "sys_role", uniqueConstraints = {
    @UniqueConstraint(name = "uk_role_code", columnNames = "role_code")
})
public class SysRole extends AuditableEntity {

    @Column(name = "role_code", nullable = false, length = 50)
    private String roleCode;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "is_system", nullable = false)
    private Integer isSystem = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoleStatus status = RoleStatus.ACTIVE;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<SysRolePermission> rolePermissions = new ArrayList<>();

    public enum RoleStatus {
        ACTIVE, INACTIVE
    }
}