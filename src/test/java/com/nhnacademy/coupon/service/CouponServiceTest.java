package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.Dto.CouponRequestDTO;
import com.nhnacademy.coupon.exception.CouponNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponRequestException;
import com.nhnacademy.coupon.repository.CouponRepository;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @InjectMocks
    private CouponService couponService;

    private CouponPolicy couponPolicy;
    private Coupon coupon;
    private CouponRequestDTO couponRequestDTO;

    @BeforeEach
    public void setUp() {
        // CouponPolicy 객체는 빌더 패턴으로 생성
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

        // Coupon 객체도 빌더 패턴으로 생성
        coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("New Year Coupon")
                .couponTarget("USER")
                .couponTargetId(123L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        couponRequestDTO = new CouponRequestDTO(1L, "New Year Coupon", "USER", 123L, ZonedDateTime.now().plusDays(30));
    }

    @Test
    @DisplayName("쿠폰 생성")
    public void testCreateCoupon() {
        // given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(couponRepository.saveAll(anyList())).thenReturn(List.of(coupon));

        // when
        List<Coupon> createdCoupons = couponService.createCoupon(couponRequestDTO, 1);

        // then
        assertNotNull(createdCoupons);
        assertEquals(1, createdCoupons.size());
        assertEquals("New Year Coupon", createdCoupons.get(0).getCouponName());
        verify(couponRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("잘못된 카운트로 쿠폰 생성")
    public void testCreateCouponWithInvalidCount() {
        // when & then
        assertThrows(InvalidCouponRequestException.class, () -> couponService.createCoupon(couponRequestDTO, 0));
    }

    @Test
    @DisplayName("잘못된 정책 아이디로 쿠폰 생성")
    public void testCreateCouponWithInvalidPolicyId() {
        // given
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(InvalidCouponRequestException.class, () -> couponService.createCoupon(couponRequestDTO, 1));
    }

    @Test
    @DisplayName("쿠폰 사용")
    public void testUseCoupon() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // when
        Coupon usedCoupon = couponService.useCoupon(1L);

        // then
        assertNotNull(usedCoupon);
        assertFalse(usedCoupon.isCouponIsActive());
        assertNotNull(usedCoupon.getCouponUsedAt());
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("없는 쿠폰 사용")
    public void testUseCouponNotFound() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponNotFoundException.class, () -> couponService.useCoupon(1L));
    }

    @Test
    @DisplayName("쿠폰 비활성화")
    public void testDeactivateCoupon() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // when
        couponService.deactivateCoupon(1L);

        // then
        assertFalse(coupon.isCouponIsActive());
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("없는 쿠폰 비활성화")
    public void testDeactivateCouponNotFound() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponNotFoundException.class, () -> couponService.deactivateCoupon(1L));
    }

    @Test
    @DisplayName("쿠폰 조회")
    public void testGetCouponById() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // when
        Coupon foundCoupon = couponService.getCouponById(1L);

        // then
        assertNotNull(foundCoupon);
        assertEquals(1L, foundCoupon.getCouponId());
        assertTrue(foundCoupon.isCouponIsActive());
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("없는 쿠폰 조회")
    public void testGetCouponByIdNotFound() {
        // given
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById(1L));
    }

    @Test
    @DisplayName("활성 쿠폰 리스트로 조회")
    public void testGetCouponsByIdsAndActive() {
        // given
        List<Long> couponIds = List.of(1L);
        when(couponRepository.findByCouponIdInAndCouponIsActive(couponIds, true)).thenReturn(List.of(coupon));

        // when
        List<Coupon> coupons = couponService.getCouponsByIdsAndActive(couponIds, true);

        // then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        verify(couponRepository, times(1)).findByCouponIdInAndCouponIsActive(couponIds, true);
    }

    @Test
    @DisplayName("활성 쿠폰 조회")
    public void testGetCouponsByActive() {
        // given
        when(couponRepository.findByCouponIsActive(true)).thenReturn(List.of(coupon));

        // when
        List<Coupon> coupons = couponService.getCouponsByActive(true);

        // then
        assertNotNull(coupons);
        assertEquals(1, coupons.size());
        verify(couponRepository, times(1)).findByCouponIsActive(true);
    }

    @Test
    @DisplayName("Null 리스트로 조회")
    public void testGetCouponsByIdsAndActiveWithNullCouponIds() {
        // when & then
        assertThrows(InvalidCouponRequestException.class, () -> couponService.getCouponsByIdsAndActive(null, true));
    }

    @Test
    @DisplayName("빈 리스트로 조회")
    public void testGetCouponsByIdsAndActiveWithEmptyCouponIds() {
        // given
        List<Long> emptyCouponIds = new ArrayList<>();

        // when
        List<Coupon> coupons = couponService.getCouponsByIdsAndActive(emptyCouponIds, true);

        // then
        assertNotNull(coupons);
        assertTrue(coupons.isEmpty());
        verify(couponRepository, never()).findByCouponIdInAndCouponIsActive(anyList(), anyBoolean());
    }

}
