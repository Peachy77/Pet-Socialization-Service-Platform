import { shallowMount } from '@vue/test-utils';
import OrderDetailsView from '@/views/Users/OrderDetailsView.vue';
import { getOrderDetail } from '@/api/orders';

jest.mock('@/api/orders', () => ({
  getOrderDetail: jest.fn()
}));

const flush = async () => {
  await Promise.resolve();
  await Promise.resolve();
};

describe('OrderDetailsView 页面', () => {
  const back = jest.fn();
  const mockMessage = { error: jest.fn() };

  beforeEach(() => {
    getOrderDetail.mockResolvedValue({
      code: 0,
      data: {
        id: 1001,
        status: 'completed',
        serviceName: '宠物洗护',
        price: 88
      }
    });
    localStorage.clear();
    jest.clearAllMocks();
  });

  test('创建时解析路由并加载订单详情', async () => {
    const queryOrder = encodeURIComponent(JSON.stringify({ id: 1001, status: 'pending', price: '¥66' }));
    const wrapper = shallowMount(OrderDetailsView, {
      mocks: {
        $router: { back },
        $route: { query: { order: queryOrder } },
        $message: mockMessage
      }
    });

    await flush();

    expect(getOrderDetail).toHaveBeenCalledWith(1001);
    expect(wrapper.vm.order.id).toBe(1001);
  });

  test('pending 订单可取消并写入本地状态', () => {
    window.confirm = jest.fn(() => true);
    const wrapper = shallowMount(OrderDetailsView, {
      mocks: {
        $router: { back },
        $route: { query: {} },
        $message: mockMessage
      }
    });

    wrapper.setData({ order: { id: 2001, status: 'pending' } });
    wrapper.vm.cancelOrder();

    expect(wrapper.vm.order.status).toBe('cancelled');
    const cache = JSON.parse(localStorage.getItem('pawhub_order_status') || '{}');
    expect(cache['2001'].status).toBe('cancelled');
  });

  test('goBack 调用 router.back', () => {
    const wrapper = shallowMount(OrderDetailsView, {
      mocks: {
        $router: { back },
        $route: { query: {} },
        $message: mockMessage
      }
    });

    wrapper.vm.goBack();
    expect(back).toHaveBeenCalled();
  });
});
