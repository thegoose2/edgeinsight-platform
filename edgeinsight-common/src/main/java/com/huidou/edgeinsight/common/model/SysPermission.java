package com.huidou.edgeinsight.common.model;

import com.huidou.edgeinsight.common.model.base.BaseEntity;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Data
@Entity
@Table(name = "sys_permission", uniqueConstraints = {
    @UniqueConstraint(name = "uk_perm_code", columnNames = "perm_code")
})
public class SysPermission extends BaseEntity {

    @Column(name = "perm_code", nullable = false, length = 100)
    private String permCode;

    @Column(name = "perm_name", nullable = false, length = 50)
    private String permName;

    @Enumerated(EnumType.STRING)
    @Column(name = "perm_type", nullable = false)
    private PermType permType;

    @Column(name = "module", nullable = false, length = 50)
    private String module;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "sort", nullable = false)
    private Integer sort = 0;

    public enum PermType {
        MENU, OPERATION
    }
}