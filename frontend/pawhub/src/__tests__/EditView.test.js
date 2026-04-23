import { shallowMount } from '@vue/test-utils';
import EditView from '@/views/Users/EditView.vue';
import { updateCurrentUser } from '@/api/users';
import { uploadFile } from '@/api/upload';

jest.mock('@/api/users', () => ({
  updateCurrentUser: jest.fn()
}));

jest.mock('@/api/upload', () => ({
  uploadFile: jest.fn()
}));

describe('EditView 页面', () => {
  let wrapper;
  const push = jest.fn();
  const back = jest.fn();
  const mockMessage = { success: jest.fn(), error: jest.fn() };

  beforeEach(() => {
    localStorage.setItem('pawhub_user_profile', JSON.stringify({
      username: '本地用户',
      email: 'local@example.com',
      avatar: 'avatar.png',
      bio: 'local bio'
    }));

    wrapper = shallowMount(EditView, {
      mocks: {
        $router: { push, back },
        $message: mockMessage
      }
    });

    jest.clearAllMocks();
  });

  afterEach(() => {
    wrapper.destroy();
    localStorage.clear();
  });

  test('创建时从本地加载用户资料', () => {
    expect(wrapper.vm.form.username).toBe('本地用户');
    expect(wrapper.vm.form.email).toBe('local@example.com');
    expect(wrapper.vm.avatarPreview).toBe('avatar.png');
  });

  test('用户名为空时阻止保存', () => {
    wrapper.vm.form.username = '';
    wrapper.vm.handleSave();

    expect(mockMessage.error).toHaveBeenCalledWith('请输入用户名');
    expect(updateCurrentUser).not.toHaveBeenCalled();
  });

  test('修改密码但两次新密码不一致时提示错误', () => {
    wrapper.vm.form.username = 'u1';
    wrapper.vm.form.bio = 'bio';
    wrapper.vm.form.oldPassword = 'old';
    wrapper.vm.form.newPassword = '123456';
    wrapper.vm.form.confirmPassword = '654321';

    wrapper.vm.handleSave();
    expect(mockMessage.error).toHaveBeenCalledWith('两次输入的新密码不一致');
  });

  test('保存成功后跳转 mine', async () => {
    updateCurrentUser.mockResolvedValue({ code: 0, message: 'ok' });

    wrapper.vm.form.username = 'newName';
    wrapper.vm.form.bio = 'newBio';
    wrapper.vm.form.avatar = 'newAvatar.png';

    wrapper.vm.handleSave();
    await Promise.resolve();

    expect(updateCurrentUser).toHaveBeenCalledWith({
      username: 'newName',
      avatar: 'newAvatar.png',
      bio: 'newBio'
    });
    expect(mockMessage.success).toHaveBeenCalled();
    expect(push).toHaveBeenCalledWith({ name: 'mine' });
  });

  test('上传头像失败时给出提示', async () => {
    uploadFile.mockRejectedValue(new Error('upload failed'));

    const fakeFile = new File(['a'], 'a.png', { type: 'image/png' });
    const event = { target: { files: [fakeFile] } };
    global.URL.createObjectURL = jest.fn(() => 'blob:preview');

    wrapper.vm.handleAvatarChange(event);
    await Promise.resolve();
    await Promise.resolve();

    expect(mockMessage.error).toHaveBeenCalledWith('头像上传失败');
  });
});
