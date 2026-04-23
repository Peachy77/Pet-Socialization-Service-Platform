import { shallowMount } from '@vue/test-utils';
import MessageView from '@/views/Main/MessageView.vue';

describe('Main/MessageView 页面', () => {
  test('渲染标题和主要组件', () => {
    const wrapper = shallowMount(MessageView, {
      stubs: {
        MessageList: true,
        BottomNav: true
      }
    });

    expect(wrapper.find('.header').text()).toContain('消息通知');
    expect(wrapper.findComponent({ name: 'MessageList' }).exists()).toBe(true);
    expect(wrapper.findComponent({ name: 'BottomNav' }).exists()).toBe(true);
  });
});
