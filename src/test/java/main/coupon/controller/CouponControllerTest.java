package main.coupon.controller;

import main.coupon.entity.Coupon;
import main.coupon.service.CouponService;
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

        coupon = new Coupon();
        coupon.setCoupon_id(1L);
        coupon.setCoupon_name("Summer Discount");
        coupon.setCoupon_target("ALL");
        coupon.setCoupon_target_id(0L);
        coupon.setCoupon_deadline(ZonedDateTime.now().plusDays(30));
        coupon.setCoupon_created_at(ZonedDateTime.now());
        coupon.setCoupon_is_active(true);
        coupon.setCoupon_used_at(null);
    }

    // 쿠폰 생성 테스트
    @Test
    public void testCreateCoupon() throws Exception {
        when(couponService.createCoupon(any(), any(), any(), any(), any()))
                .thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/create")
                        .param("couponPolicyId", "1")
                        .param("couponName", "Summer Discount")
                        .param("couponTarget", "ALL")
                        .param("couponTargetId", "0")
                        .param("couponDeadline", ZonedDateTime.now().plusDays(30).toString())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_name", is("Summer Discount")))
                .andExpect(jsonPath("$.coupon_target", is("ALL")))
                .andExpect(jsonPath("$.coupon_target_id", is(0)));
    }

    // 쿠폰 ID로 조회 테스트
    @Test
    public void testGetCouponById() throws Exception {
        when(couponService.getCouponById(1L)).thenReturn(coupon);

        mockMvc.perform(get("/api/coupons/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_name", is("Summer Discount")))
                .andExpect(jsonPath("$.coupon_target", is("ALL")))
                .andExpect(jsonPath("$.coupon_target_id", is(0)));
    }

    // 모든 쿠폰 조회 테스트
    @Test
    public void testGetAllCoupons() throws Exception {
        List<Coupon> coupons = List.of(coupon);
        when(couponService.getAllCoupons()).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].coupon_name", is("Summer Discount")))
                .andExpect(jsonPath("$[0].coupon_target", is("ALL")));
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
                .andExpect(jsonPath("$[0].coupon_name", is("Summer Discount")))
                .andExpect(jsonPath("$[0].coupon_target", is("ALL")));
    }

    // 쿠폰 사용 테스트
    @Test
    public void testUseCoupon() throws Exception {
        when(couponService.useCoupon(1L)).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/use/{couponId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coupon_name", is("Summer Discount")))
                .andExpect(jsonPath("$.coupon_is_active", is(true)));
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
