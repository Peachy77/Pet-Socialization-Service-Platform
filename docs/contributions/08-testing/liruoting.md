# 软件测试贡献说明

姓名：李若婷                  学号：2312190234                  角色：前端                   日期：2026-4-23

## 完成的测试工作

### 测试文件

```
src\__tests__\api.test.js
src\__tests__\BottomNav.test.js
src\__tests__\EditView.test.js
src\__tests__\FansView.test.js
src\__tests__\FocusView.test.js
src\__tests__\HomeView.test.js
src\__tests__\LoginForm.test.js
src\__tests__\MessagesDetailsView.test.js
src\__tests__\MessageView.test.js
src\__tests__\MineView.test.js
src\__tests__\OrderDetailsView.test.js
src\__tests__\OrderList.test.js
src\__tests__\PostCard.test.js
src\__tests__\PostDetailsView.test.js
src\__tests__\PublishView.test.js
src\__tests__\RegisterForm.test.js
src\__tests__\SearchBar.test.js
src\__tests__\SearchResultsView.test.js
src\__tests__\SearchView.test.js
src\__tests__\SettingView.test.js
src\__tests__\UserCard.test.js
src\__tests__\UserInformationView.test.js
```

### 测试清单

- [x] 正常情况测试（**50 个**）
  - API 层：25 个（登录、注册、动态、消息、订单等接口路径验证）
  - 页面层：20 个（渲染、数据加载、表单提交成功、点赞/关注成功、跳转）
  - 组件层：5 个（渲染、按钮点击、事件触发）

- [x] 边界 / 异常情况测试（**35 个**）

  - 表单校验：11 个（账号/密码为空、密码不一致、信息不完整）
  - 网络异常：6 个（请求失败、Token 缺失、服务端错误）
  - 业务边界：8 个（空内容发表、无 ID 时不发送、取消确认、点赞/关注失败恢复
  - 参数缺失：5 个（缺少 targetUserId、缺少 postId、缺少用户 ID）

- [x] Mock 使用

  - API Mock：7 个模块（users、posts、messages、orders、services、upload、ai）

  - 路由 Mock：router.push、router.back、route

  - 全局组件 Mock：使用 stubs 隔离 BottomNav、PostCard、ServiceCard 等子组件

    

### 覆盖率

- 核心模块覆盖率：88.18%

### AI辅助

 GitHub Copilot 

## PR 链接

- PR  https://github.com/Peachy77/Pet-Socialization-Service-Platform/pull/24

## 遇到的问题和解决

问题：API Mock 不生效

解决：jest.mock() 需放在 import 之前，且在 mock 外部声明 mock 函数 ，所以调整代码顺序，统一使用 mockPost/mockGet

## 心得体会

​       通过本次软件测试作业，我深入理解了前端单元测试的核心价值。系统尝试了Jest 和 Vue Test Utils 等的用法，尝试解决了组件依赖、异步时序、Mock 失效等问题，同时学会了从用户视角设计测试用例，不再关注组件实现细节，而是验证用户能看到的渲染结果和交互行为，体会到了”测试“这个环节对于整个开发流程的重要性。