import { shallowMount } from '@vue/test-utils';
import PostCard from '@/components/PostCard.vue';

const basePost = {
  id: 1,
  avatar: 'avatar.jpg',
  name: '小明',
  time: '1小时前',
  content: '今天带狗狗出门啦',
  images: ['1.jpg', '2.jpg', '3.jpg'],
  tags: ['遛狗', '日常'],
  likes: 12,
  comments: 3,
  liked: false,
  isMine: true
};

describe('PostCard 组件', () => {
  test('最多显示两张图片', () => {
    const wrapper = shallowMount(PostCard, {
      propsData: { post: basePost },
      mocks: { $router: { push: jest.fn() } }
    });

    expect(wrapper.vm.showImages).toEqual(['1.jpg', '2.jpg']);
    expect(wrapper.findAll('.post-images img')).toHaveLength(2);
  });

  test('点击点赞按钮触发 toggle-like 事件', async () => {
    const wrapper = shallowMount(PostCard, {
      propsData: { post: basePost },
      mocks: { $router: { push: jest.fn() } }
    });

    await wrapper.find('.like-btn').trigger('click');
    expect(wrapper.emitted('toggle-like')).toBeTruthy();
    expect(wrapper.emitted('toggle-like')[0]).toEqual([basePost]);
  });

  test('我的帖子显示删除按钮并可触发删除事件', async () => {
    const wrapper = shallowMount(PostCard, {
      propsData: { post: { ...basePost, isMine: true } },
      mocks: { $router: { push: jest.fn() } }
    });

    expect(wrapper.find('.delete-btn').exists()).toBe(true);
    await wrapper.find('.delete-btn').trigger('click');
    expect(wrapper.emitted('delete-post')).toBeTruthy();
  });

  test('点击卡片跳转帖子详情', async () => {
    const push = jest.fn();
    const wrapper = shallowMount(PostCard, {
      propsData: { post: basePost },
      mocks: { $router: { push } }
    });

    await wrapper.find('.post-card').trigger('click');
    expect(push).toHaveBeenCalled();
    expect(push.mock.calls[0][0].name).toBe('postDetail');
  });
});
