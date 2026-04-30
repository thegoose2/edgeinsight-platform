package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.core.domain.auth.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sysRole")
public class SysRoleController {

    private final RoleService roleService;

    public SysRoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/list")
    @RequiresPermission("system:role")
    public Result<List<SysRoleVO>> list() {
        List<SysRoleVO> vo = roleService.findAll();
        return Result.ok(vo);
    }

    @GetMapping("/select")
    @RequiresPermission("system:role")
    public Result<SysRoleVO> getById(@RequestParam Long id) {
        SysRoleVO vo = roleService.findById(id);
        return Result.ok(vo);
    }

    @PostMapping("/insert")
    @RequiresPermission("system:role")
    public Result<SysRoleVO> create(@RequestBody SysRoleInsertReq req) {
        SysRoleVO vo = roleService.createRole(req);
        return Result.ok(vo);
    }

    @PutMapping("/updateIncrement")
    @RequiresPermission("system:role")
    public Result<SysRoleVO> update(@RequestBody SysRoleUpdateReq req) {
        SysRoleVO vo = roleService.updateRole(req.getId(), req);
        return Result.ok(vo);
    }

    @DeleteMapping("/delete")
    @RequiresPermission("system:role")
    public Result<?> delete(@RequestParam Long id) {
        roleService.deleteRole(id);
        return Result.ok();
    }

    @PostMapping("/assignPermissions")
    @RequiresPermission("system:role")
    public Result<?> assignPermissions(@RequestBody AssignPermissionsReq req) {
        roleService.assignPermissions(req.getRoleId(), req.getPermIds());
        return Result.ok();
    }
}