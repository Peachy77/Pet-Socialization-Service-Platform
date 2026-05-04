# CI/CD配置贡献说明

姓名：李若婷                       学号：2312190234                       角色：前端                      日期：2026-5-2

## 完成的工作

### 工作流相关

- [x] 参与编写 / 审查 `.github/workflows/ci.yml`

- [x] 配置 Codecov 覆盖率上传（frontend flag）
- [x] 添加 README 状态徽章

### 代码适配

- [x] 本地测试命令与 CI 一致，无需额外配置

- [x] 代码通过 Lint 检查（ ESLint）
- [x] 核心覆盖率达标（> 80%）

## PR 链接

- PR https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/26

## CI 运行链接

- 主运行：https://github.com/Peachy77/Pet-Socialization-Service-Platform/actions/runs/25283583739
- 负责的前端部分：https://github.com/Peachy77/Pet-Socialization-Service-Platform/actions/runs/25274101460/job/74101110937

## 遇到的问题和解决

问题：ESLint 报错 `followUser is defined but never used`

解决：删除测试文件中未使用的导入变量

## 心得体会

​      通过本次作业，我深入理解了本地环境与CI环境的差异，掌握了ESLint和Jest的CI优化配置。学会了通过--ci参数解决Jest在非交互式环境中卡死的问题，使用--max-warnings 0确保代码零警告通过检查，同时了解了GitHub Actions的工作原理。这次实践让我认识到CI/CD对保证代码质量的重要性。



