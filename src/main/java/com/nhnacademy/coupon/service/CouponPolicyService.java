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
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 정책 생성
    public CouponPolicy createPolicy(CouponPolicyRequestDTO couponPolicy) {
        if (couponPolicy == null) {
            throw new InvalidCouponPolicyRequestException("Invalid coupon policy data");
        }

        CouponPolicy newPolicy = CouponPolicy.of(couponPolicy);

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
        couponPolicyRepository.save(deletedPolicy);  // 기존 정책을 비활성화하고 저장

        // 새로운 정책 생성
        CouponPolicy newPolicy = CouponPolicy.of(updatedPolicyDTO);

        // 새 정책 저장 및 반환
        return couponPolicyRepository.save(newPolicy);
    }

    // 쿠폰 정책 삭제
    @Transactional
    public void deletePolicy(Long policyId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new CouponPolicyNotFoundException(policyId)); // CouponPolicyNotFoundException으로 변경

        policy.markAsDeleted();
        couponPolicyRepository.save(policy);
    }

    // 쿠폰 정책 조회
    public List<CouponPolicy> getAllPolicies() {
        return couponPolicyRepository.findAll()
                .stream()
                .filter(policy -> !policy.isDeleted())
                .collect(Collectors.toList()); // 삭제되지 않은 것부터 조회
    }

    // 특정 정책만 조회 아이디로 정책 찾기
    public CouponPolicy getPolicyById(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .filter(policy -> !policy.isDeleted())
                .orElseThrow(() -> new CouponPolicyNotFoundException(policyId)); // CouponPolicyNotFoundException으로 변경
    }

}
