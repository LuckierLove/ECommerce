**项目快速开始**

此仓库包含一个 Kotlin/Spring Boot 后端和一个 Vue 3 + Vite 前端。下面记录本地开发的快速步骤，包含在本地绕过 Flyway 的方法（用于临时开发）。生产/CI 环境请确保运行 Flyway 迁移。

**本地（开发）**:
- **后端：绕过 Flyway（临时）**：在本地开发时，仓库提供 `dev` profile，它会禁用 Flyway，以避免因本地 MySQL / 驱动版本导致的兼容性问题。
  - PowerShell（项目根）:
    ```powershell
    Set-Location -Path 'backend'
    $env:SPRING_PROFILES_ACTIVE='dev'
    $env:SERVER_PORT='8080'
    gradle bootRun --no-daemon --stacktrace
    ```
  - 或直接通过环境变量绕过 Flyway：
    ```powershell
    $env:SPRING_FLYWAY_ENABLED='false'
    gradle bootRun
    ```

- **前端（Vite）**：前端 dev 环境已配置为指向后端（请确保后端运行在 `http://localhost:8080`）：
  ```powershell
  Set-Location -Path 'frontend'
  npm install
  npm run dev
  ```

- **默认账号用于本地测试**：用户名 `alice` / 密码 `alice123`（用于 `POST /api/auth/login`）。

**配置文件**:
- 本地 dev 环境变量文件为 `frontend/.env.development`，其中 `VITE_API_BASE` 已设置为 `http://localhost:8080/api`。
- 后端的本地 dev profile在 `backend/src/main/resources/application-dev.yml` 中禁用了 Flyway（`spring.flyway.enabled=false`）。

**生产 / CI（必须）**:
- 切勿在生产或 CI 中禁用 Flyway。请在 CI 或部署流程中执行 Flyway 迁移（`gradle flywayMigrate` 或在容器启动前运行迁移），并确保使用与目标环境兼容的 MySQL 驱动与数据库版本。

**RocketMQ 本地处理**:
- 后端包含一个 NoOp 的 `OrderEventPublisher`，当 RocketMQ 不可用时会使用该回退实现，因此不强制要求本地运行 RocketMQ。

**故障排查快速提示**:
- 若后端启动失败并显示 Flyway 相关错误（例如 `Unsupported Database: MySQL 8.4`），请使用 dev profile 或设置 `SPRING_FLYWAY_ENABLED=false` 来临时绕过；在 CI/生产中请使用受支持的 DB/驱动或更新 Flyway。

**贡献 / 提交**:
- 建议将本地-only 的绕过限制在 `dev` profile 中，并在 PR 描述中注明。CI 配置应包含 Flyway 的迁移步骤。

---
若需我现在为你提交包含本地说明的 PR，我可以继续创建分支并推送（需要你的确认）。
# ECommerce 本地启动说明

简要：这是一个示例电商后端/前端仓库。为了兼容本地环境，开发模式有一些绕过/说明，帮助你在本机快速运行前后端。若需在 CI/生产启用完整迁移（Flyway）和消息队列（RocketMQ），请参考长期修复步骤（下文）。

## 快速启动 - 后端（推荐用于本地开发）

说明：当前仓库包含 `dev` profile（`backend/src/main/resources/application-dev.yml`），该 profile 会禁用 Flyway（用于绕过 Flyway 与本地 MySQL 版本不兼容问题），并且 `bootRun` 已默认使用 `dev` profile 以便直接用 Gradle 启动。

步骤：在 PowerShell 中执行（在项目根）：

```powershell
cd C:\Users\MoonDrinkWind\Documents\Projects\ECommerce\backend
# 显示方式一：显式启用 dev profile（已在 build.gradle 中默认设置）
gradle bootRun -Dspring.profiles.active=dev --no-daemon --stacktrace

# 或者 显式禁用 Flyway（等同效果）
$env:SPRING_FLYWAY_ENABLED='false'
gradle bootRun --no-daemon --stacktrace

# 如果 8080 被占用，指定其它端口（例如 8081）
$env:SERVER_PORT='8081'
gradle bootRun --no-daemon --stacktrace
```

如果你希望服务固定监听 `8080`，请先确保端口空闲：

```powershell
# 列出 8080 的连接
netstat -ano | findstr :8080

# 查到监听的 PID 后查看进程并确认
tasklist /FI "PID eq <pid>"

# 终止进程（请先确认不会结束重要程序）
taskkill /PID <pid> /F
```

启动后验活：

```powershell
# 登录（种子用户）
curl -sS -X POST -H "Content-Type: application/json" -d '{"username":"alice","password":"alice123"}' http://localhost:8080/api/auth/login

# 用返回的 token 访问受保护接口
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/products
```

## 前端（开发）

前端基于 Vite。默认开发时你可以在 `frontend` 目录下运行：

```bash
cd frontend
npm install
npm run dev
```

如果前端需要代理到后端（比如后端运行在 `http://localhost:8080/api`），请在 `frontend/vite.config.ts` 或 `frontend/src/api/http.ts` 中配置 base URL 或 dev proxy 为 `http://localhost:8080`。

## 为什么需要上述绕过

- 本地 MySQL 的版本字符串（例如 `MySQL 8.4`）可能尚未被当前 `flyway-core` 版本识别，Flyway 会在应用启动早期抛出 `Unsupported Database`，导致 Spring 上下文初始化失败。短期解决方案是禁用 Flyway（仅用于本地开发），长期方案是升级 `flyway-core` 或使用兼容的 `mysql-connector-j`。

## 长期修复建议

- 在 CI/生产环境启用 Flyway 并确保数据库驱动与 Flyway 版本兼容。
- 选项：升级 `org.flywaydb:flyway-core` 到支持你 MySQL 版本的版本，或使用与 Flyway 已知兼容的 `mysql-connector-j`。

## 我已做的本地改动（便于开发）

- 新增 `backend/src/main/resources/application-dev.yml`（禁用 Flyway）。
- 在 `backend/build.gradle` 中将 `bootRun` 默认 profile 设为 `dev`（便于直接 `gradle bootRun`）。

如果你要我把这些开发改动 revert 回主分支或做成只在本地生效的脚本，我可以继续处理。

---

如果你需要，我可以现在：
- 把前端 `http` base 改为 `http://localhost:8080/api` 并启动前端以验证（推荐），或
- 查找并 pin 一个兼容的 Flyway/mysql 驱动版本并测试。

欢迎告诉我下一步要做什么。
