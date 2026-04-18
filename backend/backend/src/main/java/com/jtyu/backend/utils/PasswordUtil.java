package com.jtyu.backend.utils;

import org.springframework.util.DigestUtils;

public class PasswordUtil {
    // 固定盐值（你可以改成任意字符串）
    private static final String SALT = "PetSocial2026Secure!@#";

    /**
     * 加密密码：密码 + 盐值 → MD5
     */
    public static String encode(String password) {
        String passwordWithSalt = password + SALT;
        return DigestUtils.md5DigestAsHex(passwordWithSalt.getBytes());
    }

    /**
     * 验证密码：输入密码 + 盐值 → MD5，与存储的密文比较
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
