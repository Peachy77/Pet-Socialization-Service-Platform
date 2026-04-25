# 软件测试贡献说明

姓名：余佳陶

学号：2312190209

角色：后端

日期：2026-4-25

## 完成的测试工作

### 测试文件清单

**单元测试（Service层）**
- `src/test/java/com/jtyu/backend/service/impl/UserServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/FollowServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/LikeServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/PostServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/FavoriteServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/AppointmentOrderServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/CommentServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/PrivateMessageServiceImplTest.java`
- `src/test/java/com/jtyu/backend/service/impl/ServiceMerchantServiceImplTest.java`

**API接口测试（Controller层）**
- `src/test/java/com/jtyu/backend/controller/AppointmentOrderControllerTest.java`
- `src/test/java/com/jtyu/backend/controller/UserControllerTest.java`

---

### 测试清单

#### 单元测试

| 测试类                          | 正常场景 | 异常/边界场景 | Mock使用                                     |
| ------------------------------- | -------- | ------------- | -------------------------------------------- |
| UserServiceImplTest             | 8        | 9             | UserMapper, FollowMapper, PostMapper, etc.   |
| FollowServiceImplTest           | 4        | 4             | FollowMapper, UserMapper                     |
| LikeServiceImplTest             | 4        | 3             | LikeMapper, PostMapper                       |
| PostServiceImplTest             | 5        | 4             | PostMapper, LikeMapper                       |
| FavoriteServiceImplTest         | 3        | 3             | FavoriteMapper                               |
| AppointmentOrderServiceImplTest | 5        | 5             | AppointmentOrderMapper                       |
| CommentServiceImplTest          | 6        | 6             | CommentMapper, CommentLikeMapper, PostMapper |
| PrivateMessageServiceImplTest   | 5        | 4             | PrivateMessageMapper                         |
| ServiceMerchantServiceImplTest  | 4        | 3             | ServiceMerchantMapper, FavoriteMapper        |

**合计：正常场景 44 个，异常/边界场景 41 个，总计 85 个单元测试**

#### API接口测试

| 测试类                         | 测试端点                  | 覆盖场景                         |
| ------------------------------ | ------------------------- | -------------------------------- |
| AppointmentOrderControllerTest | POST /orders              | 创建成功、缺少必填字段、创建失败 |
|                                | GET /orders               | 获取列表、状态筛选               |
|                                | GET /orders/{id}          | 获取成功、订单不存在             |
|                                | DELETE /orders/{id}       | 取消成功、取消失败               |
|                                | PATCH /orders/{id}/status | 更新成功、缺少状态               |
| UserControllerTest             | POST /users/login         | 登录成功、密码错误、缺少邮箱     |
|                                | POST /users/register      | 注册成功、邮箱已存在、缺少字段   |

**合计：13 个 API 测试用例**

---

### 覆盖率

根据 IntelliJ IDEA Coverage 报告：

| 维度                | 覆盖率            |
| ------------------- | ----------------- |
| **Class Coverage**  | **66%** (2/3)     |
| **Method Coverage** | **52%** (20/38)   |
| **Line Coverage**   | **62%** (182/292) |

## 遇到的问题和解决

### 问题 1：测试目录结构不完整
**现象**：IDE 提示 `Cannot resolve symbol 'UserServiceImpl'`
**原因**：测试类没有放在与主代码相同的包路径下
**解决**：在 `test/java/com/jtyu/backend/` 下创建完整的 `service/impl/` 目录结构

### 问题 2：Mockito 参数匹配器使用错误
**现象**：`InvalidUseOfMatchers` 异常
**解决**：将 `anyInt()` 等匹配器只用在 `when()` 方法参数中，返回值使用具体值

### 问题 3：JaCoCo 覆盖率报告无法生成
**现象**：`Unsupported class file major version 62`（Java 18 与 JaCoCo 不兼容）
**解决**：升级 JaCoCo 到 0.8.12，并使用 IntelliJ IDEA 内置 Coverage 工具生成报告

### 问题 4：中文路径导致 IDEA Coverage 报错
**现象**：`Failed to parse agent arguments`
**解决**：在 IDEA VM Options 中添加 `-Djava.io.tmpdir=D:/tmp`

---

## 心得体会

1. **Mock 隔离的重要性**：通过 Mock Mapper 层，可以独立测试 Service 业务逻辑，不需要真实数据库连接，测试运行速度快且稳定。

2. **边界测试的价值**：除了正常流程，空值、权限错误、重复操作等边界测试发现了代码中隐藏的逻辑问题。

4. **覆盖率工具的使用**：通过 IDEA Coverage 工具直观看到哪些代码行未被覆盖，可以有针对性地补充测试用例。

