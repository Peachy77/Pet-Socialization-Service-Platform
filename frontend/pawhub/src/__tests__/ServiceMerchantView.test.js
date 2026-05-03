import { shallowMount } from '@vue/test-utils'
import ServiceMerchantView from '@/views/Service/ServiceMerchantView.vue'

describe('ServiceMerchantView 组件（部分方法）', () => {
  test('parseJson 能解析 JSON 字符串', () => {
    const wrapper = shallowMount(ServiceMerchantView, {
      mocks: { $route: { params: {}, query: {} }, $message: { error: jest.fn(), success: jest.fn() } },
      stubs: { PostCommentList: true }
    })

    expect(wrapper.vm.parseJson('["a"]')).toEqual(['a'])
    expect(wrapper.vm.parseJson('invalid')).toEqual([])
    expect(wrapper.vm.parseJson([])).toEqual([])
  })
})
