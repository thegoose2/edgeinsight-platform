# EdgeInsight Platform · 系统架构设计

## 05 API 接口设计

| 文档版本 | V1.0 |
|------|------|
| 创建日期 | 2026年4月 |

---

## 1. 接口规范

### 1.1 风格

采用 **RPC 风格**，路径明确表达操作意图。

### 1.2 基础约定

| 项目 | 规范 |
|------|------|
| Base Path | `/api/v1` |
| 数据格式 | JSON，Content-Type: application/json |
| 鉴权方式 | JWT Token（Header: `Authorization: Bearer {token}`），实现详见文档 08；登录接口 `/api/v1/auth/login` 和健康检查接口无需 Token |
| 时间格式 | ISO 8601，如 `2026-03-01T10:23:00.000Z` |
| 分页参数 | `pageNum`（从1开始）、`pageSize`（默认20） |

### 1.3 统一响应结构

```json
{
  "code":    200,
  "message": "success",
  "data":    { }
}
```

**错误码约定：**

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 409 | 冲突（如 device_code 重复） |
| 500 | 服务器内部错误 |

---

## 2. 协议档案接口

> **相关类**：`ProtocolProfileVO`（响应）、`ProtocolProfileInsertReq`（新增请求）、`ProtocolProfileUpdateReq`（修改请求）

### 2.1 查询协议档案列表

```
GET /api/v1/protocolProfile/list
```

响应：

```json
{
  "code": 200,
  "data": [
    {
      "profileType":    "INTGATHER-V2",
      "protocol":       "MQTT",
      "parserId":       "intgather-v2-default",
      "description":    "智采极 V2 标准协议"
    }
  ]
}
```

### 2.2 查询协议档案详情

```
GET /api/v1/protocolProfile/select?profileType=INTGATHER-V2
```

响应：包含完整字段，含 `topicPatterns`、`msgTypeMapping`、`frameStrategy`。

### 2.3 新增协议档案

```
POST /api/v1/protocolProfile/insert
```

请求体：

```json
{
  "profileType":      "TCP-DEVICE-A",
  "protocol":         "TCP",
  "parserId":         "project-x-device-a-v1",
  "frameStrategy":    "LENGTH_PREFIX",
  "description":      "项目X TCP直连设备"
}
```

### 2.4 修改协议档案

```
PUT /api/v1/protocolProfile/updateIncrement
```

请求体：包含 `profileType`（必填，定位记录）+ 需要修改的字段。

---

## 3. 设备类型接口

> **相关类**：`DeviceTypeVO`（列表响应）、`DeviceTypeDetailVO`（详情响应，含 `points` 数组）、`DeviceTypeInsertReq`（新增）、`DeviceTypeUpdateReq`（修改）

### 3.1 查询设备类型列表

```
GET /api/v1/deviceType/list
```

响应：

```json
{
  "code": 200,
  "data": [
    {
      "id":          1,
      "typeCode":    "CNC-MAZAK-V3",
      "name":        "马扎克 CNC V3",
      "profileType": "INTGATHER-V2",
      "description": "",
      "deviceCount": 10
    }
  ]
}
```

`deviceCount`：关联的设备数量，用于删除时的前置判断。

### 3.2 查询设备类型详情（含测点模板）

```
GET /api/v1/deviceType/select?id=1
```

响应：包含设备类型基础信息 + `points` 数组（测点模板列表）。

### 3.3 新增设备类型

```
POST /api/v1/deviceType/insert
```

请求体：

```json
{
  "typeCode":    "CNC-MAZAK-V3",
  "name":        "马扎克 CNC V3",
  "profileType": "INTGATHER-V2",
  "description": ""
}
```

### 3.4 修改设备类型基础信息

```
PUT /api/v1/deviceType/updateIncrement
```

请求体：包含 `id`（必填）+ 需要修改的字段（`name`、`description`，`typeCode` 和 `profileType` 不允许修改）。

### 3.5 删除设备类型

```
DELETE /api/v1/deviceType/delete?id=1
```

**删除规则：**
- 有关联设备（`deviceCount > 0`）：返回 409，提示"该类型下有 N 台设备，无法删除"
- 无关联设备：直接删除，级联删除其所有 `device_type_point` 记录

---

## 4. 测点模板接口

> **相关类**：`DeviceTypePointVO`（响应）、`PointInsertReq`（新增）、`PointUpdateReq`（修改）、`PointAffectPreviewVO`（二次确认预览响应）

### 4.1 查询测点模板列表

```
GET /api/v1/deviceType/point/list?deviceTypeId=1
```

响应：包含所有 `is_active = 1` 的测点，按 `point_code` 排序。

### 4.2 新增测点模板

```
POST /api/v1/deviceType/point/insert
```

请求体：

```json
{
  "deviceTypeId": 1,
  "pointCode":    "temp_inlet",
  "name":         "进口温度",
  "dataType":     "DOUBLE",
  "unit":         "℃",
  "rangeMin":     -20,
  "rangeMax":     150
}
```

新增测点立即对所有该类型 ACTIVE 设备生效，无需二次确认。

### 4.3 修改测点模板（需二次确认）

```
PUT /api/v1/deviceType/point/updateIncrement
PUT /api/v1/deviceType/point/updateIncrement?confirm=true
```

**两步操作：**

第一步（不带 `confirm`）：返回影响设备数量，不执行修改。

```json
{
  "code": 200,
  "data": {
    "affectedDeviceCount": 10,
    "message": "此操作将影响 10 台运行中设备，请确认后继续"
  }
}
```

第二步（带 `confirm=true`）：执行修改，同时清除受影响设备的 ResolvedContext 缓存。

### 4.4 删除测点模板（需二次确认）

```
DELETE /api/v1/deviceType/point/delete?id=1
DELETE /api/v1/deviceType/point/delete?id=1&confirm=true
```

**删除规则：**
- 第一步同修改，返回影响设备数量
- 若该 `point_code` 有 `telemetry_record` 历史数据：仅软删除（`is_active = 0`），保留历史数据可查
- 若无历史数据：物理删除

---

## 5. 设备接口

> **相关类**：`DeviceVO`（列表响应，含在线状态）、`DeviceDetailVO`（详情，含 `points` 数组）、`DeviceInsertReq`（注册）、`DeviceUpdateReq`（修改）、`DeviceLifecycleReq`（切换状态）、`DeviceQueryReq extends BaseQuery`（列表查询，含过滤条件）、`DeviceImportResultVO`（批量导入响应）

### 5.1 注册设备

```
POST /api/v1/device/insert
```

请求体：

```json
{
  "deviceCode":    "CNC-001",
  "name":          "1号加工中心",
  "location":      "A车间-1号产线",
  "connectId":     "INTGATHER-SN-001",
  "deviceTypeId":  1
}
```

注册成功后系统自动：
1. 创建 `device_status` 初始记录（`online_status = OFFLINE`，所有时间字段 = NULL）
2. 初始化 `ResolvedContext` 并加入缓存

### 5.2 查询设备列表

```
POST /api/v1/device/selects
```

请求体（支持多条件过滤）：

```json
{
  "deviceTypeId":    1,
  "lifecycleStatus": "ACTIVE",
  "onlineStatus":    "ONLINE",
  "keyword":         "CNC",
  "pageNum":         1,
  "pageSize":        20
}
```

响应（直接包含在线状态，不需要额外请求）：

```json
{
  "code": 200,
  "data": {
    "total": 50,
    "list": [
      {
        "id":              1,
        "deviceCode":      "CNC-001",
        "name":            "1号加工中心",
        "location":        "A车间-1号产线",
        "deviceTypeName":  "马扎克 CNC V3",
        "lifecycleStatus": "ACTIVE",
        "onlineStatus":    "ONLINE",
        "connectedAt":     "2026-03-01T08:00:00.000Z",
        "lastSeenAt":      "2026-03-01T10:23:00.000Z"
      }
    ]
  }
}
```

### 5.3 查询设备详情

```
GET /api/v1/device/select?id=1
```

响应：包含设备基础信息 + 在线状态 + 测点列表（来自 `device_type_point`）：

```json
{
  "code": 200,
  "data": {
    "id":              1,
    "deviceCode":      "CNC-001",
    "name":            "1号加工中心",
    "location":        "A车间-1号产线",
    "connectId":       "INTGATHER-SN-001",
    "deviceTypeId":    1,
    "deviceTypeName":  "马扎克 CNC V3",
    "profileType":     "INTGATHER-V2",
    "lifecycleStatus": "ACTIVE",
    "onlineStatus":    "ONLINE",
    "connectedAt":     "2026-03-01T08:00:00.000Z",
    "disconnectedAt":  null,
    "lastSeenAt":      "2026-03-01T10:23:00.000Z",
    "points": [
      {
        "pointCode": "temp_inlet",
        "name":      "进口温度",
        "dataType":  "DOUBLE",
        "unit":      "℃",
        "rangeMin":  -20,
        "rangeMax":  150
      }
    ]
  }
}
```

### 5.4 修改设备信息

```
PUT /api/v1/device/updateIncrement
```

请求体：包含 `id`（必填）+ 可修改字段说明如下：

| 字段 | 是否可修改 | 备注 |
|------|------|------|
| `name` | ✅ 是 | 无额外操作 |
| `location` | ✅ 是 | 无额外操作 |
| `connectId` | ✅ 是 | 修改后必须清除旧 `connectId` 的 `ResolvedContext` 缓存，新 `connectId` 在下次连接时懒加载 |
| `deviceTypeId` | ✅ 是 | 修改后必须清除该设备的 `ResolvedContext` 缓存（`validPointCodes` 和 `parserId` 均来自 `deviceTypeId`）；同时校验新 `deviceTypeId` 存在 |
| `deviceCode` | ❌ 否 | 业务唯一标识，不允许修改 |

### 5.5 切换生命周期状态

```
PUT /api/v1/device/updateLifecycle
```

请求体：

```json
{
  "id":     1,
  "status": "INACTIVE"
}
```

操作完成后清除对应 ResolvedContext 缓存。

### 5.6 批量导入设备

```
POST /api/v1/device/import
Content-Type: multipart/form-data
```

请求：`file` 字段，Excel 文件（.xlsx）

**Excel 模板字段：**

| 列名 | 说明 | 必填 |
|------|------|------|
| device_code | 设备编码 | 是 |
| name | 设备名称 | 是 |
| type_code | 设备类型编码（对应 device_type.type_code） | 是 |
| connect_id | 接入标识（网关 SN 等） | 是 |
| location | 安装位置 | 否 |

**导入策略：部分成功**（不因个别行失败中断整批）

响应：

```json
{
  "code": 200,
  "data": {
    "successCount": 48,
    "failCount":    2,
    "errors": [
      { "row": 3,  "reason": "device_type_code 不存在: CNC-UNKNOWN" },
      { "row": 17, "reason": "connect_id 已存在: INTGATHER-SN-005" }
    ]
  }
}
```

---

## 6. 遥测数据接口

> **相关类**：`TelemetryLatestVO`（最新值响应，含 `values` 数组）、`TelemetryPointValueVO`（单测点最新值）、`TelemetryHistoryQueryReq extends BaseQuery`（历史查询请求）、`TelemetryRecordVO`（历史记录单条）

### 6.1 查询所有测点最新值

```
GET /api/v1/telemetry/selectLatest?deviceId=1
```

响应（看板首屏加载用）：

```json
{
  "code": 200,
  "data": {
    "deviceId": 1,
    "values": [
      {
        "pointCode": "temp_inlet",
        "name":      "进口温度",
        "value":     85.6,
        "unit":      "℃",
        "ts":        "2026-03-01T10:23:00.123Z"
      }
    ]
  }
}
```

### 6.2 查询单测点历史数据

```
POST /api/v1/telemetry/selectHistory
```

请求体：

```json
{
  "deviceId":  1,
  "pointCode": "temp_inlet",
  "from":      "2026-03-01T00:00:00.000Z",
  "to":        "2026-03-01T23:59:59.999Z",
  "pageNum":   1,
  "pageSize":  500
}
```

> **排序**：结果按采集时间戳 `ts` **升序（ASC）** 返回，不支持客户端指定排序方向。

响应：

```json
{
  "code": 200,
  "data": {
    "total": 86400,
    "list": [
      { "ts": "2026-03-01T00:00:01.000Z", "value": 82.1 },
      { "ts": "2026-03-01T00:00:02.000Z", "value": 82.3 }
    ]
  }
}
```

---

## 7. 在离线接口

### 7.1 查询设备历史连接记录

```
POST /api/v1/device/connectivity/list
```

请求体：

```json
{
  "deviceId": 1,
  "from":     "2026-03-01T00:00:00.000Z",
  "to":       "2026-03-31T23:59:59.999Z",
  "pageNum":  1,
  "pageSize": 20
}
```

响应：

```json
{
  "code": 200,
  "data": {
    "total": 5,
    "list": [
      {
        "wentOfflineAt":   "2026-03-01T02:13:00.000Z",
        "cameOnlineAt":    "2026-03-01T02:13:45.000Z",
        "offlineDurationS": 45,
        "onlineDurationS":  7380,
        "cause":           "MQTT_DISCONNECT"
      }
    ]
  }
}
```

### 7.2 查询设备连接统计

```
GET /api/v1/device/connectivity/selectStats?deviceId=1&from=2026-03-01T00:00:00.000Z&to=2026-03-31T23:59:59.999Z
```

响应：

```json
{
  "code": 200,
  "data": {
    "deviceId":          1,
    "deviceName":        "1号加工中心",
    "periodSeconds":     2678400,
    "onlineTotalS":      2677200,
    "offlineTotalS":     1200,
    "offlineCount":      5,
    "avgOfflineS":       240,
    "maxOfflineS":       480,
    "availabilityRate":  0.9996
  }
}
```

---

## 8. 系统配置接口

### 8.1 查询系统配置列表

```
GET /api/v1/systemConfig/list
```

响应：

```json
{
  "code": 200,
  "data": [
    {
      "configKey":   "mqtt.grace_period_secs",
      "configValue": "30",
      "description": "MQTT 断开后等待重连的宽限时间（秒）",
      "updatedAt":   "2026-03-01T00:00:00.000Z",
      "updatedBy":   "admin"
    }
  ]
}
```

### 8.2 修改系统配置

```
PUT /api/v1/systemConfig/updateIncrement
```

请求体：

```json
{
  "configKey":   "mqtt.grace_period_secs",
  "configValue": "60",
  "updatedBy":   "admin"
}
```

修改后同步刷新内存缓存，对新建连接立即生效，已有连接的宽限期计时不受影响。

---

## 9. 认证接口

### 9.1 登录

```
POST /api/v1/auth/login
```

此接口无需 Token，其余所有接口均需在 Header 中携带 `Authorization: Bearer {token}`。

请求体：

```json
{
  "username": "admin",
  "password": "123456"
}
```

响应：

```json
{
  "code": 200,
  "data": {
    "token":    "eyJhbGciOiJIUzI1NiJ9...",
    "expireAt": "2026-03-01T18:00:00.000Z",
    "userInfo": {
      "userId":   1,
      "username": "admin",
      "realName": "张三",
      "roles":    ["ROLE_ADMIN"],
      "perms":    ["system:user", "device:manage", "control:startstop"]
    }
  }
}
```

### 9.2 登出

```
POST /api/v1/auth/logout
```

后端清除 SecurityContext，前端负责删除本地存储的 Token。响应 `code: 200` 即可。

### 9.3 获取当前用户信息

```
GET /api/v1/auth/getUserInfo
```

从当前 Token 解析用户信息返回，无需查库。响应结构同登录响应中的 `userInfo`。

---

## 10. 用户管理接口

所需权限：`system:user`

### 10.1 查询用户列表

```
POST /api/v1/sysUser/selects
```

请求体：

```json
{
  "keyword":  "张",
  "status":   "ACTIVE",
  "roleId":   1,
  "pageNum":  1,
  "pageSize": 20
}
```

响应：

```json
{
  "code": 200,
  "data": {
    "total": 10,
    "list": [
      {
        "id":          1,
        "username":    "admin",
        "realName":    "张三",
        "phone":       "13800138000",
        "status":      "ACTIVE",
        "roles":       [{"id": 1, "roleName": "系统管理员"}],
        "lastLoginAt": "2026-03-01T10:00:00.000Z",
        "createdAt":   "2026-01-01T00:00:00.000Z"
      }
    ]
  }
}
```

### 10.2 查询用户详情

```
GET /api/v1/sysUser/select?id=1
```

### 10.3 新增用户

```
POST /api/v1/sysUser/insert
```

请求体：

```json
{
  "username": "operator01",
  "password": "Init@123456",
  "realName": "李四",
  "phone":    "13900139000",
  "email":    "lisi@example.com",
  "roleIds":  [2]
}
```

### 10.4 修改用户信息

```
PUT /api/v1/sysUser/updateIncrement
```

请求体：包含 `id`（必填）+ 可修改字段（`realName`、`phone`、`email`）。`username` 不允许修改。

### 10.5 修改密码

```
PUT /api/v1/sysUser/updatePassword
```

请求体：

```json
{
  "id":          1,
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

管理员重置他人密码时无需传 `oldPassword`，需具备 `system:user` 权限。

### 10.6 启用/停用用户

```
PUT /api/v1/sysUser/updateStatus
```

请求体：

```json
{
  "id":     2,
  "status": "INACTIVE"
}
```

不允许停用当前登录用户自己。

### 10.7 删除用户

```
DELETE /api/v1/sysUser/delete?id=2
```

不允许删除内置管理员账号（`is_system = 1` 的用户）。

### 10.8 分配角色

```
POST /api/v1/sysUser/assignRoles
```

请求体：

```json
{
  "userId":  2,
  "roleIds": [2, 3]
}
```

全量替换，传入的 `roleIds` 即为该用户最终拥有的角色列表。

---

## 11. 角色管理接口

所需权限：`system:role`

### 11.1 查询角色列表

```
GET /api/v1/sysRole/list
```

响应：返回所有角色，含 `isSystem` 字段，前端据此控制删除按钮可见性。

### 11.2 查询角色详情（含权限列表）

```
GET /api/v1/sysRole/select?id=1
```

响应包含该角色已分配的权限码列表。

### 11.3 新增角色

```
POST /api/v1/sysRole/insert
```

请求体：

```json
{
  "roleCode":    "ROLE_CUSTOM",
  "roleName":    "自定义角色",
  "description": "具有部分设备管理权限",
  "permIds":     [1, 2, 5]
}
```

### 11.4 修改角色基础信息

```
PUT /api/v1/sysRole/updateIncrement
```

请求体：包含 `id`（必填）+ 可修改字段（`roleName`、`description`）。`roleCode` 不允许修改。

### 11.5 删除角色

```
DELETE /api/v1/sysRole/delete?id=5
```

**删除规则：**
- `is_system = 1` 的内置角色不允许删除
- 有关联用户的角色不允许删除，返回提示"该角色下有 N 名用户，请先解除关联"

### 11.6 分配权限

```
POST /api/v1/sysRole/assignPermissions
```

请求体：

```json
{
  "roleId":  5,
  "permIds": [1, 3, 5, 8]
}
```

全量替换。分配完成后，当前已登录用户的权限变更在下次登录后生效（Token 中的 perms 不实时更新）。

---

## 12. 权限查询接口

### 12.1 查询全部权限列表

```
GET /api/v1/sysPermission/list
```

供角色配置时的权限选择，按 `module` 分组返回：

```json
{
  "code": 200,
  "data": [
    {
      "module": "系统管理",
      "permissions": [
        {"id": 1, "permCode": "system:user",   "permName": "用户管理",   "permType": "MENU"},
        {"id": 2, "permCode": "system:role",   "permName": "角色管理",   "permType": "MENU"},
        {"id": 3, "permCode": "system:config", "permName": "系统配置",   "permType": "MENU"}
      ]
    },
    {
      "module": "设备管理",
      "permissions": [
        {"id": 4, "permCode": "device:type",      "permName": "设备类型管理", "permType": "MENU"},
        {"id": 5, "permCode": "device:manage",    "permName": "设备管理",     "permType": "MENU"},
        {"id": 6, "permCode": "device:lifecycle", "permName": "切换生命周期", "permType": "OPERATION"},
        {"id": 7, "permCode": "device:import",    "permName": "批量导入",     "permType": "OPERATION"}
      ]
    }
  ]
}
```
