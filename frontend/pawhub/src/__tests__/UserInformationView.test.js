import { shallowMount } from '@vue/test-utils';
import UserInformationView from '@/views/Users/UserInformationView.vue';
import { likePost, unlikePost } from '@/api/posts';
import { followUser, getUser, getUserPosts, unfollowUser } from '@/api/users';

jest.mock('@/api/posts', () => ({
  likePost: jest.fn(),
  unlikePost: jest.fn()
}));

jest.mock('@/api/users', () => ({
  followUser: jest.fn(),
  getUser: jest.fn(),
  getUserPosts: jest.fn(),
  unfollowUser: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('UserInformationView 页面', () => {
  const back = jest.fn();
  const push = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    getUser.mockResolvedValue({
      code: 0,
      data: {
        id: 9,
        username: 'Alice',
        isFollowing: true,
        follower_count: 5,
        following_count: 3,
        likes: 20
      }
    });

    getUserPosts.mockResolvedValue({
      code: 0,
      data: {
        list: [
          { id: 101, content: 'hello', likes: 1, comments: 0, images: [] }
        ]
      }
    });

    followUser.mockResolvedValue({ code: 0, data: {} });
    unfollowUser.mockResolvedValue({ code: 0, data: {} });
    likePost.mockResolvedValue({ code: 0, data: {} });
    unlikePost.mockResolvedValue({ code: 0, data: {} });

    jest.clearAllMocks();
  });

  test('创建时读取 query 用户并加载资料与动态', async () => {
    const rawUser = encodeURIComponent(JSON.stringify({ id: 9, username: 'Alice', avatar: 'a.png' }));

    const wrapper = shallowMount(UserInformationView, {
      stubs: { PostCard: true },
      mocks: {
        $route: { query: { user: rawUser } },
        $router: { back, push },
        $message: mockMessage
      }
    });

    await flush();
    await wrapper.vm.loadUserPosts();
    await flush();

    expect(getUser).toHaveBeenCalledWith(9);
    expect(getUserPosts).toHaveBeenCalled();
    expect(wrapper.vm.profile.username).toBe('Alice');
    expect(wrapper.vm.posts).toHaveLength(1);
  });

  test('goMessage 在缺少用户ID时提示错误', () => {
    const wrapper = shallowMount(UserInformationView, {
      stubs: { PostCard: true },
      mocks: {
        $route: { query: {} },
        $router: { back, push },
        $message: mockMessage
      }
    });

    wrapper.setData({ profile: { id: '', username: 'A', avatar: '' } });
    wrapper.vm.goMessage();

    expect(mockMessage.error).toHaveBeenCalledWith('无法获取私信对象');
    expect(push).not.toHaveBeenCalled();
  });

  test('toggleFollow 可调用 unfollow 接口', async () => {
    const wrapper = shallowMount(UserInformationView, {
      stubs: { PostCard: true },
      mocks: {
        $route: { query: {} },
        $router: { back, push },
        $message: mockMessage
      }
    });

    wrapper.setData({ profile: { id: 9 }, isFollowing: true, followLoading: false });
    await wrapper.vm.toggleFollow();

    expect(unfollowUser).toHaveBeenCalledWith(9);
  });
});
