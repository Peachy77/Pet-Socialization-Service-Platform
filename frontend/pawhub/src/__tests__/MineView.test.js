import { shallowMount } from '@vue/test-utils';
import MineView from '@/views/Main/MineView.vue';
import { getCurrentUser, getMyPosts, getMyFavorites, getMyOrders, getUser } from '@/api/users';
import { cancelOrder as cancelOrderApi } from '@/api/orders';
import { likePost, unlikePost, deletePost } from '@/api/posts';

jest.mock('@/api/users', () => ({
  getCurrentUser: jest.fn(),
  getMyPosts: jest.fn(),
  getMyFavorites: jest.fn(),
  getMyOrders: jest.fn(),
  getUser: jest.fn()
}));

jest.mock('@/api/orders', () => ({
  cancelOrder: jest.fn()
}));

jest.mock('@/api/posts', () => ({
  likePost: jest.fn(),
  unlikePost: jest.fn(),
  deletePost: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('Main/MineView 页面', () => {
  const push = jest.fn();
  const mockMessage = { success: jest.fn(), error: jest.fn() };

  beforeEach(() => {
    getCurrentUser.mockResolvedValue({
      code: 0,
      data: {
        email: 'me@example.com',
        username: 'Me',
        avatar: 'me.jpg',
        bio: 'bio',
        follower_count: 5,
        following_count: 2,
        totalLikeCount: 9
      }
    });

    getMyPosts.mockResolvedValue({
      code: 0,
      data: { list: [{ id: 1, content: 'p1', likes: 1, comments: 0, images: [] }] }
    });

    getMyFavorites.mockResolvedValue({
      code: 0,
      data: { list: [{ id: 2, name: '店A', price: 88 }] }
    });

    getMyOrders.mockResolvedValue({
      code: 0,
      data: { list: [{ id: 3, status: 'pending', serviceName: '洗护', price: 66 }] }
    });

    getUser.mockResolvedValue({ code: 0, data: {} });
    cancelOrderApi.mockResolvedValue({ code: 0, data: {} });
    likePost.mockResolvedValue({ code: 0, data: {} });
    unlikePost.mockResolvedValue({ code: 0, data: {} });
    deletePost.mockResolvedValue({ code: 0, data: {} });

    jest.clearAllMocks();
    localStorage.clear();
  });

  test('创建时加载个人数据与三个列表', async () => {
    const wrapper = shallowMount(MineView, {
      stubs: {
        BottomNav: true,
        PostCard: true,
        ServiceCard: true,
        OrderList: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    await wrapper.vm.loadMineData();
    await flush();

    expect(getCurrentUser).toHaveBeenCalled();
    expect(wrapper.vm.profile.username).toBe('Me');
    expect(wrapper.vm.posts).toHaveLength(1);
    expect(wrapper.vm.favoriteServices).toHaveLength(1);
    expect(wrapper.vm.orders).toHaveLength(1);
  });

  test('设置入口跳转 setting 页面', () => {
    const wrapper = shallowMount(MineView, {
      stubs: {
        BottomNav: true,
        PostCard: true,
        ServiceCard: true,
        OrderList: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    wrapper.vm.goSetting();
    expect(push).toHaveBeenCalledWith({ name: 'setting' });
  });

  test('切换 tab 会更新 activeTab', () => {
    const wrapper = shallowMount(MineView, {
      stubs: {
        BottomNav: true,
        PostCard: true,
        ServiceCard: true,
        OrderList: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    wrapper.vm.selectTab('orders');
    expect(wrapper.vm.activeTab).toBe('orders');
  });
});
