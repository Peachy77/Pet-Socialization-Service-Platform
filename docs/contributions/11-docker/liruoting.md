# Docker 部署贡献说明

姓名：李若婷

学号：2312190234

日期：2026-5-16

## 我完成的工作

### 1. Dockerfile 编写

- [x] 前端 Dockerfile（多阶段构建）
- [x]  .dockerignore 文件

### 2. Compose 配置

- [x] 开发环境 compose.yaml（前端部分）

- [x] 生产环境 compose.prod.yaml（前端部分）

### 3. 自动化部署

- 选项 A
- 具体内容：前端部分，编写 GitHub Actions 工作流，实现前端代码 push 到 main 分支时自动构建 Docker 镜像并推送到 GHCR，同时集成 Trivy 漏洞扫描。

## PR 链接

- PR https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/37

## 遇到的问题和解决

1.问题：开发环境无法热重载

   解决：dockerfile新增development开发阶段

2.问题：生产阶段增加非root用户报权限错误

   解决：增加用户权限，并且修改 nginx 配置，让 pid 文件写到有权限的目录

## AI 使用情况

- AI 帮助解决了哪些问题：dockerfile构建时的权限报错，以及不同阶段构建镜像的方法。还有Compose 配置时和dockerfile相关联的设置。

## 心得体会

​       通过本次Docker容器化部署实践，我体会到容器技术相比于传统虚拟机部署的便利。多阶段构建让我理解了如何优化镜像体积，分离构建环境和运行环境前端镜像容量大大减少；开发与生产环境分离配置也有诸多益处，开发环境通过挂载代码实现热重载，极大提升了开发效率，而生产环境则注重稳定性和安全性。这次实践不仅掌握了Docker基本操作，而且对容器化部署有了更深的了解。

