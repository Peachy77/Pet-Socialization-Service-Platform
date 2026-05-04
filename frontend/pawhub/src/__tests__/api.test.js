const mockPost = jest.fn();
const mockGet = jest.fn();
const mockPut = jest.fn();
const mockDelete = jest.fn();
const mockPatch = jest.fn();

jest.mock('@/api/client', () => ({
  __esModule: true,
  default: {
    post: (...args) => mockPost(...args),
    get: (...args) => mockGet(...args),
    put: (...args) => mockPut(...args),
    delete: (...args) => mockDelete(...args),
    patch: (...args) => mockPatch(...args)
  }
}));

import {
  login,
  register,
  getUsers,
  searchUsers,
  getUsersByKeyword,
  getUser,
  getCurrentUser,
  updateCurrentUser,
  followUser,
  unfollowUser,
  getMyPosts,
  getMyFavorites,
  getMyOrders as getMyUserOrders,
  getMyFollowing,
  getMyFollowers
} from '@/api/users';
import {
  getPosts,
  searchPosts,
  getPostsByKeyword,
  getPostDetail,
  createPost,
  deletePost,
  likePost,
  unlikePost,
  getComments,
  createComment,
  likeComment,
  unlikeComment,
  deleteComment,
  replyComment
} from '@/api/posts';
import {
  getMessages,
  getConversationList,
  getConversationMessages,
  createPrivateMessage,
  markConversationAsRead,
  getUnreadMessageCount,
  getPrivateMessages,
  sendPrivateMessage
} from '@/api/messages';
import {
  getOrders,
  createOrder,
  getMyOrders,
  getOrderDetail,
  cancelOrder,
  updateOrderStatus
} from '@/api/orders';
import {
  getServices,
  searchServices,
  getServicesByType,
  getServiceDetail,
  getService,
  getServiceReviews,
  createServiceReview,
  replyServiceReview,
  likeServiceReview,
  unlikeServiceReview,
  addFavorite,
  removeFavorite
} from '@/api/services';
import { uploadFile } from '@/api/upload';
import { getSearchSuggestions, getHotSearchTerms } from '@/api/ai';

describe('纯 API 层测试', () => {
  beforeEach(() => {
    mockPost.mockReset();
    mockGet.mockReset();
    mockPut.mockReset();
    mockDelete.mockReset();
    mockPatch.mockReset();
  });

  describe('users API', () => {
    test('login 会把 account 映射为 email 并调用 /users/login', async () => {
      mockPost.mockResolvedValue({ data: { code: 200, data: { token: 'token-x' } } });

      await login({ account: 'test@example.com', password: '123456' });

      expect(mockPost).toHaveBeenCalledWith(
        '/users/login',
        expect.objectContaining({ email: 'test@example.com', password: '123456' })
      );
    });

    test('register 调用 /users/register', async () => {
      mockPost.mockResolvedValue({ data: { code: 200 } });

      await register({ email: 'new@example.com', password: '123456' });

      expect(mockPost).toHaveBeenCalledWith(
        '/users/register',
        expect.objectContaining({ email: 'new@example.com', password: '123456' })
      );
    });

    test('getUsers 传递查询参数', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getUsers({ page: 1, pageSize: 10 });

      expect(mockGet).toHaveBeenCalledWith('/users', { params: { page: 1, pageSize: 10 } });
    });

    test('searchUsers 与 getUsersByKeyword 按 keyword 查询', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await searchUsers('宠物', { page: 1 });
      await getUsersByKeyword('狗狗', { page: 2 });

      expect(mockGet).toHaveBeenNthCalledWith(
        1,
        '/users',
        { params: expect.objectContaining({ keyword: '宠物', page: 1 }) }
      );
      expect(mockGet).toHaveBeenNthCalledWith(
        2,
        '/users',
        { params: expect.objectContaining({ keyword: '狗狗', page: 2 }) }
      );
    });

    test('单个用户与当前用户接口路径正确', async () => {
      mockGet.mockResolvedValue({ data: {} });

      await getUser(100);
      await getCurrentUser();

      expect(mockGet).toHaveBeenNthCalledWith(1, '/users/100');
      expect(mockGet).toHaveBeenNthCalledWith(2, '/users/me');
    });

    test('更新当前用户会走 PUT /users/me', async () => {
      mockPut.mockResolvedValue({ data: {} });

      await updateCurrentUser({ username: 'new-name' });

      expect(mockPut).toHaveBeenCalledWith('/users/me', { username: 'new-name' });
    });

    test('关注和取消关注用户', async () => {
      mockPost.mockResolvedValue({ data: {} });
      mockDelete.mockResolvedValue({ data: {} });

      await followUser(88);
      await unfollowUser(88);

      expect(mockPost).toHaveBeenCalledWith('/users/follow/88');
      expect(mockDelete).toHaveBeenCalledWith('/users/follow/88');
    });

    test('用户空间接口会根据 userId 切换路径', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getMyPosts({ page: 1 });
      await getMyFavorites({ page: 2 }, 7);
      await getMyUserOrders({ page: 3 }, 9);
      await getMyFollowing({}, 11);
      await getMyFollowers({}, 12);

      expect(mockGet).toHaveBeenNthCalledWith(1, '/users/me/posts', { params: { page: 1 } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/users/7/favorites', { params: { page: 2 } });
      expect(mockGet).toHaveBeenNthCalledWith(3, '/users/9/orders', { params: { page: 3 } });
      expect(mockGet).toHaveBeenNthCalledWith(4, '/users/11/following', { params: {} });
      expect(mockGet).toHaveBeenNthCalledWith(5, '/users/12/followers', { params: {} });
    });
  });

  describe('posts API', () => {
    test('动态列表与搜索接口路径正确', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getPosts({ page: 1 });
      await searchPosts('猫', { page: 2 });
      await getPostsByKeyword('狗', { page: 3 });

      expect(mockGet).toHaveBeenNthCalledWith(1, '/posts', { params: { page: 1 } });
      expect(mockGet).toHaveBeenNthCalledWith(
        2,
        '/posts',
        { params: expect.objectContaining({ keyword: '猫', page: 2 }) }
      );
      expect(mockGet).toHaveBeenNthCalledWith(
        3,
        '/posts',
        { params: expect.objectContaining({ keyword: '狗', page: 3 }) }
      );
    });

    test('动态详情、创建、删除和点赞接口正确', async () => {
      mockGet.mockResolvedValue({ data: {} });
      mockPost.mockResolvedValue({ data: {} });
      mockDelete.mockResolvedValue({ data: {} });

      await getPostDetail(1);
      await createPost({ content: 'hello', images: ['a'], tags: ['t'] });
      await deletePost(1);
      await likePost(1);
      await unlikePost(1);

      expect(mockGet).toHaveBeenCalledWith('/posts/1');
      expect(mockPost).toHaveBeenCalledWith('/posts', {
        content: 'hello',
        images: ['a'],
        tags: ['t']
      });
      expect(mockDelete).toHaveBeenCalledWith('/posts/1');
      expect(mockPost).toHaveBeenCalledWith('/posts/1/like');
      expect(mockDelete).toHaveBeenCalledWith('/posts/1/like');
    });

    test('评论相关接口路径正确', async () => {
      mockGet.mockResolvedValue({ data: [] });
      mockPost.mockResolvedValue({ data: {} });
      mockDelete.mockResolvedValue({ data: {} });

      await getComments(1, { page: 1 });
      await createComment(1, { content: 'nice' });
      await likeComment(1, 9);
      await unlikeComment(1, 9);
      await deleteComment(1, 9);
      await replyComment(1, 9, { content: 'reply' });

      expect(mockGet).toHaveBeenCalledWith('/posts/1/comments', { params: { page: 1 } });
      expect(mockPost).toHaveBeenCalledWith('/posts/1/comments', { content: 'nice' });
      expect(mockPost).toHaveBeenCalledWith('/posts/1/comments/9/like');
      expect(mockDelete).toHaveBeenCalledWith('/posts/1/comments/9/like');
      expect(mockDelete).toHaveBeenCalledWith('/posts/1/comments/9');
      expect(mockPost).toHaveBeenCalledWith('/posts/1/comments/9/replies', { content: 'reply' });
    });
  });

  describe('messages API', () => {
    test('消息相关接口路径正确', async () => {
      mockGet.mockResolvedValue({ data: [] });
      mockPost.mockResolvedValue({ data: {} });
      mockPatch.mockResolvedValue({ data: {} });

      await getMessages({ page: 1 });
      await getConversationList({ page: 2 }, 7);
      await getConversationMessages(88, { page: 3 }, 7);
      await createPrivateMessage({ receiver_id: 88, content: 'hi' }, 7);
      await markConversationAsRead(88, 7);
      await getUnreadMessageCount(7);

      expect(mockGet).toHaveBeenNthCalledWith(1, '/messages', { params: { page: 1 } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/messages/conversations', { params: { page: 2 } });
      expect(mockGet).toHaveBeenNthCalledWith(3, '/messages/conversations/88', { params: { page: 3 } });
      expect(mockPost).toHaveBeenCalledWith('/messages', { receiver_id: 88, content: 'hi' });
      expect(mockPatch).toHaveBeenCalledWith('/messages/conversations/88/read');
      expect(mockGet).toHaveBeenCalledWith('/messages/unread-count');
    });

    test('getPrivateMessages 和 sendPrivateMessage 是兼容别名', async () => {
      mockGet.mockResolvedValue({ data: [] });
      mockPost.mockResolvedValue({ data: {} });

      await getPrivateMessages(undefined, { page: 1 }, 5);
      await getPrivateMessages(90, { page: 2 }, 5);
      await sendPrivateMessage({ receiver_id: 90, content: 'hello' }, 5);

      expect(mockGet).toHaveBeenNthCalledWith(1, '/messages/private', { params: { page: 1 } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/messages/conversations/90', { params: { page: 2 } });
      expect(mockPost).toHaveBeenCalledWith('/messages', { receiver_id: 90, content: 'hello' });
    });
  });

  describe('orders API', () => {
    test('订单列表接口会根据 userId 切换路径', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getOrders({ status: 'pending' });
      await getOrders({ status: 'done' }, 8);
      await getMyOrders({ page: 2 });
      await getMyOrders({ page: 3 }, 9);

      expect(mockGet).toHaveBeenNthCalledWith(1, '/orders', { params: { status: 'pending' } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/users/8/orders', { params: { status: 'done' } });
      expect(mockGet).toHaveBeenNthCalledWith(3, '/orders', { params: { page: 2 } });
      expect(mockGet).toHaveBeenNthCalledWith(4, '/orders', { params: { page: 3 } });
    });

    test('创建、详情、取消和更新订单状态接口正确', async () => {
      mockPost.mockResolvedValue({ data: {} });
      mockGet.mockResolvedValue({ data: {} });
      mockDelete.mockResolvedValue({ data: {} });
      mockPatch.mockResolvedValue({ data: {} });

      await createOrder({
        serviceId: 3,
        projectName: '宠物洗护',
        appointmentDate: '2026-04-23',
        appointmentTime: '10:00',
        remark: '尽快安排',
        price: 199
      });
      await getOrderDetail(11);
      await cancelOrder(11);
      await updateOrderStatus(11, { status: 'finished' });

      expect(mockPost).toHaveBeenCalledWith('/orders', {
        service_id: 3,
        project_name: '宠物洗护',
        appointment_date: '2026-04-23',
        appointment_time: '10:00',
        remark: '尽快安排',
        price: 199
      });
      expect(mockGet).toHaveBeenCalledWith('/orders/11');
      expect(mockDelete).toHaveBeenCalledWith('/orders/11');
      expect(mockPatch).toHaveBeenCalledWith('/orders/11/status', { status: 'finished' });
    });
  });

  describe('services API', () => {
    test('商户列表、搜索和分类接口正确', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getServices({ page: 1 });
      await searchServices('洗澡', { page: 2 });
      await getServicesByType('grooming', { page: 3 });

      expect(mockGet).toHaveBeenNthCalledWith(1, '/services', { params: { page: 1 } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/services', { params: { page: 2, keyword: '洗澡' } });
      expect(mockGet).toHaveBeenNthCalledWith(3, '/services', { params: { page: 3, type: 'grooming' } });
    });

    test('商户详情和别名接口正确', async () => {
      mockGet.mockResolvedValue({ data: {} });

      await getServiceDetail(5);
      await getService(6);

      expect(mockGet).toHaveBeenNthCalledWith(1, '/services/5');
      expect(mockGet).toHaveBeenNthCalledWith(2, '/services/6');
    });

    test('商户评论、回复、点赞和收藏接口正确', async () => {
      mockGet.mockResolvedValue({ data: [] });
      mockPost.mockResolvedValue({ data: {} });
      mockDelete.mockResolvedValue({ data: {} });

      await getServiceReviews(7, { page: 1 });
      await createServiceReview(7, { content: '不错' });
      await replyServiceReview(7, 9, { content: '谢谢' });
      await likeServiceReview(7, 9);
      await unlikeServiceReview(7, 9);
      await addFavorite({ service_id: 7 });
      await removeFavorite({ service_id: 7 });

      expect(mockGet).toHaveBeenCalledWith('/services/7/reviews', { params: { page: 1 } });
      expect(mockPost).toHaveBeenCalledWith('/services/7/reviews', { content: '不错' });
      expect(mockPost).toHaveBeenCalledWith('/services/7/reviews/9/replies', { content: '谢谢' });
      expect(mockPost).toHaveBeenCalledWith('/services/7/reviews/9/like');
      expect(mockDelete).toHaveBeenCalledWith('/services/7/reviews/9/like');
      expect(mockPost).toHaveBeenCalledWith('/favorites', { service_id: 7 });
      expect(mockDelete).toHaveBeenCalledWith('/favorites', { data: { service_id: 7 } });
    });
  });

  describe('upload API', () => {
    test('uploadFile 会使用 multipart/form-data 并写入 file 字段', async () => {
      const append = jest.fn();
      const formDataMock = { append };
      const originalFormData = global.FormData;
      global.FormData = jest.fn(() => formDataMock);

      try {
        mockPost.mockResolvedValue({ data: {} });
        const file = new File(['abc'], 'avatar.png', { type: 'image/png' });

        await uploadFile(file);

        expect(global.FormData).toHaveBeenCalled();
        expect(append).toHaveBeenCalledWith('file', file);
        expect(mockPost).toHaveBeenCalledWith(
          '/upload',
          formDataMock,
          {
            headers: {
              'Content-Type': 'multipart/form-data'
            }
          }
        );
      } finally {
        global.FormData = originalFormData;
      }
    });
  });

  describe('ai API', () => {
    test('搜索建议和热门词接口正确', async () => {
      mockGet.mockResolvedValue({ data: [] });

      await getSearchSuggestions('宠物');
      await getHotSearchTerms();

      expect(mockGet).toHaveBeenNthCalledWith(1, '/ai/search/suggestions', { params: { keyword: '宠物' } });
      expect(mockGet).toHaveBeenNthCalledWith(2, '/ai/search/hot');
    });
  });
});