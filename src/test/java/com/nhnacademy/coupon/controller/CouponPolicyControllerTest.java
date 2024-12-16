package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CouponPolicyControllerTest {

    @Mock
    private CouponPolicyService couponPolicyService;

    @InjectMocks
    private CouponPolicyController couponPolicyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponPolicyController).build();
    }

    @Test
    void createPolicy_ShouldReturnPolicy() throws Exception {
        CouponPolicy policy = new CouponPolicy(
                1L, "Winter Discount", 10000, "PERCENTAGE", 10,
                5000, false, "Winter Sale", ZonedDateTime.now()
        );

        when(couponPolicyService.createPolicy(any(CouponPolicy.class))).thenReturn(policy);

        mockMvc.perform(post("/api/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coupon_policy_name\": \"Winter Discount\", \"min_purchase_amount\": 10000, \"discount_type\": \"PERCENTAGE\", \"discount_value\": 10, \"max_discount_amount\": 5000, \"is_deleted\": false, \"event_type\": \"Winter Sale\", \"created_at\": \"2024-12-15T10:00:00+09:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_policy_name").value("Winter Discount"))
                .andExpect(jsonPath("$.discount_value").value(10));
    }

    @Test
    void updatePolicy_ShouldReturnUpdatedPolicy() throws Exception {
        CouponPolicy existingPolicy = new CouponPolicy(
                1L, "Winter Discount", 10000, "PERCENTAGE", 10,
                5000, false, "Winter Sale", ZonedDateTime.now()
        );

        CouponPolicy updatedPolicy = new CouponPolicy(
                1L, "Updated Winter Discount", 15000, "PERCENTAGE", 15,
                6000, false, "Holiday Sale", ZonedDateTime.now()
        );

        when(couponPolicyService.updatePolicy(eq(1L), any(CouponPolicy.class))).thenReturn(updatedPolicy);

        mockMvc.perform(put("/api/policies/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"coupon_policy_name\": \"Updated Winter Discount\", \"min_purchase_amount\": 15000, \"discount_type\": \"PERCENTAGE\", \"discount_value\": 15, \"max_discount_amount\": 6000, \"is_deleted\": false, \"event_type\": \"Holiday Sale\", \"created_at\": \"2024-12-15T10:00:00+09:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_policy_name").value("Updated Winter Discount"))
                .andExpect(jsonPath("$.discount_value").value(15));
    }

    @Test
    void getPolicyById_ShouldReturnPolicy() throws Exception {
        CouponPolicy policy = new CouponPolicy(
                1L, "Winter Discount", 10000, "PERCENTAGE", 10,
                5000, false, "Winter Sale", ZonedDateTime.now()
        );

        when(couponPolicyService.getPolicyById(1L)).thenReturn(policy);

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_policy_name").value("Winter Discount"));
    }

    @Test
    void testGetAllPolicies() throws Exception {
        CouponPolicy couponPolicy1 = new CouponPolicy(
                1L, "Policy 1", 100, "PERCENTAGE", 10, 500, false, "EVENT_1", ZonedDateTime.now());
        CouponPolicy couponPolicy2 = new CouponPolicy(
                2L, "Policy 2", 200, "AMOUNT", 20, 300, false, "EVENT_2", ZonedDateTime.now());

        List<CouponPolicy> policies = Arrays.asList(couponPolicy1, couponPolicy2);

        when(couponPolicyService.getAllPolicies()).thenReturn(policies);

        mockMvc.perform(get("/api/policies/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Content-Type 확인
                .andExpect(jsonPath("$[0].coupon_policy_id").value(1)) // 첫 번째 정책 ID 확인
                .andExpect(jsonPath("$[0].coupon_policy_name").value("Policy 1")) // 첫 번째 정책 이름 확인
                .andExpect(jsonPath("$[1].coupon_policy_id").value(2)) // 두 번째 정책 ID 확인
                .andExpect(jsonPath("$[1].coupon_policy_name").value("Policy 2")); // 두 번째 정책 이름 확인
    }

    @Test
    void deletePolicy_ShouldReturnMessage() throws Exception {
        doNothing().when(couponPolicyService).deletePolicy(1L);

        mockMvc.perform(post("/api/policies/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon policy deleted successfully"));
    }
}
