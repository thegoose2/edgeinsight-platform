package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class SysRoleVO {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private Boolean isSystem;
    private String status;
    private List<Long> permIds;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getIsSystem() { return isSystem; }
    public void setIsSystem(Boolean isSystem) { this.isSystem = isSystem; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<Long> getPermIds() { return permIds; }
    public void setPermIds(List<Long> permIds) { this.permIds = permIds; }
}