import { shallowMount } from '@vue/test-utils';
import SearchBar from '@/components/SearchBar.vue';

describe('SearchBar 组件', () => {
  test('使用默认 placeholder', () => {
    const wrapper = shallowMount(SearchBar, {
      mocks: {
        $route: { path: '/home' },
        $router: { push: jest.fn() }
      }
    });

    const input = wrapper.find('input');
    expect(input.attributes('placeholder')).toBe('搜索宠物服务、动态、用户...');
  });

  test('输入回车时触发 search 事件', async () => {
    const wrapper = shallowMount(SearchBar, {
      mocks: {
        $route: { path: '/home' },
        $router: { push: jest.fn() }
      }
    });

    const input = wrapper.find('input');
    await input.setValue('宠物医院');
    await input.trigger('keyup.enter');

    expect(wrapper.emitted('search')).toBeTruthy();
    expect(wrapper.emitted('search')[0]).toEqual(['宠物医院']);
  });

  test('非搜索页时会跳转到 /search', () => {
    const push = jest.fn();
    const wrapper = shallowMount(SearchBar, {
      mocks: {
        $route: { path: '/home' },
        $router: { push }
      }
    });

    wrapper.vm.goSearchPage();
    expect(push).toHaveBeenCalledWith('/search');
  });

  test('已在搜索页时不跳转', () => {
    const push = jest.fn();
    const wrapper = shallowMount(SearchBar, {
      mocks: {
        $route: { path: '/search' },
        $router: { push }
      }
    });

    wrapper.vm.goSearchPage();
    expect(push).not.toHaveBeenCalled();
  });
});
