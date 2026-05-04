import { shallowMount } from '@vue/test-utils'
import ServiceMenu from '@/components/ServiceMenu.vue'

describe('ServiceMenu 组件', () => {
  test('goPage 会调用 router.push 带上 type 查询参数', () => {
    const push = jest.fn()
    const wrapper = shallowMount(ServiceMenu, {
      mocks: { $router: { push } }
    })

    const item = { name: '美容', icon: '✂️', en: 'grooming' }
    wrapper.vm.goPage(item)

    expect(push).toHaveBeenCalled()
    expect(push.mock.calls[0][0].path).toBe('/service')
    expect(push.mock.calls[0][0].query.type).toBe('grooming')
  })
})
