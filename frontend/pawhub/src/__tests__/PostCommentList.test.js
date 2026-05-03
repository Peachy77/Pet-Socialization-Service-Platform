import { shallowMount } from '@vue/test-utils'
import PostCommentList from '@/components/PostCommentList.vue'

describe('PostCommentList 组件', () => {
  test('submitReply 会触发 reply-comment 事件并携带 image 字段', () => {
    const wrapper = shallowMount(PostCommentList, {
      propsData: {
        comments: [],
        currentUserId: '1',
        currentUserName: 'me'
      }
    })

    wrapper.setData({ replyDraft: '回复内容', replyImage: '' })

    wrapper.vm.submitReply({ id: 10 })

    const emitted = wrapper.emitted()['reply-comment']
    expect(emitted).toBeTruthy()
    const payload = emitted[0][0]
    expect(payload).toBeDefined()
    expect(payload.image).toBe('')
    expect(payload.id).toBe(10)
  })
})
