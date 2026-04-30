package com.huidou.edgeinsight.common.dto;

import java.util.List;

public class PermissionGroupVO {
    private String module;
    private List<SysPermissionVO> permissions;

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public List<SysPermissionVO> getPermissions() { return permissions; }
    public void setPermissions(List<SysPermissionVO> permissions) { this.permissions = permissions; }
}