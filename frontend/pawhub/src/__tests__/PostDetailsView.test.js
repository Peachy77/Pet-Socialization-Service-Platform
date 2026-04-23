import { shallowMount } from '@vue/test-utils';
import PostDetailsView from '@/views/Posts/PostDetailsView.vue';
import {
  getPostDetail,
  likePost,
  unlikePost,
  getComments,
  createComment,
  deleteComment,
  likeComment,
  unlikeComment,
  replyComment
} from '@/api/posts';
import { uploadFile } from '@/api/upload';
import { followUser, getUser, unfollowUser } from '@/api/users';

jest.mock('@/api/posts', () => ({
  getPostDetail: jest.fn(),
  likePost: jest.fn(),
  unlikePost: jest.fn(),
  getComments: jest.fn(),
  createComment: jest.fn(),
  deleteComment: jest.fn(),
  likeComment: jest.fn(),
  unlikeComment: jest.fn(),
  replyComment: jest.fn()
}));

jest.mock('@/api/upload', () => ({
  uploadFile: jest.fn()
}));

jest.mock('@/api/users', () => ({
  followUser: jest.fn(),
  getUser: jest.fn(),
  unfollowUser: jest.fn()
}));

const flush = async (times = 10) => {
  for (let index = 0; index < times; index += 1) {
    await Promise.resolve();
  }
};

const mockPostDetail = {
  code: 0,
  data: {
    post_id: 1,
    user_id: 100,
    username: '小明',
    create_time: '2026-04-23 12:00:00',
    avatar: '/uploads/avatar.jpg',
    images: '["/uploads/img1.jpg","/uploads/img2.jpg"]',
    content: '今天带狗狗出门啦',
    tags: '["遛狗","日常"]',
    like_count: 12,
    comment_count: 3,
    is_liked: false
  }
};

const mockComments = {
  code: 0,
  data: {
    list: [
      {
        comment_id: 1,
        user_id: 200,
        username: '小红',
        create_time: '2026-04-23 13:00:00',
        avatar: '/uploads/avatar2.jpg',
        content: '好可爱！',
        images: JSON.stringify([]),
        like_count: 2,
        liked: false,
        reply_count: 1,
        replies: [
          {
            id: 901,
            user_id: 201,
            username: '小白',
            content: '真的很可爱',
            likes: 0,
            liked: false
          }
        ]
      }
    ]
  }
};

describe('Posts/PostDetailsView 页面', () => {
  const push = jest.fn();
  const back = jest.fn();
  const mockMessage = {
    error: jest.fn(),
    success: jest.fn()
  };

  const mountView = async (options = {}) => {
    const wrapper = shallowMount(PostDetailsView, {
      mocks: {
        $route: {
          params: { id: '1' },
          query: {},
          ...options.route
        },
        $router: { push, back },
        $message: mockMessage
      }
    });

    await flush();
    await wrapper.vm.$nextTick();
    return wrapper;
  };

  beforeEach(() => {
    getPostDetail.mockResolvedValue(mockPostDetail);
    getComments.mockResolvedValue(mockComments);
    likePost.mockResolvedValue({ code: 0, data: {} });
    unlikePost.mockResolvedValue({ code: 0, data: {} });
    createComment.mockResolvedValue({ code: 0, data: {} });
    deleteComment.mockResolvedValue({ code: 0, data: {} });
    likeComment.mockResolvedValue({ code: 0, data: {} });
    unlikeComment.mockResolvedValue({ code: 0, data: {} });
    replyComment.mockResolvedValue({ code: 0, data: {} });
    uploadFile.mockResolvedValue({ code: 0, data: '/uploads/comment.jpg' });
    followUser.mockResolvedValue({ code: 0, data: {} });
    unfollowUser.mockResolvedValue({ code: 0, data: {} });
    getUser.mockResolvedValue({ code: 0, data: { user_id: 100, username: '小明', isFollowing: false } });
    global.URL.revokeObjectURL = jest.fn();
    global.URL.createObjectURL = jest.fn(() => 'blob:test');

    localStorage.setItem('userId', '200');
    localStorage.setItem(
      'pawhub_user_profile',
      JSON.stringify({
        id: 200,
        username: '小红',
        avatar: '/uploads/avatar2.jpg'
      })
    );

    jest.clearAllMocks();
  });

  afterEach(() => {
    localStorage.clear();
  });

  test('加载动态详情和评论列表', async () => {
    const wrapper = await mountView();

    expect(getPostDetail).toHaveBeenCalledWith('1');
    expect(getComments).toHaveBeenCalledWith('1');
    expect(wrapper.vm.post).toBeTruthy();
    expect(wrapper.vm.post.id).toBe(1);
    expect(wrapper.vm.post.content).toBe('今天带狗狗出门啦');
    expect(wrapper.vm.commentList.length).toBe(1);
    expect(wrapper.text()).toContain('动态详情');
    expect(wrapper.vm.loading).toBe(false);
  });

  test('接口返回非成功状态时展示动态不存在', async () => {
    getPostDetail.mockResolvedValueOnce({ code: 2, message: 'not found' });

    const wrapper = await mountView();

    expect(getComments).toHaveBeenCalledWith('1');
    expect(wrapper.vm.post).toBeNull();
    expect(wrapper.text()).toContain('动态不存在');
  });

  test('点赞和取消点赞动态', async () => {
    const wrapper = await mountView();

    const initialLikes = wrapper.vm.post.likes;

    await wrapper.vm.toggleLike();
    expect(likePost).toHaveBeenCalledWith(1);
    expect(wrapper.vm.liked).toBe(true);
    expect(wrapper.vm.post.likes).toBe(initialLikes + 1);

    await wrapper.vm.toggleLike();
    expect(unlikePost).toHaveBeenCalledWith(1);
    expect(wrapper.vm.liked).toBe(false);
    expect(wrapper.vm.post.likes).toBe(initialLikes);
  });

  test('关注和取消关注作者', async () => {
    const wrapper = await mountView();

    expect(wrapper.vm.followed).toBe(false);

    await wrapper.vm.toggleFollow();
    expect(followUser).toHaveBeenCalledWith(100);
    expect(wrapper.vm.followed).toBe(true);

    await wrapper.vm.toggleFollow();
    expect(unfollowUser).toHaveBeenCalledWith(100);
    expect(wrapper.vm.followed).toBe(false);
  });

  test('发表文本评论后会刷新评论列表并增加计数', async () => {
    const wrapper = await mountView();

    wrapper.vm.commentDraft = '这是一条测试评论';
    const initialCommentCount = wrapper.vm.post.comments;

    await wrapper.vm.submitComment();

    expect(createComment).toHaveBeenCalledWith(1, {
      content: '这是一条测试评论',
      images: []
    });
    expect(wrapper.vm.commentDraft).toBe('');
    expect(wrapper.vm.post.comments).toBe(initialCommentCount + 1);
    expect(getComments).toHaveBeenCalledWith(1, { page: 1, pageSize: 20 });
  });

  test('发表带图片的评论会先上传图片', async () => {
    const wrapper = await mountView();
    const mockFile = new File(['test'], 'test.jpg', { type: 'image/jpeg' });

    wrapper.vm.commentDraft = '带图片的评论';
    wrapper.vm.commentImageFile = mockFile;
    wrapper.vm.commentImage = 'blob:test';

    await wrapper.vm.submitComment();

    expect(uploadFile).toHaveBeenCalledWith(mockFile);
    expect(createComment).toHaveBeenCalledWith(1, {
      content: '带图片的评论',
      images: ['/uploads/comment.jpg']
    });
    expect(wrapper.vm.commentImage).toBe('');
    expect(wrapper.vm.commentImageFile).toBeNull();
  });

  test('评论点赞、回复和删除会更新页面状态', async () => {
    const wrapper = await mountView();

    const comment = wrapper.vm.commentList[0];
    const initialCommentLikes = comment.likes;

    await wrapper.vm.handleLikeComment(comment.id);
    expect(likeComment).toHaveBeenCalledWith(1, comment.id);
    expect(wrapper.vm.commentList[0].liked).toBe(true);
    expect(wrapper.vm.commentList[0].likes).toBe(initialCommentLikes + 1);

    const reply = wrapper.vm.commentList[0].replies[0];
    const initialReplyLikes = reply.likes;

    await wrapper.vm.handleLikeReply({ id: reply.id });
    expect(likeComment).toHaveBeenCalledWith(1, reply.id);
    expect(wrapper.vm.commentList[0].replies[0].liked).toBe(true);
    expect(wrapper.vm.commentList[0].replies[0].likes).toBe(initialReplyLikes + 1);

    await wrapper.vm.handleReplyComment({ id: comment.id, text: '补充回复内容' });
    expect(replyComment).toHaveBeenCalledWith(1, comment.id, { content: '补充回复内容' });
    expect(wrapper.vm.post.comments).toBe(4);

    await wrapper.vm.handleDeleteComment(comment);
    expect(deleteComment).toHaveBeenCalledWith(1, comment.id);
    expect(wrapper.vm.commentList.find(item => item.id === comment.id)).toBeUndefined();
    expect(wrapper.vm.post.comments).toBe(3);
  });

  test('点击作者区域会跳转到用户信息页', async () => {
    const wrapper = await mountView();
    getUser.mockClear();

    await wrapper.find('.author-left').trigger('click');
    await flush();
    await wrapper.vm.$nextTick();

    expect(getUser).toHaveBeenCalledWith(100);
    expect(push).toHaveBeenCalledWith({
      name: 'userInformation',
      query: {
        user: encodeURIComponent(JSON.stringify({ user_id: 100, username: '小明', isFollowing: false }))
      }
    });
  });
});