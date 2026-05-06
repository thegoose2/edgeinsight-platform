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
@Table(name = "sys_user", uniqueConstraints = {
    @UniqueConstraint(name = "uk_username", columnNames = "username")
})
public class SysUser extends AuditableEntity {

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "is_system", nullable = false)
    private Integer isSystem = 0;

    @Column(name = "last_login_at")
    private java.time.LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sys_user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<SysRole> roles = new HashSet<>();

    public enum UserStatus {
        ACTIVE, INACTIVE
    }
}