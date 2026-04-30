package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.SecurityContextHolder;
import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.core.domain.auth.UserService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sysUser")
public class SysUserController {

    private final UserService userService;

    public SysUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/selects")
    @RequiresPermission("system:user")
    public Result<PageResult<SysUserVO>> list(@RequestBody SysUserQueryReq query) {
        Page<SysUserVO> page = userService.findAll(query);
        return Result.ok(PageResult.ok(page));
    }

    @GetMapping("/select")
    @RequiresPermission("system:user")
    public Result<SysUserVO> getById(@RequestParam Long id) {
        SysUserVO vo = userService.findById(id);
        return Result.ok(vo);
    }

    @PostMapping("/insert")
    @RequiresPermission("system:user")
    public Result<SysUserVO> create(@RequestBody SysUserInsertReq req) {
        SysUserVO vo = userService.createUser(req);
        return Result.ok(vo);
    }

    @PutMapping("/updateIncrement")
    @RequiresPermission("system:user")
    public Result<SysUserVO> update(@RequestBody SysUserUpdateReq req) {
        SysUserVO vo = userService.updateUser(req.getId(), req);
        return Result.ok(vo);
    }

    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@RequestBody SysUserPasswordReq req) {
        Long currentUserId = SecurityContextHolder.getCurrentUser().getUserId();
        boolean isAdmin = SecurityContextHolder.getCurrentUser().getPerms().contains("system:user");
        userService.updatePassword(req.getId(), req.getOldPassword(), req.getNewPassword(), isAdmin && !req.getId().equals(currentUserId));
        return Result.ok();
    }

    @PutMapping("/updateStatus")
    @RequiresPermission("system:user")
    public Result<?> updateStatus(@RequestBody SysUserStatusReq req) {
        userService.updateStatus(req.getId(), req.getStatus());
        return Result.ok();
    }

    @DeleteMapping("/delete")
    @RequiresPermission("system:user")
    public Result<?> delete(@RequestParam Long id) {
        userService.deleteUser(id);
        return Result.ok();
    }

    @PostMapping("/assignRoles")
    @RequiresPermission("system:user")
    public Result<?> assignRoles(@RequestBody AssignRolesReq req) {
        userService.assignRoles(req.getUserId(), req.getRoleIds());
        return Result.ok();
    }
}