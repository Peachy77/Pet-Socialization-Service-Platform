import { shallowMount } from '@vue/test-utils';
import MessagesDetailsView from '@/views/Messages/MessagesDetailsView.vue';
import { createPrivateMessage, getConversationMessages, markConversationAsRead } from '@/api/messages';
import { uploadFile } from '@/api/upload';

jest.mock('@/api/messages', () => ({
  createPrivateMessage: jest.fn(),
  getConversationMessages: jest.fn(),
  markConversationAsRead: jest.fn()
}));

jest.mock('@/api/upload', () => ({
  uploadFile: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('Messages/MessagesDetailsView 页面', () => {
  const back = jest.fn();
  const mockMessage = {
    error: jest.fn(),
    warning: jest.fn()
  };

  beforeEach(() => {
    getConversationMessages.mockResolvedValue({
      code: 0,
      data: {
        list: [
          {
            id: 1,
            senderId: 100,
            content: '你好',
            createTime: '2026-04-23 12:00:00',
            read: true
          }
        ]
      }
    });
    markConversationAsRead.mockResolvedValue({ code: 0, data: {} });
    createPrivateMessage.mockResolvedValue({ code: 0, data: {} });
    uploadFile.mockResolvedValue({ code: 0, data: { data: '/uploads/a.jpg' } });

    localStorage.setItem('userId', '200');
    localStorage.setItem('pawhub_user_profile', JSON.stringify({
      username: 'Me',
      avatar: 'me.jpg'
    }));

    jest.clearAllMocks();
  });

  afterEach(() => {
    localStorage.clear();
  });

  test('可加载会话消息并标记已读', async () => {
    const wrapper = shallowMount(MessagesDetailsView, {
      mocks: {
        $route: {
          query: {
            targetUserId: '100',
            username: 'Tom',
            avatar: 'tom.jpg',
            type: 'private'
          }
        },
        $router: { back },
        $message: mockMessage
      }
    });

    await flush();
    await wrapper.vm.loadConversationMessages();
    await wrapper.vm.markCurrentConversationAsRead();

    expect(getConversationMessages).toHaveBeenCalledWith('100', { page: 1, pageSize: 20 });
    expect(markConversationAsRead).toHaveBeenCalledWith('100');
    expect(wrapper.vm.messages.length).toBeGreaterThan(0);
  });

  test('输入文本后发送会调用 createPrivateMessage', async () => {
    const wrapper = shallowMount(MessagesDetailsView, {
      mocks: {
        $route: {
          query: {
            targetUserId: '100',
            username: 'Tom',
            avatar: 'tom.jpg'
          }
        },
        $router: { back },
        $message: mockMessage
      }
    });

    await flush();

    await wrapper.setData({ inputText: '测试消息' });
    await wrapper.vm.sendMessage();

    expect(createPrivateMessage).toHaveBeenCalledWith({
      receiver_id: 100,
      content: '测试消息',
      images: []
    });
    expect(wrapper.vm.inputText).toBe('');
  });

  test('缺少 targetUserId 时不发送消息', async () => {
    const wrapper = shallowMount(MessagesDetailsView, {
      mocks: {
        $route: {
          query: {
            username: 'Tom'
          }
        },
        $router: { back },
        $message: mockMessage
      }
    });

    await flush();

    await wrapper.setData({ inputText: '不会发送' });
    await wrapper.vm.sendMessage();

    expect(createPrivateMessage).not.toHaveBeenCalled();
  });

  test('返回按钮调用 router.back', () => {
    const wrapper = shallowMount(MessagesDetailsView, {
      mocks: {
        $route: { query: {} },
        $router: { back },
        $message: mockMessage
      }
    });

    wrapper.vm.goBack();
    expect(back).toHaveBeenCalled();
  });
});
