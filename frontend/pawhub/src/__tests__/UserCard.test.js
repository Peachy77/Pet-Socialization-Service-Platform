import { shallowMount } from '@vue/test-utils';
import UserCard from '@/components/UserCard.vue';

const user = {
  id: 1,
  avatar: 'avatar.jpg',
  name: '测试用户',
  bio: '喜欢小动物'
};

describe('UserCard 组件', () => {
  test('正确展示用户名和简介', () => {
    const wrapper = shallowMount(UserCard, {
      propsData: { user }
    });

    expect(wrapper.text()).toContain('测试用户');
    expect(wrapper.text()).toContain('喜欢小动物');
  });

  test('following 为 false 时显示 关注', () => {
    const wrapper = shallowMount(UserCard, {
      propsData: { user, following: false }
    });

    expect(wrapper.find('.follow').text()).toBe('关注');
  });

  test('following 为 true 时显示 已关注', () => {
    const wrapper = shallowMount(UserCard, {
      propsData: { user, following: true }
    });

    expect(wrapper.find('.follow').text()).toBe('已关注');
  });

  test('点击关注按钮触发 follow 事件', async () => {
    const wrapper = shallowMount(UserCard, {
      propsData: { user }
    });

    await wrapper.find('.follow').trigger('click');
    expect(wrapper.emitted('follow')).toBeTruthy();
    expect(wrapper.emitted('follow')[0]).toEqual([user]);
  });

  test('点击卡片触发 click 事件', async () => {
    const wrapper = shallowMount(UserCard, {
      propsData: { user }
    });

    await wrapper.find('.user-card').trigger('click');
    expect(wrapper.emitted('click')).toBeTruthy();
    expect(wrapper.emitted('click')[0]).toEqual([user]);
  });
});
