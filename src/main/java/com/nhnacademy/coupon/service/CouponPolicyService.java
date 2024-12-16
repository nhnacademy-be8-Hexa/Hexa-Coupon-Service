package com.nhnacademy.coupon.service;

import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    //쿠폰 정책 생성
    public CouponPolicy createPolicy(CouponPolicy couponPolicy){
        couponPolicy.setCreated_at(ZonedDateTime.now());
        return couponPolicyRepository.save(couponPolicy);
    }

    // 쿠폰 정책 수정
    @Transactional
    public CouponPolicy updatePolicy(Long policyId, CouponPolicy updatedPolicy) {
        // 기존 정책 가져오기
        CouponPolicy existingPolicy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon policy id not found"));

        // 기존 정책을 비활성화
        existingPolicy.setIs_deleted(true);
        couponPolicyRepository.save(existingPolicy);

        // 기존정책의 수정된 내용을 바탕으로 새로운 정책 생성
        CouponPolicy newPolicy = new CouponPolicy();
        newPolicy.setCoupon_policy_name(updatedPolicy.getCoupon_policy_name());
        newPolicy.setMin_purchase_amount(updatedPolicy.getMin_purchase_amount());
        newPolicy.setDiscount_type(updatedPolicy.getDiscount_type());
        newPolicy.setDiscount_value((updatedPolicy.getDiscount_value()));
        newPolicy.setMax_discount_amount(updatedPolicy.getMax_discount_amount());
        newPolicy.setEvent_type(updatedPolicy.getEvent_type());
        newPolicy.setCreated_at(ZonedDateTime.now()); // 새로운 생성 시간

        // 새 정책 저장 및 반환
        return couponPolicyRepository.save(newPolicy);
    }

    // 쿠폰 정책 삭제
    @Transactional
    public void deletePolicy(Long policyId) {
        CouponPolicy policy = couponPolicyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon policy id not found"));

        policy.setIs_deleted(true);
        couponPolicyRepository.save(policy);
    }

    // 쿠폰 정책 조회
    public List<CouponPolicy> getAllPolicies() {
        return couponPolicyRepository.findAll()
                .stream()
                .filter(policy -> !policy.is_deleted())
                .collect(Collectors.toList()); // 삭제되지 않은 것부터 조회
    }

    // 특정 정책만 조회 아이디로 정책 찾기
    public CouponPolicy getPolicyById(Long policyId) {
        return couponPolicyRepository.findById(policyId)
                .filter(policy -> !policy.is_deleted())
                .orElseThrow(() -> new IllegalArgumentException("Coupon policy id not found"));
    }

}
