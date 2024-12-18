package com.nhnacademy.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.Dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponPolicyController.class)
class CouponPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponPolicyService couponPolicyService;  // @MockBean을 사용해 Spring Context에 빈을 주입

    @Autowired
    private ObjectMapper objectMapper;

    // 1. 모든 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicies() throws Exception {
        List<CouponPolicy> policies = Arrays.asList(
                CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                        .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(null).build(),
                CouponPolicy.builder().couponPolicyId(2L).couponPolicyName("Policy2").minPurchaseAmount(2000)
                        .discountType("discount").discountValue(15).maxDiscountAmount(200).isDeleted(false).eventType("birthday").createdAt(null).build()
        );

        when(couponPolicyService.getAllPolicies(false)).thenReturn(policies);

        mockMvc.perform(get("/api/policies?deleted=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponPolicyName").value("Policy1"));
    }

    // 2. 특정 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicyById() throws Exception {
        CouponPolicy policy = CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(null).build();

        when(couponPolicyService.getPolicyById(1L)).thenReturn(policy);

        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"));
    }

    // 3. 특정 이벤트 타입의 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicyByEventType() throws Exception {
        CouponPolicy policy = CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(null).build();

        when(couponPolicyService.getPolicyByEventType("welcome")).thenReturn(policy);

        mockMvc.perform(get("/api/policies/welcome/eventType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"));
    }

    // 4. 쿠폰 정책 생성 테스트
    @Test
    void testCreatePolicy() throws Exception {
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO("Policy1", 1000, "discount", 10, 100, "welcome");

        CouponPolicy createdPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Policy1")
                .minPurchaseAmount(1000)
                .discountType("discount")
                .discountValue(10)
                .maxDiscountAmount(100)
                .isDeleted(false)
                .eventType("welcome")
                .createdAt(null)
                .build();

        when(couponPolicyService.createPolicy(requestDTO)).thenReturn(createdPolicy);

        mockMvc.perform(post("/api/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"));
    }

    // 5. 쿠폰 정책 수정 테스트
    @Test
    void testUpdatePolicy() throws Exception {
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO( "Updated Policy", 2000, "discount", 20, 200, "birthday");
        CouponPolicy updatedPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Updated Policy")
                .minPurchaseAmount(2000)
                .discountType("discount")
                .discountValue(20)
                .maxDiscountAmount(200)
                .isDeleted(false)
                .eventType("birthday")
                .createdAt(null)
                .build();

        when(couponPolicyService.updatePolicy(1L, requestDTO)).thenReturn(updatedPolicy);

        mockMvc.perform(patch("/api/policies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Updated Policy"));
    }

    // 6. 쿠폰 정책 삭제 테스트
    @Test
    void testDeletePolicy() throws Exception {
        doNothing().when(couponPolicyService).deletePolicy(1L);

        mockMvc.perform(delete("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon policy deleted successfully"));
    }
}
