package com.huidou.edgeinsight.common.dto;

public class SysUserQueryReq extends BaseQuery {
    private String keyword;
    private String status;
    private Long roleId;

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
}