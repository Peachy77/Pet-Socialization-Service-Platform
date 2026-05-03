import { shallowMount } from '@vue/test-utils'
import ServiceDetailsView from '@/views/Service/ServiceDetailsView.vue'
import { getServices } from '@/api/services'

jest.mock('@/api/services', () => ({ getServices: jest.fn() }))

describe('ServiceDetailsView 组件', () => {
  beforeEach(() => {
    getServices.mockResolvedValue({
      code: 1,
      data: {
        list: [
          {
            service_id: 1,
            name: '爱宠美容',
            images: ['img.jpg'],
            address: '地址',
            rating: 4.8,
            tags: [],
            price: '¥88起'
          }
        ],
        total: 1
      }
    })

    jest.clearAllMocks()
  })

  test('fetchServices 能正确解析并填充 services', async () => {
    const wrapper = shallowMount(ServiceDetailsView, {
      mocks: { $route: { query: {} } }
    })

    await wrapper.vm.fetchServices()

    expect(getServices).toHaveBeenCalled()
    expect(wrapper.vm.services).toHaveLength(1)
    expect(wrapper.vm.total).toBeGreaterThanOrEqual(0)
  })
})
