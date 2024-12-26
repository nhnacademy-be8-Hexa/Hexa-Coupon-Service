package com.nhnacademy.coupon;

import com.netflix.appinfo.ApplicationInfoManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@TestConfiguration
@ActiveProfiles("test")
class HexaCouponServiceApplicationTests {

    @MockBean
    private ApplicationInfoManager applicationInfoManager;  // Eureka 관련 Bean Mock 처리

    @Test
    void contextLoads() {
    }

}
