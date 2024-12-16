package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Dto.UpdateCouponPolicyDTO;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class CouponPolicyServiceTest {

    @InjectMocks
    private CouponPolicyService couponPolicyService;

    @Mock
    private CouponPolicyRepository couponPolicyRepository;

    private CouponPolicy couponPolicy;
    private UpdateCouponPolicyDTO updateCouponPolicyDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        couponPolicy = new CouponPolicy("Test Policy", 1000, "PERCENTAGE", 10, 100, "EVENT", ZonedDateTime.now());
        updateCouponPolicyDTO = new UpdateCouponPolicyDTO("Updated Policy", 2000, "FIXED", 20, 150, "NEW_EVENT");
    }

    // 쿠폰 정책 생성
    @Test
    void createPolicyTest() {
        // CouponPolicy.of() 메서드를 사용해 DTO로부터 쿠폰 정책 객체를 생성
        UpdateCouponPolicyDTO dto = new UpdateCouponPolicyDTO(
                "Test Policy",       // couponPolicyName
                1000,                // minPurchaseAmount
                "PERCENTAGE",        // discountType
                10,                  // discountValue
                500,                 // maxDiscountAmount
                "Seasonal Sale"      // eventType
        );

        CouponPolicy couponPolicy = CouponPolicy.of(dto);  // DTO로부터 CouponPolicy 객체 생성

        // save 메서드가 호출되면 couponPolicy 객체를 반환하도록 설정
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        // 서비스 호출
        CouponPolicy createdPolicy = couponPolicyService.createPolicy(dto);

        // 반환된 정책이 null이 아닌지 확인
        assertNotNull(createdPolicy);
        assertEquals("Test Policy", createdPolicy.getCouponPolicyName());  // 이름이 올바른지 확인
        assertEquals(1000, createdPolicy.getMinPurchaseAmount());  // 최소 구매 금액이 올바른지 확인

        // couponPolicyRepository.save() 메서드가 정확히 한 번 호출되었는지 확인
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    // 쿠폰 정책 수정
    @Test
    void updatePolicyTest() {
        // 기존 정책을 mock으로 설정
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));

        // 저장된 쿠폰 정책을 업데이트 후 반환하도록 mock 설정
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(new CouponPolicy(
                "Updated Policy", 2000, "FIXED", 20, 150, "NEW_EVENT", ZonedDateTime.now()
        ));

        CouponPolicy updatedPolicy = couponPolicyService.updatePolicy(1L, updateCouponPolicyDTO);

        // 반환된 값 검증
        assertNotNull(updatedPolicy);
        assertEquals("Updated Policy", updatedPolicy.getCouponPolicyName()); // 수정된 정책명을 검증
        assertEquals(2000, updatedPolicy.getMinPurchaseAmount());
        assertEquals("FIXED", updatedPolicy.getDiscountType());
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponPolicyRepository, times(2)).save(any(CouponPolicy.class));  // 저장 호출 확인
    }

    // 쿠폰 정책 삭제
    @Test
    void deletePolicyTest() {
        // 기존 정책을 mock으로 설정
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));

        couponPolicyService.deletePolicy(1L);

        assertTrue(couponPolicy.isDeleted());
        verify(couponPolicyRepository, times(1)).findById(anyLong());
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    // 쿠폰 정책 조회 - 전체
    @Test
    void getAllPoliciesTest() {
        when(couponPolicyRepository.findAll()).thenReturn(List.of(couponPolicy));

        List<CouponPolicy> policies = couponPolicyService.getAllPolicies();

        assertNotNull(policies);
        assertEquals(1, policies.size());
        assertFalse(policies.get(0).isDeleted());
        verify(couponPolicyRepository, times(1)).findAll();
    }

    // 쿠폰 정책 조회 - ID로
    @Test
    void getPolicyByIdTest() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.of(couponPolicy));

        CouponPolicy foundPolicy = couponPolicyService.getPolicyById(1L);

        assertNotNull(foundPolicy);
        assertEquals("Test Policy", foundPolicy.getCouponPolicyName());
        verify(couponPolicyRepository, times(1)).findById(anyLong());
    }

    // 예외 처리: 쿠폰 정책 ID가 없는 경우
    @Test
    void updatePolicyNotFoundTest() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponPolicyService.updatePolicy(1L, updateCouponPolicyDTO));
    }

    // 예외 처리: 쿠폰 정책 삭제 시 ID가 없는 경우
    @Test
    void deletePolicyNotFoundTest() {
        when(couponPolicyRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponPolicyService.deletePolicy(1L));
    }

    // 쿠폰 정책 비활성화 테스트
    @Test
    void markAsDeletedTest() {
        couponPolicy.markAsDeleted();

        assertTrue(couponPolicy.isDeleted());
    }
}
