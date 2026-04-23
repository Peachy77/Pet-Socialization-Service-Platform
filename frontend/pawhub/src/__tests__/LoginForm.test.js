import { mount, shallowMount } from '@vue/test-utils';
import LoginView from '@/views/Users/LoginView.vue';
import * as userApi from '@/api/users';

jest.mock('@/api/users');
jest.mock('vue-router');

describe('LoginView 登录页面', () => {
  let wrapper;
  const mockPush = jest.fn();
  const mockMessage = {
    error: jest.fn(),
    success: jest.fn()
  };

  beforeEach(() => {
    wrapper = shallowMount(LoginView, {
      mocks: {
        $router: { push: mockPush },
        $message: mockMessage
      },
      stubs: {
        'router-link': true
      }
    });
    localStorage.clear();
    jest.clearAllMocks();
  });

  afterEach(() => {
    wrapper.destroy();
  });

  // 组件渲染测试
  test('登录页面正常渲染', () => {
    expect(wrapper.exists()).toBe(true);
    expect(wrapper.find('.login-page').exists()).toBe(true);
  });

  test('显示PawHub标题', () => {
    expect(wrapper.text()).toContain('PawHub');
  });

  test('显示账号输入框', () => {
    const input = wrapper.find('input[placeholder="请输入邮箱"]');
    expect(input.exists()).toBe(true);
  });

  test('显示密码输入框', () => {
    const input = wrapper.find('input[type="password"]');
    expect(input.exists()).toBe(true);
  });

  test('显示登录按钮', () => {
    const btn = wrapper.find('.login-btn');
    expect(btn.exists()).toBe(true);
    expect(btn.text()).toContain('登录');
  });

  test('显示注册链接', () => {
    expect(wrapper.text()).toContain('立即注册');
  });

  test('完整挂载时页面可正常渲染', () => {
    const fullWrapper = mount(LoginView, {
      mocks: {
        $router: { push: jest.fn() },
        $message: mockMessage
      },
      stubs: {
        'router-link': true
      }
    });

    expect(fullWrapper.find('.login-page').exists()).toBe(true);
    fullWrapper.destroy();
  });

  // 表单输入测试
  test('输入账号后form.account更新', async () => {
    const input = wrapper.find('input[placeholder="请输入邮箱"]');
    await input.setValue('test@example.com');
    expect(wrapper.vm.form.account).toBe('test@example.com');
  });

  test('输入密码后form.password更新', async () => {
    const input = wrapper.find('input[type="password"]');
    await input.setValue('password123');
    expect(wrapper.vm.form.password).toBe('password123');
  });

  // 登录功能测试 - 成功场景
  test('登录成功保存token和用户信息', async () => {
    userApi.login.mockResolvedValue({
      data: {
        token: 'jwt_token_xxx',
        userId: 1,
        username: 'testuser',
        email: 'test@example.com',
        avatar: '',
        bio: 'Test bio',
        followerCount: 10,
        followingCount: 5
      }
    });

    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(userApi.login).toHaveBeenCalledWith({
      account: 'test@example.com',
      password: 'password123'
    });
    expect(localStorage.getItem('token')).toBe('jwt_token_xxx');
    expect(localStorage.getItem('userId')).toBe('1');
    expect(mockMessage.success).toHaveBeenCalledWith('登录成功');
    expect(mockPush).toHaveBeenCalledWith('/home');
  });

  test('登录成功时兼容直接返回payload结构', async () => {
    userApi.login.mockResolvedValue({
      token: 'plain_token_xxx',
      username: 'plainUser',
      email: 'plain@example.com'
    });

    wrapper.vm.form.account = 'plain@example.com';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(localStorage.getItem('token')).toBe('plain_token_xxx');
    // 未返回 userId 时不应写入
    expect(localStorage.getItem('userId')).toBeNull();
    expect(mockMessage.success).toHaveBeenCalledWith('登录成功');
    expect(mockPush).toHaveBeenCalledWith('/home');
  });

  // 登录功能测试 - 异常场景
  test('账号为空时提示错误', async () => {
    wrapper.vm.form.account = '';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalledWith('请输入账号和密码');
    expect(userApi.login).not.toHaveBeenCalled();
  });

  test('密码为空时提示错误', async () => {
    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = '';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalledWith('请输入账号和密码');
    expect(userApi.login).not.toHaveBeenCalled();
  });

  test('响应中没有token时提示错误', async () => {
    userApi.login.mockResolvedValue({ data: {} });

    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalled();
    expect(mockPush).not.toHaveBeenCalled();
  });

  test('响应缺少token且无服务端报错时使用默认错误文案', async () => {
    userApi.login.mockResolvedValue({ data: {} });

    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalledWith('登录失败，请检查账号密码');
  });

  test('网络请求失败时提示错误信息', async () => {
    userApi.login.mockRejectedValue({
      response: {
        data: {
          message: '账号或密码错误'
        }
      }
    });

    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = 'wrongpassword';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalledWith('账号或密码错误');
    expect(localStorage.getItem('token')).toBeNull();
  });

  test('网络失败且缺少服务端message时走默认错误文案', async () => {
    userApi.login.mockRejectedValue(new Error('Network down'));

    wrapper.vm.form.account = 'test@example.com';
    wrapper.vm.form.password = 'password123';
    await wrapper.vm.handleLogin();

    expect(mockMessage.error).toHaveBeenCalledWith('登录失败，请检查账号密码');
  });
});
