package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.exception.CouponPolicyNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponPolicyRequestException;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 정책 생성
    @Transactional
    public CouponPolicy createPolicy(CouponPolicyRequestDTO couponPolicyDTO) {
        if (couponPolicyDTO == null) {
            throw new InvalidCouponPolicyRequestException("Invalid coupon policy data");
        }

        CouponPolicy newPolicy = CouponPolicy.of(
                couponPolicyDTO.couponPolicyName(),
                couponPolicyDTO.minPurchaseAmount(),
                couponPolicyDTO.discountType(),
                couponPolicyDTO.discountValue(),
                couponPolicyDTO.maxDiscountAmount(),
                couponPolicyDTO.eventType()
        );

        return couponPolicyRepository.save(newPolicy);
    }

    // 쿠폰 정책 수정
    @Transactional
    public CouponPolicy updatePolicy(Long policyId, CouponPolicyRequestDTO updatedPolicyDTO) {
        // 기존 정책 가져오기
        CouponPolicy existingPolicy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponPolicyNotFoundException(policyId)); // CouponPolicyNotFoundException으로 변경

        // 기존 정책을 비활성화
        CouponPolicy deletedPolicy = existingPolicy.markAsDeleted();

        // 새 정책 저장 및 반환
        return createPolicy(updatedPolicyDTO);
    }

    // 쿠폰 정책 삭제
    @Transactional
    public void deletePolicy(Long policyId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponPolicyNotFoundException(policyId)); // CouponPolicyNotFoundException으로 변경

        policy.markAsDeleted();
    }

    // 쿠폰 정책 조회
    public List<CouponPolicy> getAllPolicies(boolean deleted) {
        return couponPolicyRepository.findByIsDeleted(deleted);
    }


    // 특정 정책만 조회 아이디로 정책 찾기
    public CouponPolicy getPolicyById(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponPolicyNotFoundException(policyId)); // CouponPolicyNotFoundException으로 변경
    }

    // 이벤트 타입으로 정책 찾기 (welcome, birthday)
    public CouponPolicy getPolicyByEventType(String eventType) {
        CouponPolicy couponPolicy = couponPolicyRepository.findByEventType(eventType);
        if(couponPolicy == null) {
            throw new CouponPolicyNotFoundException("event type: %s Coupon Policy Not found.".formatted(eventType));
        }
        return couponPolicy;
    }

}
