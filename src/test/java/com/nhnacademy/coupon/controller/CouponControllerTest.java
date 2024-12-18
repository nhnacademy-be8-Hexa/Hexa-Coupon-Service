package com.nhnacademy.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.dto.CouponRequestDTO;
import com.nhnacademy.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    private CouponPolicy couponPolicy;

    @BeforeEach
    void setUp() {
        couponPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Holiday Discount")
                .minPurchaseAmount(1000)
                .discountType("PERCENTAGE")
                .discountValue(10)
                .maxDiscountAmount(500)
                .isDeleted(false)
                .eventType("birthday")
                .createdAt(ZonedDateTime.now())
                .build();
    }

    // 1. 모든 쿠폰 조회 테스트
    @Test
    void testGetCouponsByActive() throws Exception {

        List<Coupon> coupons = Arrays.asList(
                Coupon.builder()
                        .couponId(1L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build(),
                Coupon.builder()
                        .couponId(2L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon2")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build()
        );

        when(couponService.getCouponsByActive(true)).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons?active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponName").value("New Year Coupon"));
    }

    // 2. 쿠폰 생성 테스트
    @Test
    void testCreateCoupons() throws Exception {
        CouponRequestDTO requestDTO = new CouponRequestDTO(1L, "Coupon1", "user", 100L, null);

        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.createCoupon(requestDTO, 1)).thenReturn(Arrays.asList(coupon));

        mockMvc.perform(post("/api/coupons?count=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].couponName").value("Coupon1"));
    }

    // 3. 쿠폰 사용 테스트
    @Test
    void testUseCoupon() throws Exception {
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.useCoupon(1L)).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/1/use"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("Coupon1"));
    }

    // 4. 쿠폰 비활성화 테스트
    @Test
    void testDeactivateCoupon() throws Exception {
        doNothing().when(couponService).deactivateCoupon(1L);

        mockMvc.perform(post("/api/coupons/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deactivated successfully"));
    }

    @Test
    void getCouponById() throws Exception {
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.getCouponById(1L)).thenReturn(coupon);

        mockMvc.perform(get("/api/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("Coupon1"));
    }

    @Test
    void getCouponsByActive_couponIdsProvided_returnsFilteredCoupons() throws Exception {
        // given
        List<Long> couponIds = Arrays.asList(1L, 2L, 3L);
        Coupon coupon1 = Coupon.builder()
                .couponId(1L)
                .couponName("Coupon 1")
                .couponTarget("USER")
                .couponTargetId(100L)
                .couponIsActive(true)
                .couponCreatedAt(java.time.ZonedDateTime.now())
                .build();
        Coupon coupon2 = Coupon.builder()
                .couponId(2L)
                .couponName("Coupon 2")
                .couponTarget("USER")
                .couponTargetId(101L)
                .couponIsActive(true)
                .couponCreatedAt(java.time.ZonedDateTime.now())
                .build();

        List<Coupon> coupons = Arrays.asList(coupon1, coupon2);

        when(couponService.getCouponsByIdsAndActive(couponIds, true)).thenReturn(coupons);

        // when & then
        mockMvc.perform(get("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]") // couponIds를 body로 전달
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponId").value(1L))
                .andExpect(jsonPath("$[1].couponId").value(2L))
                .andExpect(jsonPath("$[0].couponName").value("Coupon 1"))
                .andExpect(jsonPath("$[1].couponName").value("Coupon 2"));

        // verify
        verify(couponService, times(1)).getCouponsByIdsAndActive(couponIds, true);
    }

    @Test
    void createCoupons_countIsNegative_returnsBadRequest() throws Exception {
        // given
        CouponRequestDTO couponRequestDTO = new CouponRequestDTO(1L, "Coupon 1", "USER", 100L, ZonedDateTime.now().plusDays(1));

        // when & then
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponPolicyId\": 1, \"couponName\": \"Coupon 1\", \"couponTarget\": \"USER\", \"couponTargetId\": 100, \"couponDeadline\": \"2024-12-19T00:00:00Z\"}")
                        .param("count", "-1"))  // count = -1
                .andExpect(status().isBadRequest()) // 상태 코드 400을 확인
                .andExpect(content().json("[]"));  // 빈 배열이 반환되도록 설정

        // verify
        verify(couponService, times(0)).createCoupon(any(), anyInt());  // couponService의 메서드가 호출되지 않음
    }

}
