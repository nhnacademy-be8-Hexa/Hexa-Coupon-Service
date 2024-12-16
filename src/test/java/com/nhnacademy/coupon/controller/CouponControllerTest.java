package com.nhnacademy.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import com.nhnacademy.coupon.service.CouponService;
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
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CouponControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private Coupon coupon;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(couponController).build();

        CouponPolicy couponPolicy = CouponPolicy.builder()
                .couponPolicyName("Default Policy")
                .minPurchaseAmount(1000)  // 기본값 설정
                .discountType("PERCENTAGE") // 기본값 설정
                .discountValue(10)         // 기본값 설정
                .maxDiscountAmount(500)    // 기본값 설정
                .eventType("SUMMER_SALE")  // 기본값 설정
                .createdAt(ZonedDateTime.now()) // 생성일 설정
                .build();

        CreatCouponDTO couponDTO = new CreatCouponDTO(
                1L,                              // couponPolicyId
                "Summer Discount",               // couponName
                "ALL",                           // couponTarget
                0L,                              // couponTargetId
                ZonedDateTime.now().plusDays(30) // couponDeadline
        );

        // Coupon을 DTO와 Policy를 이용해 생성
        coupon = Coupon.of(couponDTO, couponPolicy);
    }

    // 쿠폰 생성 테스트
    @Test
    public void testCreateCoupon() throws Exception {
        when(couponService.createCoupon(any(CreatCouponDTO.class)))
                .thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{" + "\"couponPolicyId\": 1," + "\"couponName\": \"Summer Discount\"," + "\"couponTarget\": \"ALL\"," + "\"couponTargetId\": 0," + "\"couponDeadline\": \"" + ZonedDateTime.now().plusDays(30).toString() + "\"" + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName", is("Summer Discount")))
                .andExpect(jsonPath("$.couponTarget", is("ALL")))
                .andExpect(jsonPath("$.couponTargetId", is(0)));
    }


    // 쿠폰 ID로 조회 테스트
    @Test
    public void testGetCouponById() throws Exception {
        when(couponService.getCouponById(1L)).thenReturn(coupon);

        mockMvc.perform(get("/api/coupons/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName", is("Summer Discount")))
                .andExpect(jsonPath("$.couponTarget", is("ALL")))
                .andExpect(jsonPath("$.couponTargetId", is(0)));
    }

    // 모든 쿠폰 조회 테스트
    @Test
    public void testGetAllCoupons() throws Exception {
        List<Coupon> coupons = List.of(coupon);
        when(couponService.getAllCoupons()).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponName", is("Summer Discount")))
                .andExpect(jsonPath("$[0].couponTarget", is("ALL")));
    }

    // 쿠폰 ID 리스트로 쿠폰 조회 테스트
    @Test
    public void testGetCouponsByIds() throws Exception {
        List<Long> couponIds = List.of(1L);
        List<Coupon> coupons = List.of(coupon);
        when(couponService.getCouponsByIds(couponIds)).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons/list")
                        .param("couponIds", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponName", is("Summer Discount")))
                .andExpect(jsonPath("$[0].couponTarget", is("ALL")));
    }

    // 쿠폰 사용 테스트
    @Test
    public void testUseCoupon() throws Exception {
        when(couponService.useCoupon(1L)).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/use/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName", is("Summer Discount")))
                .andExpect(jsonPath("$.couponIsActive", is(true)));
    }

    // 쿠폰 비활성화 (삭제 처리) 테스트
    @Test
    public void testDeactivateCoupon() throws Exception {
        doNothing().when(couponService).deactivateCoupon(1L);

        mockMvc.perform(post("/api/coupons/deactivate/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deactivated successfully"));

        verify(couponService, times(1)).deactivateCoupon(1L);
    }
}
