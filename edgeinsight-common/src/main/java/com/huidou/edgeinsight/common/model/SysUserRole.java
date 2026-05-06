package com.huidou.edgeinsight.common.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "sys_user_role")
@IdClass(SysUserRole.SysUserRoleId.class)
public class SysUserRole implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Data
    public static class SysUserRoleId implements Serializable {
        private Long userId;
        private Long roleId;
    }
}