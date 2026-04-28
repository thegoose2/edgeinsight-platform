# EdgeInsight Platform · 系统架构设计

## 08 用户权限与 JWT 设计

| 文档版本 | V1.0 |
|------|------|
| 创建日期 | 2026年4月 |

---

## 1. 设计概述

### 1.1 方案选型

| 项目 | 选择 | 说明 |
|------|------|------|
| 认证方式 | JWT 单 Token | 私有化内网部署，用户量少，单 Token 足够 |
| Token 有效期 | 8 小时（可配置） | 覆盖运维人员一个班次，存储在 system_config |
| Token 传递方式 | 请求 Header | `Authorization: Bearer {token}`，所有 API 调用必须携带 |
| 权限模型 | RBAC | 角色 → 权限，用户 → 角色，支持自定义角色 |
| 权限粒度 | 菜单模块级 + 关键操作权限码 | 菜单权限控制页面可见性，操作权限码控制高风险操作 |
| 密码存储 | BCrypt | Spring Security 内置，单向加密 |

### 1.2 Token 设计

**Token Payload 结构：**

```json
{
  "userId":   1,
  "username": "admin",
  "realName": "张三",
  "roles":    ["ROLE_ADMIN"],
  "perms":    ["system:user", "device:manage", "control:startstop"],
  "iat":      1711900800,
  "exp":      1711929600
}
```

Token 中直接携带角色和权限列表，接口鉴权时直接从 Token 解析，不查库，保持无状态。

**公开接口（无需 Token）：**

系统支持两种方式声明公开接口，`JwtAuthFilter` 依次检查，任意一种匹配则跳过 Token 校验：

**方式一：路径白名单（`application.yml` 配置）**

适合路径固定、明确无需鉴权的接口，统一在配置文件中维护：

```yaml
security:
  public-paths:
    - POST:/api/v1/auth/login
    - GET:/actuator/health
```

**方式二：`@Anonymous` 注解**

适合分散在各模块的对外公开接口，直接标注在 Controller 方法上，可读性强，代码即文档：

```java
@GetMapping("/select")
@Anonymous   // 标注此接口无需 Token，任何人可访问
public Result<DeviceVO> selectPublic(@RequestParam Long id) { ... }
```

**所有其他接口**均需在 Header 中携带有效 Token：`Authorization: Bearer {token}`

---

## 2. 权限体系设计

### 2.1 权限分类

权限分两种类型，`perm_type` 字段区分：

| 类型 | 说明 | 控制对象 | 示例 |
|------|------|------|------|
| `MENU` | 菜单权限 | 前端页面/模块入口可见性 | `device:manage` |
| `OPERATION` | 操作权限码 | 高风险操作按钮 + 后端 API 拦截 | `control:startstop` |

### 2.2 权限码清单（初始数据）

**菜单权限：**

| 权限码 | 名称 | 模块 |
|------|------|------|
| `system:user` | 用户管理 | 系统管理 |
| `system:role` | 角色管理 | 系统管理 |
| `system:config` | 系统配置 | 系统管理 |
| `system:profile` | 协议档案管理 | 系统管理 |
| `device:type` | 设备类型管理 | 设备管理 |
| `device:manage` | 设备管理 | 设备管理 |
| `telemetry:view` | 数据监控 | 数据监控 |
| `alarm:manage` | 告警管理 | 告警管理（延后） |
| `control:operate` | 设备控制 | 设备控制（延后） |

**操作权限码：**

| 权限码 | 名称 | 风险等级 |
|------|------|------|
| `device:lifecycle` | 切换设备生命周期状态 | 低 |
| `device:import` | 批量导入设备 | 低 |
| `system:data:clean` | 遥测数据清理 | 高 |
| `control:param` | 参数设置 | 低 |
| `control:mode` | 模式切换 | 中 |
| `control:startstop` | 远程启停 | 高 |

### 2.3 内置角色与权限（初始数据）

4 个内置角色，`is_system = 1`，不允许删除，可修改权限配置。

| 角色 | 角色码 | 拥有权限 |
|------|------|------|
| 系统管理员 | `ROLE_ADMIN` | 全部权限 |
| 运维人员 | `ROLE_OPERATOR` | `device:manage` · `device:lifecycle` · `telemetry:view` · `alarm:manage` · `control:operate` · `control:param` · `control:mode` · `control:startstop` |
| 生产管理者 | `ROLE_MANAGER` | `device:manage`（只读） · `telemetry:view` · `alarm:manage`（只读） |
| 一线操作员 | `ROLE_WORKER` | `telemetry:view` |

---

## 3. 数据库表设计

### 3.1 sys_user（用户表）

```sql
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)     NOT NULL COMMENT '登录用户名，唯一',
    password        VARCHAR(100)    NOT NULL COMMENT 'BCrypt 加密密码',
    real_name       VARCHAR(50)     NOT NULL COMMENT '真实姓名',
    phone           VARCHAR(20)     COMMENT '手机号',
    email           VARCHAR(100)    COMMENT '邮箱',
    status          ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    is_system       TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否内置账号：1=内置不可删除（如 admin）',
    last_login_at   DATETIME        COMMENT '最近登录时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(50)     NOT NULL DEFAULT 'system' COMMENT '创建人用户名（JPA Auditing 自动填充）',
    updated_by      VARCHAR(50)     NOT NULL DEFAULT 'system' COMMENT '最后修改人用户名（JPA Auditing 自动填充）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) COMMENT '系统用户表';
```

### 3.2 sys_role（角色表）

```sql
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    role_code       VARCHAR(50)     NOT NULL COMMENT '角色编码，唯一，如 ROLE_ADMIN',
    role_name       VARCHAR(50)     NOT NULL COMMENT '角色名称',
    description     VARCHAR(200)    COMMENT '描述',
    is_system       TINYINT(1)      NOT NULL DEFAULT 0 COMMENT '是否内置角色：1=内置不可删除',
    status          ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by      VARCHAR(50)     NOT NULL DEFAULT 'system' COMMENT '创建人用户名（JPA Auditing 自动填充）',
    updated_by      VARCHAR(50)     NOT NULL DEFAULT 'system' COMMENT '最后修改人用户名（JPA Auditing 自动填充）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) COMMENT '角色表，支持自定义角色';
```

### 3.3 sys_permission（权限表）

```sql
CREATE TABLE sys_permission (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    perm_code       VARCHAR(100)    NOT NULL COMMENT '权限码，唯一，如 device:manage',
    perm_name       VARCHAR(50)     NOT NULL COMMENT '权限名称',
    perm_type       ENUM('MENU','OPERATION') NOT NULL COMMENT '权限类型：MENU菜单权限 / OPERATION操作权限码',
    module          VARCHAR(50)     NOT NULL COMMENT '所属模块，如 device、system、telemetry',
    description     VARCHAR(200)    COMMENT '描述',
    sort            INT             NOT NULL DEFAULT 0 COMMENT '排序',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（BaseEntity 字段）',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间（BaseEntity 字段）',
    PRIMARY KEY (id),
    UNIQUE KEY uk_perm_code (perm_code)
) COMMENT '权限表，菜单权限 + 操作权限码';
```

### 3.4 sys_user_role（用户角色关联）

```sql
CREATE TABLE sys_user_role (
    user_id         BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES sys_user(id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) COMMENT '用户角色关联，多对多';
```

### 3.5 sys_role_permission（角色权限关联）

```sql
CREATE TABLE sys_role_permission (
    role_id         BIGINT          NOT NULL,
    perm_id         BIGINT          NOT NULL,
    PRIMARY KEY (role_id, perm_id),
    FOREIGN KEY (role_id) REFERENCES sys_role(id),
    FOREIGN KEY (perm_id) REFERENCES sys_permission(id)
) COMMENT '角色权限关联，多对多';
```

---

## 4. JWT 处理流程

### 4.1 登录流程

```
POST /api/v1/auth/login
  → 校验 username + password（BCrypt 比对）
  → 查询用户角色列表
  → 查询角色对应权限码列表（去重合并）
  → 生成 JWT Token（Payload 含 userId / roles / perms）
  → 更新 sys_user.last_login_at
  → 返回 Token + 用户基础信息
```

### 4.2 请求鉴权流程

```
请求到达
  → JwtAuthFilter 拦截
  → 检查路径白名单（application.yml security.public-paths）
      命中 → 直接放行
  → 检查目标 Controller 方法是否有 @Anonymous 注解
      有 → 直接放行
  → 提取 Header: Authorization: Bearer {token}
  → Token 不存在或格式错误 → 返回 401
  → 验证 Token 签名和有效期 → 过期或非法 → 返回 401
  → 解析 Payload，提取 userId + roles + perms
  → 写入 SecurityContext（ThreadLocal）
  → 放行，进入 Controller

Controller / Service
  → 菜单权限：@RequiresPermission("device:manage")
  → 操作权限：@RequiresPermission("control:startstop")
  → 无权限 → 返回 403
```

### 4.3 登出流程

JWT 无状态，服务端无法主动使 Token 失效。登出处理：
- 后端：清除 SecurityContext，返回成功
- 前端：删除本地存储的 Token

**说明：** 内网场景安全要求相对宽松，此方案可接受。若后续需要强制登出（如踢人下线），可在 `system_config` 表记录用户最近一次密码修改时间，Token 中增加 `pwdVersion` 字段，服务端校验时比对版本号。

---

## 5. 核心类设计

```
edgeinsight-api / security/
  ├── annotation/
  │   ├── Anonymous                 标注无需 Token 的公开接口
  │   └── RequiresPermission        标注所需权限码，配合 PermissionAspect 使用
  │
  ├── config/
  │   └── SecurityProperties        读取 application.yml 中的 security.public-paths
  │
  ├── JwtTokenProvider              Token 生成、解析、校验
  │     generateToken(UserInfo)  → String
  │     parseToken(token)        → UserInfo
  │     validateToken(token)     → boolean
  │
  ├── JwtAuthFilter                 OncePerRequestFilter，拦截所有请求
  │     优先级1：匹配路径白名单（SecurityProperties）→ 放行
  │     优先级2：目标方法有 @Anonymous 注解 → 放行
  │     其他：校验 Token → 写入 SecurityContext
  │
  └── PermissionAspect              AOP 切面，处理 @RequiresPermission 注解
        校验 SecurityContext 中的 perms 是否包含所需权限码
        无权限 → 抛出 ForbiddenException → 统一异常处理器返回 403

edgeinsight-core / domain/auth/
  ├── AuthService                   登录逻辑、用户信息查询
  ├── UserService                   用户 CRUD
  └── RoleService                   角色 CRUD、权限分配
```

---

## 6. system_config 新增配置项

```sql
INSERT INTO system_config VALUES
('jwt.secret',                  'your-256-bit-secret',  'JWT 签名密钥，部署时必须修改', NOW(), 'system'),
('jwt.access_token_expire_hours', '8',                  'Token 有效期（小时）',          NOW(), 'system');
```

**注意：** `jwt.secret` 在私有化部署时必须修改为随机生成的强密钥，不得使用默认值。

---

## 8. 初始用户数据

```sql
-- 内置管理员账号（is_system=1，不可删除）
-- 密码 Admin@123456 的 BCrypt 哈希，部署后须由管理员登录后立即修改
INSERT INTO sys_user (username, password, real_name, status, is_system, created_by, updated_by)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
        '系统管理员', 'ACTIVE', 1, 'system', 'system');
```

> **说明：** `is_system = 1` 的用户不允许通过 API 删除（`DELETE /api/v1/sysUser/delete` 接口在 Service 层前置校验该字段），但可以修改密码和基础信息。

---

## 7. 后端接口权限控制映射

各模块 API 所需权限码，`JwtAuthFilter` + `PermissionAspect` 联合执行：

| API 路径 | 所需权限码 | 说明 |
|------|------|------|
| `/api/v1/sysUser/**` | `system:user` | 用户管理所有操作 |
| `/api/v1/sysRole/**` | `system:role` | 角色管理所有操作 |
| `/api/v1/systemConfig/**` | `system:config` | 系统配置 |
| `/api/v1/protocolProfile/**` | `system:profile` | 协议档案 |
| `/api/v1/deviceType/**` | `device:type` | 设备类型管理 |
| `/api/v1/device/selects` | `device:manage` | 设备列表查询 |
| `/api/v1/device/select` | `device:manage` | 设备详情 |
| `/api/v1/device/insert` | `device:manage` | 注册设备 |
| `/api/v1/device/import` | `device:import` | 批量导入（额外权限码） |
| `/api/v1/device/updateLifecycle` | `device:lifecycle` | 切换生命周期（额外权限码） |
| `/api/v1/telemetry/**` | `telemetry:view` | 数据查询 |
| `/api/v1/device/connectivity/**` | `telemetry:view` | 在离线查询 |
