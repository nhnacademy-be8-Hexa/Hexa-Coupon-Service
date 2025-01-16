package com.nhnacademy.coupon.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.nhnacademy.coupon.actuator.ApplicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
@WebMvcTest(ApplicationStatusController.class)
class ApplicationStatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationInfoManager applicationInfoManager;

    @MockBean
    private ApplicationStatus applicationStatus;

    @Test
    void testStopStatus() throws Exception {
        mockMvc.perform(post("/actuator/status/down"))
                .andExpect(status().isOk());

        verify(applicationInfoManager).setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
        verify(applicationStatus).stopService();
    }

    @Test
    void testStartStatus() throws Exception {
        mockMvc.perform(post("/actuator/status/up"))
                .andExpect(status().isOk());

        verify(applicationInfoManager).setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        verify(applicationStatus).startService();
    }
}