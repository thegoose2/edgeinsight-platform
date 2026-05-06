package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.annotation.Anonymous;
import com.huidou.edgeinsight.common.dto.Result;
import com.huidou.edgeinsight.common.dto.insert.InsertRequest;
import com.huidou.edgeinsight.common.model.SysRole;
import com.huidou.edgeinsight.common.model.SysUser;
import com.huidou.edgeinsight.core.repository.spi.SysRoleRepository;
import com.huidou.edgeinsight.core.repository.spi.SysUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/sysUser")
public class SysUserController {

    private final SysUserRepository sysUserRepository;
    private final SysRoleRepository sysRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public SysUserController(SysUserRepository sysUserRepository,
                             SysRoleRepository sysRoleRepository,
                             PasswordEncoder passwordEncoder) {
        this.sysUserRepository = sysUserRepository;
        this.sysRoleRepository = sysRoleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public Result<?> list() {
        return Result.ok();
    }

    @GetMapping("/select/{id}")
    public Result<?> Select(@PathVariable Long id) {
        return Result.ok();
    }

    @Anonymous
    @Transactional
    @PostMapping("/insert")
    public Result<?> Insert(@RequestBody InsertRequest request) {
        SysUser user = new SysUser();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setStatus(SysUser.UserStatus.ACTIVE);
        user.setLastLoginAt(LocalDateTime.now());

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            List<Long> roleIds = request.getRoleIds().stream()
                    .map(Integer::longValue)
                    .collect(Collectors.toList());
            Set<SysRole> roles = new HashSet<>(sysRoleRepository.findAllById(roleIds));
            user.setRoles(roles);
        }

        sysUserRepository.save(user);
        return Result.ok();
    }

    @PutMapping("/updateIncrement")
    public Result<?> updateIncrement(@PathVariable Long id, @RequestBody Object body) {
        return Result.ok();
    }

    @PutMapping("/updatePassword")
    public Result<?> updatePassword(@PathVariable Long id, @RequestBody Object body) {

        return Result.ok();
    }

    @DeleteMapping("/updateStatus")
    public Result<?> updateStatus(@PathVariable Long id) {

        return Result.ok();
    }
}
