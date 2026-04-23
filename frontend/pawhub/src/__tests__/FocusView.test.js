import { shallowMount } from '@vue/test-utils';
import FocusView from '@/views/Users/FocusView.vue';
import { followUser, getMyFollowing, unfollowUser } from '@/api/users';

jest.mock('@/api/users', () => ({
  followUser: jest.fn(),
  getMyFollowing: jest.fn(),
  unfollowUser: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('FocusView 页面', () => {
  const push = jest.fn();
  const back = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    getMyFollowing.mockResolvedValue({
      code: 0,
      data: {
        list: [{ id: 2, username: 'Jerry', avatar: 'a2' }]
      }
    });
    jest.clearAllMocks();
  });

  test('创建时加载关注列表', async () => {
    const wrapper = shallowMount(FocusView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    await flush();

    expect(wrapper.vm.users).toHaveLength(1);
    expect(wrapper.vm.users[0].following).toBe(true);
  });

  test('取消关注后从列表移除', async () => {
    unfollowUser.mockResolvedValue({ code: 0, data: {} });
    const wrapper = shallowMount(FocusView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    wrapper.setData({ users: [{ id: 2, following: true, name: 'Jerry' }] });
    await wrapper.vm.handleFollow({ id: 2 });

    expect(unfollowUser).toHaveBeenCalledWith(2);
    expect(wrapper.vm.users).toHaveLength(0);
  });

  test('返回按钮调用 router.back', () => {
    const wrapper = shallowMount(FocusView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    wrapper.vm.goBack();
    expect(back).toHaveBeenCalled();
  });
});
