package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class AssignPermissionsReq {
    private Long roleId;
    private List<Long> permIds;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public List<Long> getPermIds() { return permIds; }
    public void setPermIds(List<Long> permIds) { this.permIds = permIds; }
}