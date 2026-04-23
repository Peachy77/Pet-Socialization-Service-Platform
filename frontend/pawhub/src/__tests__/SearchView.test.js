import { shallowMount } from '@vue/test-utils';
import SearchView from '@/views/Search/SearchView.vue';
import { getSearchSuggestions, getHotSearchTerms } from '@/api/ai';

jest.mock('@/api/ai', () => ({
  getSearchSuggestions: jest.fn(),
  getHotSearchTerms: jest.fn()
}));

describe('Search/SearchView 页面', () => {
  const push = jest.fn();

  beforeEach(() => {
    jest.useFakeTimers();
    getSearchSuggestions.mockResolvedValue({
      code: 0,
      data: {
        suggestions: ['宠物医院', '宠物洗护']
      }
    });
    getHotSearchTerms.mockResolvedValue({
      code: 0,
      data: {
        list: ['热门1', '热门2']
      }
    });
    jest.clearAllMocks();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  test('mounted 时会加载热门搜索词', async () => {
    const wrapper = shallowMount(SearchView, {
      mocks: {
        $route: { query: {} },
        $router: { push }
      }
    });

    await wrapper.vm.loadHotSearchTerms();
    await Promise.resolve();
    expect(getHotSearchTerms).toHaveBeenCalled();
    expect(wrapper.vm.hotTags).toEqual(['热门1', '热门2']);
  });

  test('输入关键字后会请求 AI 建议', async () => {
    const wrapper = shallowMount(SearchView, {
      mocks: {
        $route: { query: {} },
        $router: { push }
      }
    });

    await wrapper.setData({ keyword: '宠物' });
    wrapper.vm.onKeywordInput();
    jest.advanceTimersByTime(500);
    await Promise.resolve();
    await Promise.resolve();

    expect(getSearchSuggestions).toHaveBeenCalledWith('宠物');
    expect(Array.isArray(wrapper.vm.suggestions)).toBe(true);
  });

  test('selectTag 会触发搜索跳转', () => {
    const wrapper = shallowMount(SearchView, {
      mocks: {
        $route: { query: {} },
        $router: { push }
      }
    });

    wrapper.vm.selectTag('猫咪');
    expect(push).toHaveBeenCalledWith({
      path: '/search/results',
      query: { keyword: '猫咪' }
    });
  });

  test('goBack 会跳转 home', () => {
    const wrapper = shallowMount(SearchView, {
      mocks: {
        $route: { query: {} },
        $router: { push }
      }
    });

    wrapper.vm.goBack();
    expect(push).toHaveBeenCalledWith({ path: '/home' });
  });
});
