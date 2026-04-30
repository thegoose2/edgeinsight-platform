package com.huidou.edgeinsight.api.rest;

import com.huidou.edgeinsight.api.security.annotation.RequiresPermission;
import com.huidou.edgeinsight.common.dto.*;
import com.huidou.edgeinsight.common.model.DeviceType;
import com.huidou.edgeinsight.common.model.DeviceTypePoint;
import com.huidou.edgeinsight.core.domain.device.DeviceTypeService;
import com.huidou.edgeinsight.core.domain.device.DeviceTypeServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/deviceType")
public class DeviceTypeController {

    private final DeviceTypeServiceImpl deviceTypeService;

    public DeviceTypeController(DeviceTypeServiceImpl deviceTypeService) {
        this.deviceTypeService = deviceTypeService;
    }

    @GetMapping("/list")
    @RequiresPermission("device:type")
    public Result<List<DeviceTypeVO>> list() {
        List<DeviceType> all = deviceTypeService.findAll();
        List<DeviceTypeVO> vos = all.stream().map(deviceTypeService::toVO).collect(Collectors.toList());
        return Result.ok(vos);
    }

    @GetMapping("/select")
    @RequiresPermission("device:type")
    public Result<DeviceTypeVO> getById(@RequestParam Long id) {
        DeviceType dt = deviceTypeService.findById(id);
        return Result.ok(deviceTypeService.toVO(dt));
    }

    @PostMapping("/insert")
    @RequiresPermission("device:type")
    public Result<DeviceTypeVO> create(@RequestBody DeviceTypeInsertReq req) {
        DeviceType dt = new DeviceType();
        dt.setTypeCode(req.getTypeCode());
        dt.setName(req.getName());
        dt.setProfileType(req.getProfileType());
        dt.setDescription(req.getDescription());
        dt = deviceTypeService.save(dt);
        return Result.ok(deviceTypeService.toVO(dt));
    }

    @PutMapping("/updateIncrement")
    @RequiresPermission("device:type")
    public Result<DeviceTypeVO> update(@RequestBody DeviceTypeUpdateReq req) {
        DeviceType dt = new DeviceType();
        dt.setName(req.getName());
        dt.setDescription(req.getDescription());
        dt = deviceTypeService.update(req.getId(), dt);
        return Result.ok(deviceTypeService.toVO(dt));
    }

    @DeleteMapping("/delete")
    @RequiresPermission("device:type")
    public Result<?> delete(@RequestParam Long id) {
        deviceTypeService.delete(id);
        return Result.ok();
    }

    @GetMapping("/point/list")
    @RequiresPermission("device:type")
    public Result<List<DeviceTypePointVO>> listPoints(@RequestParam Long deviceTypeId) {
        List<DeviceTypePoint> points = deviceTypeService.getPointTemplates(deviceTypeId);
        List<DeviceTypePointVO> vos = points.stream().map(p -> {
            DeviceTypePointVO vo = new DeviceTypePointVO();
            vo.setId(p.getId());
            vo.setPointCode(p.getPointCode());
            vo.setName(p.getName());
            vo.setDataType(p.getDataType());
            vo.setUnit(p.getUnit());
            vo.setRangeMin(p.getRangeMin());
            vo.setRangeMax(p.getRangeMax());
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(vos);
    }

    @PostMapping("/point/insert")
    @RequiresPermission("device:type")
    public Result<DeviceTypePointVO> addPoint(@RequestBody PointInsertReq req) {
        DeviceTypePointVO vo = deviceTypeService.addPoint(req);
        return Result.ok(vo);
    }

    @PutMapping("/point/updateIncrement")
    @RequiresPermission("device:type")
    public Result<?> updatePoint(@RequestBody PointUpdateReq req,
                                 @RequestParam(required = false, defaultValue = "false") boolean confirm) {
        if (!confirm) {
            PointAffectPreviewVO preview = deviceTypeService.previewUpdatePoint(req.getId());
            return Result.ok(preview);
        }
        DeviceTypePointVO vo = deviceTypeService.updatePoint(req, true);
        return Result.ok(vo);
    }

    @DeleteMapping("/point/delete")
    @RequiresPermission("device:type")
    public Result<?> deletePoint(@RequestParam Long id,
                                 @RequestParam(required = false, defaultValue = "false") boolean confirm) {
        if (!confirm) {
            PointAffectPreviewVO preview = deviceTypeService.previewDeletePoint(id);
            return Result.ok(preview);
        }
        deviceTypeService.deletePoint(id, true);
        return Result.ok();
    }
}