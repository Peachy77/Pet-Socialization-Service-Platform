import { shallowMount } from '@vue/test-utils';
import PublishView from '@/views/Main/PublishView.vue';
import { createPost } from '@/api/posts';
import { uploadFile } from '@/api/upload';

jest.mock('@/api/posts', () => ({
  createPost: jest.fn()
}));

jest.mock('@/api/upload', () => ({
  uploadFile: jest.fn()
}));

describe('Main/PublishView 页面', () => {
  const push = jest.fn();
  const back = jest.fn();
  const mockMessage = { warning: jest.fn(), success: jest.fn(), error: jest.fn() };

  beforeEach(() => {
    createPost.mockResolvedValue({ code: 1, data: 12345 });
    uploadFile.mockResolvedValue({ code: 1, data: 'http://img/a.jpg' });
    jest.clearAllMocks();
  });

  test('空内容且无图片时发布会提示 warning', async () => {
    const wrapper = shallowMount(PublishView, {
      stubs: { BottomNav: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    await wrapper.vm.handlePublish();
    expect(mockMessage.warning).toHaveBeenCalledWith('请输入内容或上传图片后再发布');
    expect(createPost).not.toHaveBeenCalled();
  });

  test('添加标签并去重', () => {
    const wrapper = shallowMount(PublishView, {
      stubs: { BottomNav: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    wrapper.setData({ tagInput: '宠物' });
    wrapper.vm.addTag();
    wrapper.setData({ tagInput: '#宠物' });
    wrapper.vm.addTag();

    expect(wrapper.vm.tags).toEqual(['宠物']);
    expect(mockMessage.warning).toHaveBeenCalledWith('标签已存在');
  });

  test('发布成功后跳转 home', async () => {
    const wrapper = shallowMount(PublishView, {
      stubs: { BottomNav: true },
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    await wrapper.setData({
      content: '今天很开心',
      imageUrls: ['http://img/a.jpg'],
      tags: ['日常']
    });

    await wrapper.vm.handlePublish();

    expect(createPost).toHaveBeenCalledWith({
      content: '今天很开心',
      images: ['http://img/a.jpg'],
      tags: ['日常']
    });
    expect(push).toHaveBeenCalledWith({ name: 'home' });
  });
});
