package com.huidou.edgeinsight.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Data
@Entity
@Table(name = "sys_role_permission")
@IdClass(SysRolePermission.SysRolePermissionId.class)
public class SysRolePermission implements Serializable {

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Id
    @Column(name = "perm_id")
    private Long permId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private SysRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perm_id", insertable = false, updatable = false)
    private SysPermission permission;

    @Data
    public static class SysRolePermissionId implements Serializable {
        private Long roleId;
        private Long permId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SysRolePermissionId that = (SysRolePermissionId) o;
            return Objects.equals(roleId, that.roleId) && Objects.equals(permId, that.permId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleId, permId);
        }
    }
}