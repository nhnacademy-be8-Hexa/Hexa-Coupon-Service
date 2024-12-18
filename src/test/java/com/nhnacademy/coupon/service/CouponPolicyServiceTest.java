package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.exception.CouponPolicyNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponPolicyRequestException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponPolicyServiceTest {

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    @InjectMocks
    private CouponPolicyService couponPolicyService;

    private CouponPolicy couponPolicy;
    private CouponPolicyRequestDTO couponPolicyDTO;

    @BeforeEach
    public void setUp() {
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

        couponPolicyDTO = new CouponPolicyRequestDTO(
                "Holiday Discount", 1000, "PERCENTAGE", 10, 500, "birthday"
        );
    }

    @Test
     void testCreatePolicy() {
        // given
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        // when
        CouponPolicy createdPolicy = couponPolicyService.createPolicy(couponPolicyDTO);

        // then
        assertNotNull(createdPolicy);
        assertEquals("Holiday Discount", createdPolicy.getCouponPolicyName());
        assertFalse(createdPolicy.isDeleted());  // 기본 값이 false이어야 함
        assertNotNull(createdPolicy.getCreatedAt());  // 생성일시가 null이 아님
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    @Test
     void testCreatePolicyInvalidData() {
        // given
        CouponPolicyRequestDTO invalidCouponPolicyDTO = null;

        // when & then
        assertThrows(InvalidCouponPolicyRequestException.class, () -> couponPolicyService.createPolicy(invalidCouponPolicyDTO));
    }

    @Test
     void testUpdatePolicy() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        // when
        CouponPolicy updatedPolicy = couponPolicyService.updatePolicy(1L, couponPolicyDTO);

        // then
        assertNotNull(updatedPolicy);
        assertEquals("Holiday Discount", updatedPolicy.getCouponPolicyName());
        assertTrue(updatedPolicy.isDeleted());
        assertNotNull(updatedPolicy.getCreatedAt());  // 생성일시가 null이 아님
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    @Test
     void testUpdatePolicyNotFound() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.updatePolicy(999L, couponPolicyDTO));
    }

    @Test
     void testDeletePolicy() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));

        // when
        couponPolicyService.deletePolicy(1L);

        // then
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
     void testDeletePolicyNotFound() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.deletePolicy(999L));
    }

    @Test
     void testGetAllPolicies() {
        // given
        when(couponPolicyRepository.findByIsDeleted(anyBoolean())).thenReturn(List.of(couponPolicy));

        // when
        List<CouponPolicy> policies = couponPolicyService.getAllPolicies(false);

        // then
        assertNotNull(policies);
        assertFalse(policies.isEmpty());
        assertEquals(1, policies.size());
        verify(couponPolicyRepository, times(1)).findByIsDeleted(anyBoolean());
    }

    @Test
     void testGetPolicyById() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));

        // when
        CouponPolicy foundPolicy = couponPolicyService.getPolicyById(1L);

        // then
        assertNotNull(foundPolicy);
        assertEquals(1L, foundPolicy.getCouponPolicyId());
        assertFalse(foundPolicy.isDeleted());
        assertNotNull(foundPolicy.getCreatedAt());  // 생성일시가 null이 아님
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    @Test
     void testGetPolicyByIdNotFound() {
        // given
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.getPolicyById(999L));
    }

    @Test
     void testGetPolicyByEventType() {
        // given
        when(couponPolicyRepository.findByEventType(anyString())).thenReturn(couponPolicy);

        // when
        CouponPolicy foundPolicy = couponPolicyService.getPolicyByEventType("birthday");

        // then
        assertNotNull(foundPolicy);
        assertEquals("birthday", foundPolicy.getEventType());
        assertFalse(foundPolicy.isDeleted());
        assertNotNull(foundPolicy.getCreatedAt());  // 생성일시가 null이 아님
        verify(couponPolicyRepository, times(1)).findByEventType(anyString());
    }

    @Test
     void testGetPolicyByEventTypeNotFound() {
        // given
        when(couponPolicyRepository.findByEventType(anyString())).thenReturn(null);

        // when & then
        assertThrows(CouponPolicyNotFoundException.class, () -> couponPolicyService.getPolicyByEventType("nonexistent"));
    }
}
