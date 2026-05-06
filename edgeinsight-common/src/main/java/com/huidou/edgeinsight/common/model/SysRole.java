package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.AuditableEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_role_permission",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "perm_id"))
    private Set<SysPermission> permissions = new HashSet<>();

    public enum RoleStatus {
        ACTIVE, INACTIVE
    }
}