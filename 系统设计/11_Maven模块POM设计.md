# EdgeInsight Platform · 系统架构设计

## 11 Maven 多模块 POM 设计

| 文档版本 | V1.0 |
|------|------|
| 创建日期 | 2026年4月 |

---

## 1. 父 POM（edgeinsight-platform/pom.xml）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- Spring Boot 父 POM，统一管理 Spring 生态版本 -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <groupId>com.huidou.edgeinsight</groupId>
    <artifactId>edgeinsight-platform</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <name>EdgeInsight Platform</name>

    <!-- 子模块声明，顺序即构建顺序（按依赖层次排列） -->
    <modules>
        <module>edgeinsight-common</module>
        <module>edgeinsight-parser-spi</module>
        <module>edgeinsight-adapter-spi</module>
        <module>edgeinsight-core</module>
        <module>edgeinsight-adapter-mqtt</module>
        <module>edgeinsight-api</module>
        <module>edgeinsight-app</module>
    </modules>

    <properties>
        <java.version>11</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <!-- 第三方依赖版本集中管理 -->
        <moquette.version>0.17</moquette.version>
        <jjwt.version>0.12.5</jjwt.version>
        <easyexcel.version>3.3.4</easyexcel.version>
        <knife4j.version>4.4.0</knife4j.version>
        <hutool.version>5.8.25</hutool.version>
        <mysql.version>8.0.33</mysql.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <!-- ── 内部模块 ───────────────────────────────────── -->
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-common</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-parser-spi</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-adapter-spi</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-core</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-adapter-mqtt</artifactId>
                <version>${project.version}</version>
            </dependency>
            <dependency>
                <groupId>com.huidou.edgeinsight</groupId>
                <artifactId>edgeinsight-api</artifactId>
                <version>${project.version}</version>
            </dependency>

            <!-- ── Moquette MQTT Broker ────────────────────────── -->
            <dependency>
                <groupId>io.moquette</groupId>
                <artifactId>moquette-broker</artifactId>
                <version>${moquette.version}</version>
                <!-- 排除 Moquette 内置的 SLF4J 实现，使用 Spring Boot 的 Logback -->
                <exclusions>
                    <exclusion>
                        <groupId>org.slf4j</groupId>
                        <artifactId>slf4j-log4j12</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>

            <!-- ── JWT ───────────────────────────────────────────── -->
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-api</artifactId>
                <version>${jjwt.version}</version>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-impl</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>io.jsonwebtoken</groupId>
                <artifactId>jjwt-jackson</artifactId>
                <version>${jjwt.version}</version>
                <scope>runtime</scope>
            </dependency>

            <!-- ── EasyExcel ─────────────────────────────────────── -->
            <!-- 排除旧版 poi，使用 poi 5.x 避免版本冲突 -->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>easyexcel</artifactId>
                <version>${easyexcel.version}</version>
                <exclusions>
                    <exclusion>
                        <groupId>org.apache.poi</groupId>
                        <artifactId>poi</artifactId>
                    </exclusion>
                    <exclusion>
                        <groupId>org.apache.poi</groupId>
                        <artifactId>poi-ooxml</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
            <!-- 显式引入 poi 5.x -->
            <dependency>
                <groupId>org.apache.poi</groupId>
                <artifactId>poi</artifactId>
                <version>5.2.3</version>
            </dependency>
            <dependency>
                <groupId>org.apache.poi</groupId>
                <artifactId>poi-ooxml</artifactId>
                <version>5.2.3</version>
            </dependency>

            <!-- ── Knife4j API 文档 ───────────────────────────────── -->
            <dependency>
                <groupId>com.github.xiaoymin</groupId>
                <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
                <version>${knife4j.version}</version>
            </dependency>

            <!-- ── Hutool 工具库 ──────────────────────────────────── -->
            <dependency>
                <groupId>cn.hutool</groupId>
                <artifactId>hutool-core</artifactId>
                <version>${hutool.version}</version>
            </dependency>

            <!-- ── MySQL 驱动 ─────────────────────────────────────── -->
            <dependency>
                <groupId>com.mysql</groupId>
                <artifactId>mysql-connector-j</artifactId>
                <version>${mysql.version}</version>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

---

## 2. 各子模块依赖清单

### edgeinsight-common

无业务依赖，只引入基础注解和 JPA 映射支持。

```xml
<dependencies>
    <!-- JPA 注解（@MappedSuperclass 等），compile 范围即可 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- Bean Validation 注解（@NotNull 等） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

---

### edgeinsight-parser-spi

可独立发布为 jar，解析器库引用此模块。

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-common</artifactId>
    </dependency>
</dependencies>
```

---

### edgeinsight-adapter-spi

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-common</artifactId>
    </dependency>
</dependencies>
```

---

### edgeinsight-core

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-common</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-parser-spi</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-adapter-spi</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>easyexcel</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
    </dependency>
    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

---

### edgeinsight-adapter-mqtt

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-adapter-spi</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-parser-spi</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-core</artifactId>
    </dependency>

    <dependency>
        <groupId>io.moquette</groupId>
        <artifactId>moquette-broker</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

---

### edgeinsight-api

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-core</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <!-- JWT 三件套 -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
    </dependency>

    <!-- API 文档 -->
    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-openapi2-spring-boot-starter</artifactId>
    </dependency>
</dependencies>
```

---

### edgeinsight-app（启动模块，打 fat jar）

```xml
<dependencies>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-core</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-adapter-mqtt</artifactId>
    </dependency>
    <dependency>
        <groupId>com.huidou.edgeinsight</groupId>
        <artifactId>edgeinsight-api</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <!-- 仅在 app 模块打 fat jar，其余模块不配置此插件 -->
        </plugin>
    </plugins>
</build>
```

---

## 3. 注意事项

| 事项 | 说明 |
|------|------|
| `spring-boot-maven-plugin` | 只在 `edgeinsight-app` 模块配置，其余模块不配置，否则打出的 jar 无法被其他模块作为依赖引用 |
| EasyExcel + poi 冲突 | EasyExcel 3.3.x 内部使用 poi 5.x，但声明的依赖版本较低，务必在父 POM 中显式管理 poi 版本 |
| Moquette SLF4J 冲突 | Moquette 0.17 携带旧版 slf4j-log4j12，必须在父 POM 中 exclusion，否则与 Spring Boot 的 Logback 冲突导致启动失败 |
| MySQL 驱动 GroupId | MySQL 8.0.31+ 的 Maven groupId 已从 `mysql:mysql-connector-java` 改为 `com.mysql:mysql-connector-j`，注意区分 |
| `parser-spi` 独立发布 | 解析器库仓库直接引用 `edgeinsight-parser-spi` 作为依赖，需将该模块单独 `mvn install` 或发布到内部 Maven 仓库 |
