package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.Dto.UpdateCouponPolicyDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class CouponPolicyController {
    private final CouponPolicyService couponPolicyService;

    // 쿠폰 정책 생성
    @PostMapping("/create")
    public ResponseEntity<CouponPolicy> createPolicy(@RequestBody @Valid UpdateCouponPolicyDTO couponPolicy) {
        CouponPolicy createdPolicy = couponPolicyService.createPolicy(couponPolicy);
        return ResponseEntity.ok(createdPolicy);
    }

    // 쿠폰 정책 수정
    @PutMapping("/update/{policyId}")
    public ResponseEntity<CouponPolicy> updatePolicy(@PathVariable Long policyId, @RequestBody @Valid UpdateCouponPolicyDTO updatedPolicy) {
        CouponPolicy policy = couponPolicyService.updatePolicy(policyId, updatedPolicy);
        return ResponseEntity.ok(policy);
    }

    // 쿠폰 정책 삭제
    @PostMapping("/delete/{policyId}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long policyId) {
        couponPolicyService.deletePolicy(policyId);
        return ResponseEntity.ok("Coupon policy deleted successfully");
    }

    // 모든 쿠폰 정책 조회
    @GetMapping("/all")
    public ResponseEntity<List<CouponPolicy>> getAllPolicies() {
        List<CouponPolicy> policies = couponPolicyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    // 특정 쿠폰 정책 조회
    @GetMapping("/{policyId}")
    public ResponseEntity<CouponPolicy> getPolicyById(@PathVariable Long policyId) {
        CouponPolicy policy = couponPolicyService.getPolicyById(policyId);
        return ResponseEntity.ok(policy);
    }
}
