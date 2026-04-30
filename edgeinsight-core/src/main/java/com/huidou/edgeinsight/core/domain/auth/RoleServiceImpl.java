package com.huidou.edgeinsight.core.domain.auth;

import com.huidou.edgeinsight.common.dto.AssignPermissionsReq;
import com.huidou.edgeinsight.common.dto.SysRoleInsertReq;
import com.huidou.edgeinsight.common.dto.SysRoleUpdateReq;
import com.huidou.edgeinsight.common.dto.SysRoleVO;
import com.huidou.edgeinsight.common.exception.BusinessException;
import com.huidou.edgeinsight.common.exception.NotFoundException;
import com.huidou.edgeinsight.common.model.SysRole;
import com.huidou.edgeinsight.common.model.SysRolePermission;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysPermissionRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysRolePermissionRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysRoleRepository;
import com.huidou.edgeinsight.core.repository.jpa.JpaSysUserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final JpaSysRoleRepository roleRepository;
    private final JpaSysRolePermissionRepository rolePermissionRepository;
    private final JpaSysPermissionRepository permissionRepository;
    private final JpaSysUserRoleRepository userRoleRepository;

    public RoleServiceImpl(JpaSysRoleRepository roleRepository,
                           JpaSysRolePermissionRepository rolePermissionRepository,
                           JpaSysPermissionRepository permissionRepository,
                           JpaSysUserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public SysRoleVO createRole(Object reqObj) {
        SysRoleInsertReq req = (SysRoleInsertReq) reqObj;

        SysRole role = new SysRole();
        role.setRoleCode(req.getRoleCode());
        role.setRoleName(req.getRoleName());
        role.setDescription(req.getDescription());
        role = roleRepository.save(role);

        if (req.getPermIds() != null && !req.getPermIds().isEmpty()) {
            for (Long permId : req.getPermIds()) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(role.getId());
                rp.setPermId(permId);
                rolePermissionRepository.save(rp);
            }
        }

        return toVO(role);
    }

    @Override
    @Transactional
    public SysRoleVO updateRole(Long id, Object reqObj) {
        SysRoleUpdateReq req = (SysRoleUpdateReq) reqObj;

        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found: " + id));

        if (req.getRoleName() != null) role.setRoleName(req.getRoleName());
        if (req.getDescription() != null) role.setDescription(req.getDescription());

        roleRepository.save(role);
        return toVO(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found: " + id));

        if (Boolean.TRUE.equals(role.getIsSystem())) {
            throw new BusinessException("Cannot delete system built-in role");
        }

        long userCount = userRoleRepository.findRoleIdsByUserId(id).size();
        if (userCount > 0) {
            throw new BusinessException("Cannot delete role with " + userCount + " associated users");
        }

        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.delete(role);
    }

    @Override
    public SysRoleVO findById(Long id) {
        SysRole role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Role not found: " + id));
        return toVO(role);
    }

    @Override
    public List<SysRoleVO> findAll() {
        return roleRepository.findAll().stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignPermissions(Long roleId, List<Long> permIds) {
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role not found: " + roleId);
        }

        rolePermissionRepository.deleteByRoleId(roleId);

        if (permIds != null) {
            for (Long permId : permIds) {
                if (!permissionRepository.existsById(permId)) {
                    throw new NotFoundException("Permission not found: " + permId);
                }
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(roleId);
                rp.setPermId(permId);
                rolePermissionRepository.save(rp);
            }
        }
    }

    private SysRoleVO toVO(SysRole role) {
        SysRoleVO vo = new SysRoleVO();
        vo.setId(role.getId());
        vo.setRoleCode(role.getRoleCode());
        vo.setRoleName(role.getRoleName());
        vo.setDescription(role.getDescription());
        vo.setIsSystem(role.getIsSystem());
        vo.setStatus(role.getStatus().name());

        List<Long> permIds = rolePermissionRepository.findPermIdsByRoleId(role.getId());
        vo.setPermIds(permIds);

        return vo;
    }
}