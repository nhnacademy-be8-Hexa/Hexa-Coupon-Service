package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import com.nhnacademy.coupon.exception.CouponNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponRequestException;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import com.nhnacademy.coupon.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @InjectMocks
    private CouponService couponService;

    private CouponPolicy couponPolicy;
    private Coupon coupon;
    private CreatCouponDTO couponDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        couponPolicy = CouponPolicy.builder()
                .couponPolicyName("Default Policy")
                .minPurchaseAmount(1000)  // 기본값 설정
                .discountType("PERCENTAGE") // 기본값 설정
                .discountValue(10)         // 기본값 설정
                .maxDiscountAmount(500)    // 기본값 설정
                .eventType("SUMMER_SALE")  // 기본값 설정
                .createdAt(ZonedDateTime.now()) // 생성일 설정
                .build();

        // Set up CreatCouponDTO
        couponDTO = new CreatCouponDTO(
                1L,                              // couponPolicyId
                "Summer Discount",               // couponName
                "ALL",                           // couponTarget
                0L,                              // couponTargetId
                ZonedDateTime.now().plusDays(30) // couponDeadline
        );

        // Set up Coupon
        coupon = Coupon.of(couponDTO, couponPolicy);
    }

    @Test
    void testCreateCoupon_success() {
        when(couponPolicyRepository.findById(couponDTO.couponPolicyId()))
                .thenReturn(Optional.of(couponPolicy));

        int count = 3;

        when(couponRepository.saveAll(anyList()))
                .thenReturn(List.of(
                        Coupon.of(couponDTO, couponPolicy),
                        Coupon.of(couponDTO, couponPolicy),
                        Coupon.of(couponDTO, couponPolicy)
                ));

        List<Coupon> createdCoupon = couponService.createCoupon(couponDTO, count);

        assertNotNull(createdCoupon);
        assertEquals(count, createdCoupon.size());
        assertEquals("Summer Discount", createdCoupon.get(0).getCouponName());
        assertEquals("ALL", createdCoupon.get(0).getCouponTarget());

        verify(couponPolicyRepository, times(1)).findById(couponDTO.couponPolicyId());
        verify(couponRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testCreateMultipleCoupons_invalidPolicy() {
        when(couponPolicyRepository.findById(couponDTO.couponPolicyId()))
                .thenReturn(Optional.empty());

        int count = 3;
        assertThrows(InvalidCouponRequestException.class, () -> couponService.createCoupon(couponDTO, count));
        verify(couponPolicyRepository, times(1)).findById(couponDTO.couponPolicyId());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void shouldThrowExceptionWhenCountIsZeroOrNegative() {
        CreatCouponDTO validDTO = new CreatCouponDTO(
                1L,
                "Summer Discount",
                "ALL",
                0L,
                ZonedDateTime.now().plusDays(1)
        );

        assertThrows(
                InvalidCouponRequestException.class,
                () -> couponService.createCoupon(validDTO, 0),
                "쿠폰 개수는 1개 이상이어야 합니다."
        );
        assertThrows(
                InvalidCouponRequestException.class,
                () -> couponService.createCoupon(validDTO, -5),
                "쿠폰 개수는 1개 이상이어야 합니다."
        );
    }

    @Test
    void testCreateMultipleCoupons_invalidDeadline() {
        CreatCouponDTO invalidDTO = new CreatCouponDTO(
                1L,
                "Summer Discount",
                "ALL",
                0L,
                ZonedDateTime.now().minusDays(1)
        );

        when(couponPolicyRepository.findById(invalidDTO.couponPolicyId()))
                .thenReturn(Optional.of(couponPolicy));

        int count = 3;

        assertThrows(InvalidCouponRequestException.class, () -> {
            couponService.createCoupon(invalidDTO, count);
        });

        verify(couponPolicyRepository, times(1)).findById(invalidDTO.couponPolicyId());
        verify(couponRepository, never()).saveAll(anyList());
    }

    @Test
    void testGetCouponById_success() {
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        Coupon foundCoupon = couponService.getCouponById(1L);

        assertNotNull(foundCoupon);
        assertEquals("Summer Discount", foundCoupon.getCouponName());
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCouponById_notFound() {
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> couponService.getCouponById(1L));
        verify(couponRepository, times(1)).findById(1L);
    }

    @Test
    void testGetCouponsByIds_success() {
        List<Long> couponIds = Arrays.asList(1L, 2L);
        when(couponRepository.findAllById(couponIds)).thenReturn(Arrays.asList(coupon));

        List<Coupon> coupons = couponService.getCouponsByIds(couponIds);

        assertNotNull(coupons);
        assertFalse(coupons.isEmpty());
        verify(couponRepository, times(1)).findAllById(couponIds);
    }

    @Test
    void testGetCouponsByIds_invalidInput() {
        assertThrows(InvalidCouponRequestException.class, () -> couponService.getCouponsByIds(null));
        assertThrows(InvalidCouponRequestException.class, () -> couponService.getCouponsByIds(List.of()));
        verify(couponRepository, never()).findAllById(any());
    }

    @Test
    void testGetAllCoupons() {
        when(couponRepository.findAll()).thenReturn(Arrays.asList(coupon));

        List<Coupon> coupons = couponService.getAllCoupons();

        assertNotNull(coupons);
        assertFalse(coupons.isEmpty());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testUseCoupon_success() {
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(coupon)).thenReturn(coupon);

        Coupon usedCoupon = couponService.useCoupon(1L);

        assertNotNull(usedCoupon);
        assertNotNull(usedCoupon.getCouponUsedAt());
        verify(couponRepository, times(1)).findById(1L);
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testUseCoupon_alreadyUsed() {
        coupon.markAsUsed(ZonedDateTime.now());
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        assertThrows(IllegalStateException.class, () -> couponService.useCoupon(1L));
        verify(couponRepository, times(1)).findById(1L);
        verify(couponRepository, never()).save(any());
    }

    @Test
    void testDeactivateCoupon_success() {
        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(couponRepository.save(coupon)).thenReturn(coupon);

        couponService.deactivateCoupon(1L);

        assertFalse(coupon.isCouponIsActive());
        verify(couponRepository, times(1)).findById(1L);
        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    void testGetUsedCoupons_success() {
        Coupon usedCoupon = Coupon.of(couponDTO, couponPolicy);
        usedCoupon.markAsUsed(ZonedDateTime.now());
        when(couponRepository.findUsedCoupons()).thenReturn(Arrays.asList(usedCoupon));

        List<Coupon> usedCoupons = couponService.getUsedCoupons();

        assertNotNull(usedCoupons);
        assertFalse(usedCoupons.isEmpty());
        assertEquals(1, usedCoupons.size());
        verify(couponRepository, times(1)).findUsedCoupons();
    }

    @Test
    void testGetUsedCoupons_empty() {

        when(couponRepository.findUsedCoupons()).thenReturn(new ArrayList<>());

        List<Coupon> usedCoupons = couponService.getUsedCoupons();

        assertNotNull(usedCoupons);
        assertTrue(usedCoupons.isEmpty());
        verify(couponRepository, times(1)).findUsedCoupons();
    }

    @Test
    void testGetUsedCouponsByIds_success() {

        Coupon usedCoupon = Coupon.of(couponDTO, couponPolicy);
        usedCoupon.markAsUsed(ZonedDateTime.now());
        List<Long> couponIds = Arrays.asList(1L, 2L);

        when(couponRepository.findUsedCouponsByIds(couponIds)).thenReturn(Arrays.asList(usedCoupon));

        List<Coupon> usedCoupons = couponService.getUsedCouponsByIds(couponIds);

        assertNotNull(usedCoupons);
        assertFalse(usedCoupons.isEmpty());
        assertEquals(1, usedCoupons.size());
        verify(couponRepository, times(1)).findUsedCouponsByIds(couponIds);
    }

    @Test
    void testGetUsedCouponsByIds_invalidInput_null() {

        assertThrows(InvalidCouponRequestException.class, () -> couponService.getUsedCouponsByIds(null));
        verify(couponRepository, never()).findUsedCouponsByIds(any());
    }

    @Test
    void testGetUsedCouponsByIds_invalidInput_empty() {
        assertThrows(InvalidCouponRequestException.class, () -> couponService.getUsedCouponsByIds(new ArrayList<>()));
        verify(couponRepository, never()).findUsedCouponsByIds(any());
    }

    @Test
    void testGetUsedCouponsByIds_noUsedCoupons() {

        List<Long> couponIds = Arrays.asList(1L, 2L);
        when(couponRepository.findUsedCouponsByIds(couponIds)).thenReturn(new ArrayList<>());

        List<Coupon> usedCoupons = couponService.getUsedCouponsByIds(couponIds);

        assertNotNull(usedCoupons);
        assertTrue(usedCoupons.isEmpty());
        verify(couponRepository, times(1)).findUsedCouponsByIds(couponIds);
    }


}
