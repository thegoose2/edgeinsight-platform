# EdgeInsight Platform · 系统架构设计

## 10 application.yml 配置模板

| 文档版本 | V1.0 |
|------|------|
| 创建日期 | 2026年4月 |

---

## 说明

本文档提供 `edgeinsight-app/src/main/resources/` 下三个配置文件的完整骨架，以及每个关键配置项的说明。

- `application.yml`：公共配置（不含环境敏感信息）
- `application-dev.yml`：开发环境覆盖
- `application-prod.yml`：生产环境覆盖

---

## 1. application.yml（公共配置）

```yaml
spring:
  application:
    name: edgeinsight-platform

  profiles:
    active: dev     # 启动时指定：-Dspring.profiles.active=prod

  # ── 数据库 ────────────────────────────────────────────────────
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    # url / username / password 在 profile 文件中覆盖
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

  # ── JPA ──────────────────────────────────────────────────────
  jpa:
    database-platform: org.hibernate.dialect.MySQL8Dialect
    hibernate:
      ddl-auto: validate          # 生产环境：validate；开发环境可设 update
    show-sql: false
    open-in-view: false           # 关闭 OSIV，避免懒加载问题
    properties:
      hibernate:
        jdbc:
          time_zone: UTC          # 统一使用 UTC，与 Instant 配合
        format_sql: false

  # ── JPA Auditing ──────────────────────────────────────────────
  # @EnableJpaAuditing 在 EdgeInsightApplication 启动类上声明
  # AuditorAware 实现类：SecurityAuditorAware（从 SecurityContext 获取当前用户名）

  # ── Jackson ───────────────────────────────────────────────────
  jackson:
    date-format: yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
    time-zone: UTC
    default-property-inclusion: non_null

# ── 服务端口 ──────────────────────────────────────────────────────
server:
  port: 8080
  servlet:
    context-path: /

# ── Moquette MQTT Broker ──────────────────────────────────────────
# Moquette 通过 MoquetteBrokerConfig 以编程方式启动，不使用 application.yml 属性
# 以下为自定义配置节，由 MoquetteBrokerConfig 读取
moquette:
  port: 1883                      # MQTT 监听端口（标准端口，内网部署可直接使用）
  host: 0.0.0.0                   # 监听所有网卡
  persistence-path: ${user.home}/.moquette   # 持久化目录（Session、Retain 消息等）
  allow-anonymous: false          # 禁止匿名连接，强制走 DeviceAuthenticator

# ── 安全与 JWT ────────────────────────────────────────────────────
security:
  # 路径白名单：无需 Token 即可访问（方式一，另有 @Anonymous 注解方式二）
  public-paths:
    - POST:/api/v1/auth/login
    - GET:/actuator/health
    - GET:/actuator/info
    - GET:/doc.html              # Knife4j API 文档页面
    - GET:/v2/api-docs
    - GET:/swagger-resources
    - GET:/webjars/**

# JWT 配置存储于 system_config 表（运行时可热更新），此处为启动阶段兜底默认值
# 注意：jwt.secret 必须在 application-prod.yml 或环境变量中覆盖为强密钥
jwt:
  secret: CHANGE_ME_IN_PROD_USE_256BIT_RANDOM_STRING
  access-token-expire-hours: 8

# ── Knife4j API 文档 ─────────────────────────────────────────────
knife4j:
  enable: true
  setting:
    language: zh_CN

# ── Actuator ──────────────────────────────────────────────────────
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized

# ── 日志 ─────────────────────────────────────────────────────────
logging:
  level:
    root: INFO
    com.huidou.edgeinsight: INFO
    com.huidou.edgeinsight.adapter.mqtt: INFO    # MQTT 连接/认证日志
    org.hibernate.SQL: OFF                        # 生产环境关闭 SQL 日志
  file:
    name: logs/edgeinsight.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

---

## 2. application-dev.yml（开发环境）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/edgeinsight_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update            # 开发阶段自动建表/更新结构（首次启动后改为 validate）
    show-sql: true

logging:
  level:
    com.huidou.edgeinsight: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE   # 打印 SQL 参数值

moquette:
  persistence-path: ./target/.moquette   # 开发时使用项目目录，方便清理
```

---

## 3. application-prod.yml（生产环境）

```yaml
spring:
  datasource:
    # 生产环境建议通过环境变量注入，避免密码写入配置文件
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/edgeinsight?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
    username: ${DB_USERNAME:edgeinsight}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: validate          # 生产环境禁止自动 DDL 变更

# 生产环境必须覆盖 JWT 密钥
jwt:
  secret: ${JWT_SECRET:}          # 必须通过环境变量注入，不得为空

logging:
  level:
    root: WARN
    com.huidou.edgeinsight: INFO
```

---

## 4. MoquetteBrokerConfig 关键实现说明

Moquette 通过 Java 代码启动，不依赖 `application.yml` 中的 Moquette 原生配置文件。在 `edgeinsight-adapter-mqtt` 模块中：

```java
@Configuration
public class MoquetteBrokerConfig {

    @Value("${moquette.port:1883}")
    private int port;

    @Value("${moquette.host:0.0.0.0}")
    private String host;

    @Value("${moquette.persistence-path:${user.home}/.moquette}")
    private String persistencePath;

    @Bean(initMethod = "startServer", destroyMethod = "stopServer")
    public Server mqttBroker(DeviceAuthenticator authenticator) throws IOException {
        IConfig config = new MemoryConfig(new Properties());
        config.setProperty(BrokerConstants.PORT_PROPERTY_NAME, String.valueOf(port));
        config.setProperty(BrokerConstants.HOST_PROPERTY_NAME, host);
        config.setProperty(BrokerConstants.DATA_PATH_PROPERTY_NAME, persistencePath);
        config.setProperty(BrokerConstants.ALLOW_ANONYMOUS_PROPERTY_NAME, "false");
        // 持久化方式：H2（默认），适合单机私有化部署
        config.setProperty(BrokerConstants.PERSISTENCE_ENABLED_PROPERTY_NAME, "true");

        Server broker = new Server();
        List<? extends InterceptHandler> handlers = Collections.emptyList();
        broker.startServer(config, handlers, null, authenticator, null);
        return broker;
    }
}
```

> **注意**：Moquette 0.17 的 `IAuthenticator` 接口签名为 `checkValid(String clientId, String username, byte[] password)`，与旧版不同，实现时以 `clientId` 参数作为 `connect_id` 进行白名单校验。

---

## 5. 配置项速查表

| 配置键 | 位置 | 说明 | 是否必须在生产覆盖 |
|------|------|------|------|
| `spring.datasource.url` | profile | 数据库连接串 | ✅ 是 |
| `spring.datasource.username` | profile | 数据库用户名 | ✅ 是 |
| `spring.datasource.password` | profile | 数据库密码 | ✅ 是 |
| `jwt.secret` | prod / 环境变量 | JWT 签名密钥（≥256bit） | ✅ 是，不得用默认值 |
| `moquette.port` | application.yml | MQTT 监听端口，默认 1883 | 按需 |
| `moquette.persistence-path` | application.yml | Moquette 持久化目录 | 建议指定到数据盘 |
| `security.public-paths` | application.yml | 无需 Token 的路径白名单 | 按需增减 |
| `spring.jpa.hibernate.ddl-auto` | profile | 开发：update；生产：validate | ✅ 是 |
| `logging.level.*` | profile | 日志级别 | 按需 |

> **`jwt.secret` 与 `system_config` 的关系**：应用启动时从 `system_config` 表读取 `jwt.secret` 和 `jwt.access_token_expire_hours`，覆盖 `application.yml` 中的值。`application.yml` 中的配置仅作为首次启动前的兜底，正常运行以数据库值为准。
