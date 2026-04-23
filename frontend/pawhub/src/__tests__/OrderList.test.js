import { shallowMount } from '@vue/test-utils';
import OrderList from '@/components/OrderList.vue';

const orders = [
  {
    id: 'A001',
    status: 'pending',
    serviceName: '上门洗护',
    merchantName: '汪星会所',
    time: '2026-04-23 10:00',
    price: '¥99'
  },
  {
    id: 'A002',
    status: 'completed',
    serviceName: '宠物美容',
    merchantName: '喵星美容馆',
    time: '2026-04-20 09:30',
    price: '¥168'
  }
];

describe('OrderList 组件', () => {
  test('渲染订单列表和状态文案', () => {
    const wrapper = shallowMount(OrderList, {
      propsData: { orders }
    });

    expect(wrapper.findAll('.order-card')).toHaveLength(2);
    expect(wrapper.text()).toContain('待服务');
    expect(wrapper.text()).toContain('已完成');
  });

  test('点击查看详情触发 view 事件', async () => {
    const wrapper = shallowMount(OrderList, {
      propsData: { orders }
    });

    await wrapper.find('.btn-outline').trigger('click');
    expect(wrapper.emitted('view')).toBeTruthy();
    expect(wrapper.emitted('view')[0]).toEqual([orders[0]]);
  });

  test('pending 订单支持取消', async () => {
    const wrapper = shallowMount(OrderList, {
      propsData: { orders }
    });

    await wrapper.find('.btn-danger').trigger('click');
    expect(wrapper.emitted('cancel')).toBeTruthy();
    expect(wrapper.emitted('cancel')[0]).toEqual([orders[0]]);
  });

  test('completed 订单支持再次预约', async () => {
    const wrapper = shallowMount(OrderList, {
      propsData: { orders }
    });

    await wrapper.find('.btn-primary').trigger('click');
    expect(wrapper.emitted('rebook')).toBeTruthy();
    expect(wrapper.emitted('rebook')[0]).toEqual([orders[1]]);
  });
});
