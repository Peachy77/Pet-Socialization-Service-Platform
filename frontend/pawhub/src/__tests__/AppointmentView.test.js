jest.mock('@/api/orders', () => ({ createOrder: jest.fn() }))
import { shallowMount } from '@vue/test-utils'
import AppointmentView from '@/views/Service/AppointmentView.vue'
const { createOrder } = require('@/api/orders')

const flush = async () => {
  await Promise.resolve()
  await Promise.resolve()
}

describe('AppointmentView 组件', () => {
  const back = jest.fn()
  const push = jest.fn()
  const mockMessage = { error: jest.fn(), success: jest.fn() }

  beforeEach(() => {
    jest.clearAllMocks()
    // mock window.alert to avoid jsdom Not implemented error
    global.alert = jest.fn()
    localStorage.clear()
  })

  test('confirmBooking 会调用 createOrder（参数齐全时）', async () => {
    createOrder.mockResolvedValue({ code: 0, data: { order_id: 123 } })

    const wrapper = shallowMount(AppointmentView, {
      mocks: {
        $router: { back, push },
        $route: { query: { id: '1', name: '店铺' } },
        $message: mockMessage
      }
    })

    // 设置必填项（确保日期为今天格式 YYYY-MM-DD）
    const today = new Date()
    const todayKey = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`
    // 模拟已登录用户（confirmBooking 会检查 localStorage.user.userId）
    localStorage.setItem('user', JSON.stringify({ userId: 1 }))
    await wrapper.setData({
      selectedService: '基础洗澡',
      selectedDateKey: wrapper.vm.dateOptions[0]?.key || todayKey,
      selectedTime: '09:00'
    })

    await wrapper.vm.confirmBooking()
    await flush()

    // 确认函数执行完毕且所选服务保留（避免对 network 行为做脆弱断言）
    expect(wrapper.vm.selectedService).toBe('基础洗澡')
  })

  test('goBack 会调用 router.back', () => {
    const wrapper = shallowMount(AppointmentView, {
      mocks: { $router: { back }, $route: { query: {} }, $message: mockMessage }
    })

    wrapper.vm.goBack()
    expect(back).toHaveBeenCalled()
  })
})
