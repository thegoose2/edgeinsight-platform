package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "sys_permission")
public class SysPermission extends BaseEntity {

    @Column(name = "perm_code", nullable = false, unique = true)
    private String permCode;

    @Column(name = "perm_name", nullable = false)
    private String permName;

    @Enumerated(EnumType.STRING)
    @Column(name = "perm_type", nullable = false)
    private PermType permType;

    @Column(name = "module", nullable = false)
    private String module;

    @Column(name = "description")
    private String description;

    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    public enum PermType {
        MENU, OPERATION
    }

    public String getPermCode() { return permCode; }
    public void setPermCode(String permCode) { this.permCode = permCode; }
    public String getPermName() { return permName; }
    public void setPermName(String permName) { this.permName = permName; }
    public PermType getPermType() { return permType; }
    public void setPermType(PermType permType) { this.permType = permType; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
}