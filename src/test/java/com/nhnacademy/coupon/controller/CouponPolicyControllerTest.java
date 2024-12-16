package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.Dto.UpdateCouponPolicyDTO;
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
        // UpdateCouponPolicyDTO 객체 생성
        UpdateCouponPolicyDTO dto = new UpdateCouponPolicyDTO(
                "Winter Discount", 10000, "PERCENTAGE", 10, 5000, "Winter Sale"
        );

        // CouponPolicy.of 메서드를 사용하여 CouponPolicy 객체 생성
        CouponPolicy policy = CouponPolicy.of(dto);

        // 서비스에서 DTO로 CouponPolicy 객체가 반환되도록 설정
        when(couponPolicyService.createPolicy(any(UpdateCouponPolicyDTO.class))).thenReturn(policy);

        // POST 요청을 통해 쿠폰 정책 생성
        mockMvc.perform(post("/api/policies/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponPolicyName\": \"Winter Discount\", \"minPurchaseAmount\": 10000, \"discountType\": \"PERCENTAGE\", \"discountValue\": 10, \"max_discountAmount\": 5000, \"eventType\": \"Winter Sale\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Winter Discount"))
                .andExpect(jsonPath("$.discountValue").value(10));
    }

    @Test
    void updatePolicy_ShouldReturnUpdatedPolicy() throws Exception {
        // 기존 쿠폰 정책을 DTO로 생성
        UpdateCouponPolicyDTO updatedDTO = new UpdateCouponPolicyDTO(
                "Updated Winter Discount", 15000, "PERCENTAGE", 15, 6000, "Holiday Sale"
        );

        // 업데이트된 쿠폰 정책을 CouponPolicy.of 메서드로 생성
        CouponPolicy updatedPolicy = CouponPolicy.of(updatedDTO);

        // 서비스에서 업데이트된 쿠폰 정책을 반환하도록 설정
        when(couponPolicyService.updatePolicy(eq(1L), any(UpdateCouponPolicyDTO.class))).thenReturn(updatedPolicy);

        // PUT 요청을 통해 쿠폰 정책 업데이트
        mockMvc.perform(put("/api/policies/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponPolicyName\": \"Updated Winter Discount\", \"minPurchaseAmount\": 15000, \"discountType\": \"PERCENTAGE\", \"discountValue\": 15, \"maxDiscountAmount\": 6000, \"eventType\": \"Holiday Sale\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Updated Winter Discount"))
                .andExpect(jsonPath("$.discountValue").value(15));
    }

    @Test
    void getPolicyById_ShouldReturnPolicy() throws Exception {
        // UpdateCouponPolicyDTO 객체 생성
        UpdateCouponPolicyDTO dto = new UpdateCouponPolicyDTO(
                "Winter Discount", 10000, "PERCENTAGE", 10, 5000, "Winter Sale"
        );

        // CouponPolicy.of 메서드를 사용하여 CouponPolicy 객체 생성
        CouponPolicy policy = CouponPolicy.of(dto);

        // 서비스에서 정책을 반환하도록 설정
        when(couponPolicyService.getPolicyById(1L)).thenReturn(policy);

        // GET 요청을 통해 쿠폰 정책 조회
        mockMvc.perform(get("/api/policies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Winter Discount"));
    }

    @Test
    void testGetAllPolicies() throws Exception {
        // 여러 정책을 생성
        CouponPolicy couponPolicy1 = new CouponPolicy(
                "Policy 1", 100, "PERCENTAGE", 10, 500, "EVENT_1", ZonedDateTime.now());
        CouponPolicy couponPolicy2 = new CouponPolicy(
                "Policy 2", 200, "AMOUNT", 20, 300, "EVENT_2", ZonedDateTime.now());

        // 쿠폰 정책 목록 생성
        List<CouponPolicy> policies = Arrays.asList(couponPolicy1, couponPolicy2);

        // 서비스에서 정책 목록을 반환하도록 설정
        when(couponPolicyService.getAllPolicies()).thenReturn(policies);

        // GET 요청을 통해 모든 쿠폰 정책 조회
        mockMvc.perform(get("/api/policies/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // Content-Type 확인
                .andExpect(jsonPath("$[0].couponPolicyName").value("Policy 1")) // 첫 번째 정책 이름 확인
                .andExpect(jsonPath("$[1].couponPolicyName").value("Policy 2")); // 두 번째 정책 이름 확인
    }

    @Test
    void deletePolicy_ShouldReturnMessage() throws Exception {
        // 서비스에서 아무 것도 반환하지 않도록 설정 (삭제 성공)
        doNothing().when(couponPolicyService).deletePolicy(1L);

        // POST 요청을 통해 쿠폰 정책 삭제
        mockMvc.perform(post("/api/policies/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon policy deleted successfully"));
    }
}
