# Docker 部署贡献说明

姓名：余佳陶
学号：2312190209 
日期：2026-05-19  

## 我完成的工作

### 1. Dockerfile 编写
- [x] 后端 Dockerfile（多阶段构建，基于 Maven + Spring Boot）
- [x] 后端 .dockerignore 文件

### 2. Compose 配置
- [x] 开发环境 compose.yaml（含 PostgreSQL + 前后端 + 健康检查）
- [x] 生产环境 compose.prod.yaml（含资源限制、密钥管理）

### 3. 镜像优化与安全
- [x] 多阶段构建，镜像体积合理
- [x] 非 root 用户运行容器
- [x] 健康检查配置

### 4. 环境配置
- [x] 提供 .env.example 环境变量模板
- [x] 配置 Docker 镜像加速器解决网络问题

## 遇到的问题和解决

1. **问题**：Docker Desktop 无法启动，WSL 版本过旧
   **解决**：手动下载 WSL 2 安装包更新，重启后正常

2. **问题**：Maven 依赖下载超时，构建失败
   **解决**：配置阿里云 Maven 镜像源，加速依赖下载

3. **问题**：镜像拉取超时（PostgreSQL、Node 等）
   **解决**：配置 Docker 镜像加速器（DaoCloud），更换国内镜像源

4. **问题**：后端容器启动后立即退出，jar 包缺少 main 清单属性
   **解决**：修改 pom.xml，将 spring-boot-maven-plugin 的 `<skip>` 改为 false


## 心得体会

5. 通过本次作业，我掌握了 Docker 多阶段构建、Compose 配置、健康检查等核心技能，解决了网络环境下的镜像拉取和依赖下载问题，深入理解了容器化部署的完整流程。