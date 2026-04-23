import { shallowMount, RouterLinkStub } from '@vue/test-utils';
import BottomNav from '@/components/BottomNav.vue';

describe('BottomNav 组件', () => {
  test('渲染四个导航项', () => {
    const wrapper = shallowMount(BottomNav, {
      stubs: {
        'router-link': RouterLinkStub
      }
    });

    const links = wrapper.findAllComponents(RouterLinkStub);
    expect(links).toHaveLength(4);
    expect(wrapper.text()).toContain('首页');
    expect(wrapper.text()).toContain('发布');
    expect(wrapper.text()).toContain('消息');
    expect(wrapper.text()).toContain('我的');
  });

  test('导航路由配置正确', () => {
    const wrapper = shallowMount(BottomNav, {
      stubs: {
        'router-link': RouterLinkStub
      }
    });

    const links = wrapper.findAllComponents(RouterLinkStub);
    expect(links.at(0).props('to')).toBe('/home');
    expect(links.at(1).props('to')).toBe('/publish');
    expect(links.at(2).props('to')).toBe('/message');
    expect(links.at(3).props('to')).toBe('/mine');
  });
});
