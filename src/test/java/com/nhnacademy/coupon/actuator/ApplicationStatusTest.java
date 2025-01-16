package com.nhnacademy.coupon.actuator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationStatusTest {
    private ApplicationStatus applicationStatus;

    @BeforeEach
    void setUp() {
        applicationStatus = new ApplicationStatus();
    }

    @Test
    void test() {
        applicationStatus.stopService();
        Assertions.assertEquals(false, applicationStatus.getStatus());

        applicationStatus.startService();
        Assertions.assertEquals(true, applicationStatus.getStatus());
    }

}