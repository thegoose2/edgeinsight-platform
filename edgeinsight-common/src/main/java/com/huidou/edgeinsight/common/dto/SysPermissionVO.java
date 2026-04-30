package com.huidou.edgeinsight.common.dto;

public class SysPermissionVO {
    private Long id;
    private String permCode;
    private String permName;
    private String permType;
    private String module;
    private Integer sort;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPermCode() { return permCode; }
    public void setPermCode(String permCode) { this.permCode = permCode; }
    public String getPermName() { return permName; }
    public void setPermName(String permName) { this.permName = permName; }
    public String getPermType() { return permType; }
    public void setPermType(String permType) { this.permType = permType; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}