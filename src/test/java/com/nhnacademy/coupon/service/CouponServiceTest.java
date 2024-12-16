package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponRepository;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @BeforeEach
    public void setUp() {
        couponPolicy = new CouponPolicy(
                1L, "Discount Policy", 20000, "percent", 20, 10000, false,
                "welcome", ZonedDateTime.now()
        );
    }

    //쿠폰 생성
    @Test
    public void testCreateCoupon() {
        String couponName = "Summer Discount";
        String couponTarget = "ALL";
        Long couponTargetId = null;
        ZonedDateTime couponDeadline = ZonedDateTime.now().plusDays(30);

        Coupon coupon = new Coupon();

        // 관리자가 입력한 값으로 쿠폰 설정
        coupon.setCouponPolicy(couponPolicy);  // 정책 설정
        coupon.setCoupon_name(couponName);  // 쿠폰 이름 설정
        coupon.setCoupon_target(couponTarget);  // 쿠폰 대상 설정
        coupon.setCoupon_target_id(couponTargetId); // null일 경우 0 설정 //couponTargetId != null ? couponTargetId : 0
        coupon.setCoupon_deadline(couponDeadline);  // 쿠폰 유효기간 설정
        coupon.setCoupon_created_at(ZonedDateTime.now());  // 생성 시간은 현재 시간
        coupon.setCoupon_is_active(true);  // 기본적으로 활성 상태
        coupon.setCoupon_used_at(null);  // 초기에는 사용되지 않음


        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon createdCoupon = couponService.createCoupon(1L, couponName, couponTarget, couponTargetId, couponDeadline);


        assertNotNull(createdCoupon);
        assertEquals(couponName, createdCoupon.getCoupon_name());
        assertEquals(couponTarget, createdCoupon.getCoupon_target());
        assertEquals(couponDeadline, createdCoupon.getCoupon_deadline());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    // 쿠폰 조회 (쿠폰 ID로 조회)
    @Test
    public void testGetCouponById() {
        // Given
        Coupon coupon = new Coupon();
        coupon.setCoupon_id(1L);
        coupon.setCoupon_name("Summer Discount");
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        // When
        Coupon foundCoupon = couponService.getCouponById(1L);

        // Then
        assertNotNull(foundCoupon);
        assertEquals(1L, foundCoupon.getCoupon_id());
        assertEquals("Summer Discount", foundCoupon.getCoupon_name());
        verify(couponRepository, times(1)).findById(1L);
    }

    // 모든 쿠폰 조회
    @Test
    public void testGetAllCoupons() {

        Coupon coupon1 = new Coupon();
        coupon1.setCoupon_id(1L);
        coupon1.setCoupon_name("Coupon 1");

        Coupon coupon2 = new Coupon();
        coupon2.setCoupon_id(2L);
        coupon2.setCoupon_name("Coupon 2");

        List<Coupon> coupons = List.of(coupon1, coupon2);

        when(couponRepository.findAll()).thenReturn(coupons);

        List<Coupon> allCoupons = couponService.getAllCoupons();

        assertNotNull(allCoupons);
        assertEquals(2, allCoupons.size());
        assertEquals("Coupon 1", allCoupons.get(0).getCoupon_name());
        assertEquals("Coupon 2", allCoupons.get(1).getCoupon_name());
        verify(couponRepository, times(1)).findAll();
    }

    // 모든 쿠폰 조회: 결과가 빈 리스트인 경우
    @Test
    public void testGetAllCouponsEmpty() {
        // Given
        when(couponRepository.findAll()).thenReturn(List.of());

        // When
        List<Coupon> allCoupons = couponService.getAllCoupons();

        // Then
        assertNotNull(allCoupons);
        assertTrue(allCoupons.isEmpty());
        verify(couponRepository, times(1)).findAll();
    }

    // 쿠폰 ID 리스트로 쿠폰 조회
    @Test
    public void testGetCouponsByIds() {
        // Given
        List<Long> couponIds = List.of(1L, 2L);

        Coupon coupon1 = new Coupon();
        coupon1.setCoupon_id(1L);
        coupon1.setCoupon_name("Coupon 1");

        Coupon coupon2 = new Coupon();
        coupon2.setCoupon_id(2L);
        coupon2.setCoupon_name("Coupon 2");

        List<Coupon> coupons = List.of(coupon1, coupon2);

        when(couponRepository.findAllById(couponIds)).thenReturn(coupons);

        // When
        List<Coupon> foundCoupons = couponService.getCouponsByIds(couponIds);

        // Then
        assertNotNull(foundCoupons);
        assertEquals(2, foundCoupons.size());
        assertEquals("Coupon 1", foundCoupons.get(0).getCoupon_name());
        assertEquals("Coupon 2", foundCoupons.get(1).getCoupon_name());
        verify(couponRepository, times(1)).findAllById(couponIds);
    }

    // 쿠폰 ID 리스트가 비어 있는 경우 예외 발생
    @Test
    public void testGetCouponsByIdsEmptyList() {
        // Given
        List<Long> emptyCouponIds = List.of();

        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            couponService.getCouponsByIds(emptyCouponIds);
        });
        assertEquals("Coupon ID list cannot be null or empty", thrown.getMessage());
        verify(couponRepository, never()).findAllById(any());
    }

    // 쿠폰 ID 리스트가 null인 경우 예외 발생
    @Test
    public void testGetCouponsByIdsNullList() {
        // When & Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            couponService.getCouponsByIds(null);
        });
        assertEquals("Coupon ID list cannot be null or empty", thrown.getMessage());
        verify(couponRepository, never()).findAllById(any());
    }


    // 쿠폰 사용
    @Test
    public void testUseCoupon() {
        Coupon coupon = new Coupon();
        coupon.setCoupon_id(1L);
        coupon.setCoupon_name("Winter Sale");
        coupon.setCoupon_is_active(true);
        coupon.setCoupon_used_at(null); // 사용 안된 상태

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon usedCoupon = couponService.useCoupon(1L);

        assertNotNull(usedCoupon);
        assertNotNull(usedCoupon.getCoupon_used_at());  // 사용일시가 설정된 상태
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    // 이미 사용된 쿠폰을 다시 사용할 때 예외가 발생하는지 확인
    @Test
    public void testUseCouponAlreadyUsed() {

        Coupon coupon = new Coupon();
        coupon.setCoupon_id(1L);
        coupon.setCoupon_name("Spring Sale");
        coupon.setCoupon_is_active(true);
        coupon.setCoupon_used_at(ZonedDateTime.now()); // 이미 사용된 상태

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            couponService.useCoupon(1L);
        });
        assertEquals("Coupon has already been used", thrown.getMessage());
    }

    // 쿠폰 비활성화
    @Test
    public void testDeactivateCoupon() {

        Coupon coupon = new Coupon();
        coupon.setCoupon_id(1L);
        coupon.setCoupon_name("Autumn Sale");
        coupon.setCoupon_is_active(true);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        couponService.deactivateCoupon(1L);

        assertFalse(coupon.isCoupon_is_active());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    //만료된 쿠폰 생성 시 예외 처리
    @Test
    public void testCreateCouponWithExpiredDeadline() {

        String couponName = "Expired Discount";
        String couponTarget = "ALL";
        Long couponTargetId = null;
        ZonedDateTime couponDeadline = ZonedDateTime.now().minusDays(1);  // 과거 날짜

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(1L, couponName, couponTarget, couponTargetId, couponDeadline);
        });
        assertEquals("Coupon deadline must be in the future", thrown.getMessage());
    }
}
