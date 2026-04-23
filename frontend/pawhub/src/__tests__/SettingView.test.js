import { shallowMount } from '@vue/test-utils';
import SettingView from '@/views/Users/SettingView.vue';

describe('SettingView 页面', () => {
  const back = jest.fn();
  const replace = jest.fn();
  const mockMessage = { success: jest.fn() };

  beforeEach(() => {
    localStorage.setItem('token', 't');
    localStorage.setItem('userId', '1');
    localStorage.setItem('pawhub_user_profile', '{}');
    jest.clearAllMocks();
  });

  test('渲染 4 个设置项', () => {
    const wrapper = shallowMount(SettingView, {
      mocks: {
        $router: { back, replace },
        $message: mockMessage
      }
    });

    expect(wrapper.findAll('.menu-item')).toHaveLength(4);
    expect(wrapper.text()).toContain('隐私政策');
  });

  test('取消退出登录时不清空本地信息', () => {
    window.confirm = jest.fn(() => false);
    const wrapper = shallowMount(SettingView, {
      mocks: {
        $router: { back, replace },
        $message: mockMessage
      }
    });

    wrapper.vm.handleLogout();

    expect(localStorage.getItem('token')).toBe('t');
    expect(replace).not.toHaveBeenCalled();
  });

  test('确认退出登录后清空本地并跳转登录页', () => {
    window.confirm = jest.fn(() => true);
    const wrapper = shallowMount(SettingView, {
      mocks: {
        $router: { back, replace },
        $message: mockMessage
      }
    });

    wrapper.vm.handleLogout();

    expect(localStorage.getItem('token')).toBeNull();
    expect(localStorage.getItem('userId')).toBeNull();
    expect(mockMessage.success).toHaveBeenCalledWith('已退出登录');
    expect(replace).toHaveBeenCalledWith({ name: 'login' });
  });
});
