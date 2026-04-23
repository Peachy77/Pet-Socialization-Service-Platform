import { shallowMount } from '@vue/test-utils';
import SearchResultsView from '@/views/Search/SearchResultsView.vue';
import { searchPosts } from '@/api/posts';
import { followUser, getMyFollowing, searchUsers, unfollowUser } from '@/api/users';
import { searchServices } from '@/api/services';

jest.mock('@/api/posts', () => ({
  searchPosts: jest.fn()
}));

jest.mock('@/api/users', () => ({
  followUser: jest.fn(),
  getMyFollowing: jest.fn(),
  searchUsers: jest.fn(),
  unfollowUser: jest.fn()
}));

jest.mock('@/api/services', () => ({
  searchServices: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('Search/SearchResultsView 页面', () => {
  const push = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    searchPosts.mockResolvedValue({
      code: 0,
      data: {
        list: [
          {
            post_id: 1,
            username: 'A',
            content: '动态A',
            create_time: '2026-04-23 10:00:00',
            like_count: 1,
            comment_count: 2,
            images: '[]',
            tags: '[]'
          }
        ]
      }
    });

    searchUsers.mockResolvedValue({
      code: 0,
      data: {
        list: [
          { id: 2, username: 'U2', bio: 'bio', avatar: 'u2.jpg', following: 0 }
        ]
      }
    });

    searchServices.mockResolvedValue({
      code: 0,
      data: {
        list: [
          { id: 3, name: '店铺A', address: '地址A', rating: 4.8, distance: '1.2', price: 99 }
        ]
      }
    });

    getMyFollowing.mockResolvedValue({
      code: 0,
      data: {
        list: [{ id: 2 }]
      }
    });

    followUser.mockResolvedValue({ code: 0, data: {} });
    unfollowUser.mockResolvedValue({ code: 0, data: {} });

    jest.clearAllMocks();
  });

  test('mounted 时会根据 keyword 执行搜索并填充三类结果', async () => {
    const wrapper = shallowMount(SearchResultsView, {
      stubs: {
        SearchBar: true,
        PostCard: true,
        ServiceCard: true,
        UserCard: true
      },
      mocks: {
        $route: { query: { keyword: '宠物' } },
        $router: { push, back: jest.fn() },
        $message: mockMessage
      }
    });

    await flush();
    await wrapper.vm.performSearch();
    await flush();

    expect(searchPosts).toHaveBeenCalled();
    expect(searchUsers).toHaveBeenCalled();
    expect(searchServices).toHaveBeenCalled();
    expect(wrapper.vm.posts).toHaveLength(1);
    expect(wrapper.vm.users).toHaveLength(1);
    expect(wrapper.vm.services).toHaveLength(1);
  });

  test('handleSearch 会跳转到结果页', () => {
    const wrapper = shallowMount(SearchResultsView, {
      stubs: {
        SearchBar: true,
        PostCard: true,
        ServiceCard: true,
        UserCard: true
      },
      mocks: {
        $route: { query: { keyword: '宠物' } },
        $router: { push, back: jest.fn() },
        $message: mockMessage
      }
    });

    wrapper.vm.handleSearch('宠物医院');
    expect(push).toHaveBeenCalledWith({
      path: '/search/results',
      query: { keyword: '宠物医院' }
    });
  });

  test('toggleFollow 会调用取消关注并更新状态', async () => {
    const wrapper = shallowMount(SearchResultsView, {
      stubs: {
        SearchBar: true,
        PostCard: true,
        ServiceCard: true,
        UserCard: true
      },
      mocks: {
        $route: { query: { keyword: '宠物' } },
        $router: { push, back: jest.fn() },
        $message: mockMessage
      }
    });

    await wrapper.setData({ users: [{ id: 2, following: true, name: 'U2' }] });
    await wrapper.vm.toggleFollow({ id: 2 });

    expect(unfollowUser).toHaveBeenCalledWith(2);
    expect(wrapper.vm.users[0].following).toBe(false);
  });

  test('goSearch 会跳转搜索页', () => {
    const wrapper = shallowMount(SearchResultsView, {
      stubs: {
        SearchBar: true,
        PostCard: true,
        ServiceCard: true,
        UserCard: true
      },
      mocks: {
        $route: { query: { keyword: '宠物' } },
        $router: { push, back: jest.fn() },
        $message: mockMessage
      }
    });

    wrapper.vm.goSearch();
    expect(push).toHaveBeenCalledWith({ path: '/search' });
  });
});
