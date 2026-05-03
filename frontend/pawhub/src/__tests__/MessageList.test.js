import { shallowMount } from '@vue/test-utils'
import MessageList from '@/components/MessageList.vue'
import { getConversationList } from '@/api/messages'

jest.mock('@/api/messages', () => ({
  getConversationList: jest.fn()
}))

const flush = async () => {
  await Promise.resolve()
  await Promise.resolve()
}

describe('MessageList 组件', () => {
  const push = jest.fn()
  const mockMessage = { error: jest.fn() }

  beforeEach(() => {
    getConversationList.mockResolvedValue({
      code: 0,
      data: [
        {
          otherUserId: 2,
          otherUserName: 'Alice',
          otherUserAvatar: 'a.jpg',
          lastMessage: 'hello',
          lastMessageTime: Date.now(),
          unreadCount: 1
        }
      ],
      total: 1
    })

    jest.clearAllMocks()
  })

  test('created 时加载会话列表', async () => {
    const wrapper = shallowMount(MessageList, {
      mocks: { $router: { push }, $message: mockMessage }
    })

    await flush()

    expect(getConversationList).toHaveBeenCalled()
    expect(wrapper.vm.messages.length).toBeGreaterThan(0)
    expect(wrapper.vm.messages[0].otherUserName).toBe('Alice')
  })

  test('openChat 推动路由到 messagesDetails', () => {
    const wrapper = shallowMount(MessageList, {
      mocks: { $router: { push }, $message: mockMessage }
    })

    wrapper.vm.openChat(2, 'Alice', 'a.jpg', 'private')

    expect(push).toHaveBeenCalled()
    expect(push.mock.calls[0][0].name).toBe('messagesDetails')
  })
})
