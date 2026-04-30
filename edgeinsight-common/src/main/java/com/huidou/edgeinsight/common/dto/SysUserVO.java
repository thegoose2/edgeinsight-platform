package com.huidou.edgeinsight.common.dto;

import java.time.LocalDateTime;
import java.util.List;

public class SysUserVO {
    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String status;
    private List<RoleVO> roles;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<RoleVO> getRoles() { return roles; }
    public void setRoles(List<RoleVO> roles) { this.roles = roles; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static class RoleVO {
        private Long id;
        private String roleName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getRoleName() { return roleName; }
        public void setRoleName(String roleName) { this.roleName = roleName; }
    }
}