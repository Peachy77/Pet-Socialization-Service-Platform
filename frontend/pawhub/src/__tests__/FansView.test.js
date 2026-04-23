import { shallowMount } from '@vue/test-utils';
import FansView from '@/views/Users/FansView.vue';
import { followUser, getMyFollowers, getMyFollowing, unfollowUser } from '@/api/users';

jest.mock('@/api/users', () => ({
  followUser: jest.fn(),
  getMyFollowers: jest.fn(),
  getMyFollowing: jest.fn(),
  unfollowUser: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('FansView 页面', () => {
  const push = jest.fn();
  const back = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    getMyFollowers.mockResolvedValue({
      code: 0,
      data: {
        list: [
          { id: 1, username: 'Tom', bio: 'bio1', avatar: 'a1' }
        ]
      }
    });

    getMyFollowing.mockResolvedValue({
      code: 0,
      data: {
        list: [{ id: 1 }]
      }
    });

    jest.clearAllMocks();
  });

  test('创建时加载粉丝列表并标记是否已关注', async () => {
    const wrapper = shallowMount(FansView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    await wrapper.vm.loadFollowersUsers();
    await flush();

    expect(getMyFollowers).toHaveBeenCalled();
    expect(getMyFollowing).toHaveBeenCalled();
    expect(wrapper.vm.users).toHaveLength(1);
    expect(wrapper.vm.users[0].following).toBe(true);
  });

  test('点击用户卡片跳转用户主页', () => {
    const wrapper = shallowMount(FansView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    const user = { id: 1, name: 'Tom' };
    wrapper.vm.openUserInfo(user);

    expect(push).toHaveBeenCalled();
    expect(push.mock.calls[0][0].name).toBe('userInformation');
  });

  test('handleFollow 对已关注用户调用 unfollow', async () => {
    unfollowUser.mockResolvedValue({ code: 0, data: {} });
    const wrapper = shallowMount(FansView, {
      stubs: { UserCard: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    await wrapper.setData({
      users: [{ id: 1, name: 'Tom', following: true }]
    });

    await wrapper.vm.handleFollow({ id: 1 });
    await flush();

    expect(unfollowUser).toHaveBeenCalledWith(1);
    expect(wrapper.vm.users[0].following).toBe(false);
  });
});
