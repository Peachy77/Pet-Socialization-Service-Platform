import { shallowMount } from '@vue/test-utils';
import HomeView from '@/views/Main/HomeView.vue';
import { getPosts, likePost, unlikePost } from '@/api/posts';

jest.mock('@/api/posts', () => ({
  getPosts: jest.fn(),
  likePost: jest.fn(),
  unlikePost: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('Main/HomeView 页面', () => {
  const push = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    getPosts.mockResolvedValue({
      code: 1,
      data: {
        list: [
          {
            id: 11,
            username: 'U1',
            content: 'hello',
            likes: 3,
            comments: 1,
            images: ['a.jpg']
          }
        ]
      }
    });
    likePost.mockResolvedValue({ code: 1, data: {} });
    unlikePost.mockResolvedValue({ code: 1, data: {} });
    jest.clearAllMocks();
  });

  test('创建时加载动态列表', async () => {
    const wrapper = shallowMount(HomeView, {
      stubs: {
        SearchBar: true,
        ServiceMenu: true,
        PostCard: true,
        BottomNav: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    await flush();

    expect(getPosts).toHaveBeenCalledWith({ page: 1, pageSize: 20 });
    expect(wrapper.vm.posts).toHaveLength(1);
  });

  test('handleSearch 会跳转到搜索页并带 query', () => {
    const wrapper = shallowMount(HomeView, {
      stubs: {
        SearchBar: true,
        ServiceMenu: true,
        PostCard: true,
        BottomNav: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    wrapper.vm.handleSearch('宠物店');
    expect(push).toHaveBeenCalledWith({
      path: '/search',
      query: { q: '宠物店' }
    });
  });

  test('点赞流程会调用 likePost', async () => {
    const wrapper = shallowMount(HomeView, {
      stubs: {
        SearchBar: true,
        ServiceMenu: true,
        PostCard: true,
        BottomNav: true
      },
      mocks: {
        $router: { push },
        $message: mockMessage
      }
    });

    await flush();
    const firstPostId = wrapper.vm.posts[0].id;

    await wrapper.vm.handleToggleLike({ id: firstPostId });
    expect(likePost).toHaveBeenCalledWith(firstPostId);
    expect(wrapper.vm.posts[0].liked).toBe(true);
  });
});
