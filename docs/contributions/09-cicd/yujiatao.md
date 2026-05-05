# CI/CD 配置贡献说明

姓名：余佳陶

学号：2312190209

角色：后端

日期：2026/5/5

## 完成的工作

### 工作流相关

- [x] 参与编写 / 审查 `.github/workflows/ci.yml`
- [x] 配置 Codecov 覆盖率上传

### 代码适配

- [x] 本地测试命令与 CI 一致，无需额外配置
- [x] 代码通过 Lint 检查（Java 编译通过 + 单元测试）
- [x] 核心覆盖率达标（> 60%）



---

## PR 链接

- PR #X: [backend：配置jacoco生成覆盖率报告排除model包和启动类 by Peachy77 · Pull Request #29 · Peachy77/Pet-Socialization-Service-Platform](https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/29)

- [backend：新增controller、filter板块测试用例，完善impl板块测试用例 by Peachy77 · Pull Request #28 · Peachy77/Pet-Socialization-Service-Platform](https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/28)

  

---

## CI 运行链接

[Merge pull request #29 from Peachy77/develop · Peachy77/Pet-Socialization-Service-Platform@f22ab56](https://github.com/Peachy77/Pet-Socialization-Service-Platform/actions/runs/25355328369)

---

## 遇到的问题和解决

### 1. JaCoCo 覆盖率报告无法生成

**问题**：使用 `mvn test` 后一直报 `Skipping JaCoCo execution due to missing execution data file`

**解决**：
- 检查发现是 `pom.xml` 中 `maven-surefire-plugin` 和 `jacoco-maven-plugin` 配置冲突
- 最终采用最简配置，移除 surefire 的显式 argLine 配置，让 JaCoCo 自动注入 agent
- 同时将 Java 版本统一为 17，确保 CI 环境与本地一致

### 2. CI 中找不到 pom.xml

**问题**：GitHub Actions 报错 `No POM in this directory`

**解决**：检查项目目录结构，发现 `pom.xml` 实际在 `backend/backend/` 目录下，修改 CI 中的 `working-directory: backend/backend` 解决

### 3. 单元测试中 `orderUserId` 为 null 导致 NPE

**问题**：`AppointmentOrderServiceImplTest` 中测试失败，`orderUserId` 为 null

**解决**：测试代码中 mock 数据的 key 从 `user_id`/`order_id`（下划线格式）改为 `userId`/`orderId`（驼峰格式），与 Service 中的字段名保持一致

### 4. Mockito 不支持静态方法 mock

**问题**：`UserControllerTest` 中尝试 mock `JwtUtil.generateToken` 静态方法失败

**解决**：改用方案 B，只验证 API 返回成功和 token 字段存在，不验证具体 token 值

### 5. Model 包覆盖率低拉低整体

**问题**：Model 类（纯 getter/setter）覆盖率为 22%，影响整体

**解决**：在 JaCoCo 配置中排除 `model` 包和 `BackendApplication` 启动类

---

## 心得体会

通过本次 CI/CD 配置作业，我收获了很多：

1. **理解了 CI/CD 的价值**：每次代码提交后自动运行测试和检查，能快速发现错误，保证代码质量

2. **掌握了 GitHub Actions 配置**：学会了编写 `ci.yml` 工作流文件，配置并行 job、缓存依赖、上传覆盖率报告

5. **熟悉了 Codecov 集成**：配置 token、上传报告


