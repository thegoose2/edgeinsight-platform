package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.dto.PermissionGroupVO;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.dto.SysPermissionVO;
import com.huidou.edgeinsight.common.model.SysPermission;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysPermissionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sysPermission")
public class SysPermissionController {

    private final JpaSysPermissionRepository permissionRepository;

    public SysPermissionController(JpaSysPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping("/list")
    @RequiresPermission("system:role")
    public Result<List<PermissionGroupVO>> list() {
        List<SysPermission> allPerms = permissionRepository.findAll();

        Map<String, List<SysPermission>> grouped = allPerms.stream()
                .collect(Collectors.groupingBy(SysPermission::getModule));

        List<PermissionGroupVO> result = new ArrayList<>();
        for (Map.Entry<String, List<SysPermission>> entry : grouped.entrySet()) {
            PermissionGroupVO group = new PermissionGroupVO();
            group.setModule(entry.getKey());
            List<SysPermissionVO> permVOs = entry.getValue().stream().map(p -> {
                SysPermissionVO vo = new SysPermissionVO();
                vo.setId(p.getId());
                vo.setPermCode(p.getPermCode());
                vo.setPermName(p.getPermName());
                vo.setPermType(p.getPermType().name());
                vo.setModule(p.getModule());
                vo.setSort(p.getSort());
                return vo;
            }).collect(Collectors.toList());
            group.setPermissions(permVOs);
            result.add(group);
        }

        return Result.ok(result);
    }
}