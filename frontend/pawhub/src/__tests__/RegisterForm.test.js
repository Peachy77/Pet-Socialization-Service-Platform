import { shallowMount } from '@vue/test-utils';
import RegisterView from '@/views/Users/RegisterView.vue';
import * as userApi from '@/api/users';

jest.mock('@/api/users');
jest.mock('vue-router');

describe('RegisterView 注册页面', () => {
  let wrapper;
  const mockPush = jest.fn();
  const mockMessage = {
    error: jest.fn(),
    success: jest.fn()
  };

  beforeEach(() => {
    wrapper = shallowMount(RegisterView, {
      mocks: {
        $router: { push: mockPush },
        $message: mockMessage
      },
      stubs: {
        'router-link': true
      }
    });
    jest.clearAllMocks();
  });

  afterEach(() => {
    wrapper.destroy();
  });

  // 组件渲染测试
  test('注册页面正常渲染', () => {
    expect(wrapper.exists()).toBe(true);
    expect(wrapper.find('.register-page').exists()).toBe(true);
  });

  test('显示PawHub标题', () => {
    expect(wrapper.text()).toContain('PawHub');
  });

  test('显示邮箱输入框', () => {
    const input = wrapper.find('input[placeholder="请输入邮箱作为您的登录账号"]');
    expect(input.exists()).toBe(true);
  });

  test('显示用户名输入框', () => {
    const input = wrapper.find('input[placeholder="请输入用户名"]');
    expect(input.exists()).toBe(true);
  });

  test('显示两个密码输入框', () => {
    const inputs = wrapper.findAll('input[type="password"]');
    expect(inputs.length).toBe(2);
  });

  test('显示注册按钮', () => {
    const btn = wrapper.find('.register-btn');
    expect(btn.exists()).toBe(true);
    expect(btn.text()).toContain('注册');
  });

  test('显示登录链接', () => {
    expect(wrapper.text()).toContain('去登录');
  });

  // 表单输入测试
  test('输入邮箱后form.account更新', async () => {
    const input = wrapper.find('input[placeholder="请输入邮箱作为您的登录账号"]');
    await input.setValue('newuser@example.com');
    expect(wrapper.vm.form.account).toBe('newuser@example.com');
  });

  test('输入用户名后form.username更新', async () => {
    const input = wrapper.find('input[placeholder="请输入用户名"]');
    await input.setValue('newuser');
    expect(wrapper.vm.form.username).toBe('newuser');
  });

  test('输入密码后form.password更新', async () => {
    const inputs = wrapper.findAll('input[type="password"]');
    await inputs.at(0).setValue('password123');
    expect(wrapper.vm.form.password).toBe('password123');
  });

  test('输入确认密码后form.confirmPassword更新', async () => {
    const inputs = wrapper.findAll('input[type="password"]');
    await inputs.at(1).setValue('password123');
    expect(wrapper.vm.form.confirmPassword).toBe('password123');
  });

  // 注册功能测试 - 成功场景
  test('注册成功跳转到登录页', async () => {
    userApi.register.mockResolvedValue({ code: 200 });

    wrapper.vm.form.username = 'newuser';
    wrapper.vm.form.account = 'newuser@example.com';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password123';
    await wrapper.vm.handleRegister();

    expect(userApi.register).toHaveBeenCalledWith({
      email: 'newuser@example.com',
      username: 'newuser',
      password: 'password123'
    });
    expect(mockMessage.success).toHaveBeenCalledWith('注册成功，请登录');
    expect(mockPush).toHaveBeenCalledWith('/');
  });

  // 注册功能测试 - 异常场景
  test('用户名为空时提示错误', async () => {
    wrapper.vm.form.username = '';
    wrapper.vm.form.account = 'newuser@example.com';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password123';
    await wrapper.vm.handleRegister();

    expect(mockMessage.error).toHaveBeenCalledWith('请完整填写注册信息');
    expect(userApi.register).not.toHaveBeenCalled();
  });

  test('邮箱为空时提示错误', async () => {
    wrapper.vm.form.username = 'newuser';
    wrapper.vm.form.account = '';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password123';
    await wrapper.vm.handleRegister();

    expect(mockMessage.error).toHaveBeenCalledWith('请完整填写注册信息');
    expect(userApi.register).not.toHaveBeenCalled();
  });

  test('两次密码不一致时提示错误', async () => {
    wrapper.vm.form.username = 'newuser';
    wrapper.vm.form.account = 'newuser@example.com';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password456';
    await wrapper.vm.handleRegister();

    expect(mockMessage.error).toHaveBeenCalledWith('两次密码输入不一致');
    expect(userApi.register).not.toHaveBeenCalled();
  });

  test('邮箱已存在时提示错误', async () => {
    userApi.register.mockResolvedValue({
      code: 409,
      message: '邮箱已被注册'
    });

    wrapper.vm.form.username = 'newuser';
    wrapper.vm.form.account = 'exists@example.com';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password123';
    await wrapper.vm.handleRegister();

    expect(mockMessage.error).toHaveBeenCalledWith('邮箱已被注册');
    expect(mockPush).not.toHaveBeenCalled();
  });

  test('网络请求失败时提示错误', async () => {
    userApi.register.mockRejectedValue(new Error('网络错误'));

    wrapper.vm.form.username = 'newuser';
    wrapper.vm.form.account = 'newuser@example.com';
    wrapper.vm.form.password = 'password123';
    wrapper.vm.form.confirmPassword = 'password123';
    await wrapper.vm.handleRegister();

    expect(mockMessage.error).toHaveBeenCalledWith('网络错误');
    expect(mockPush).not.toHaveBeenCalled();
  });
});
