package com.jtyu.backend;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

    @Disabled("跳过上下文加载测试，避免环境变量依赖")
    @Test
    void contextLoads() {
    }

}
