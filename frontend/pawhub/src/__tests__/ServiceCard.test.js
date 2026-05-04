import { shallowMount } from '@vue/test-utils'
import ServiceCard from '@/components/ServiceCard.vue'

describe('ServiceCard 组件', () => {
  test('openDetail 和 goAppointment 会调用 $router.push', () => {
    const push = jest.fn()
    const service = {
      id: 1,
      type: 'grooming',
      name: '店铺',
      image: 'img.jpg',
      address: '地址',
      rating: 4.5,
      tags: [],
      price: '¥88'
    }

    const wrapper = shallowMount(ServiceCard, {
      propsData: { service },
      mocks: { $router: { push } }
    })

    wrapper.vm.openDetail()
    expect(push).toHaveBeenCalled()
    expect(push.mock.calls[0][0].name).toBe('serviceDetail')

    wrapper.vm.goAppointment()
    expect(push.mock.calls[1][0].name).toBe('serviceAppointment')
  })
})
