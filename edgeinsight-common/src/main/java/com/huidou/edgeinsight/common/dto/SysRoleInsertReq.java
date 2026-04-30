package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class SysRoleInsertReq {
    private String roleCode;
    private String roleName;
    private String description;
    private List<Long> permIds;

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Long> getPermIds() { return permIds; }
    public void setPermIds(List<Long> permIds) { this.permIds = permIds; }
}