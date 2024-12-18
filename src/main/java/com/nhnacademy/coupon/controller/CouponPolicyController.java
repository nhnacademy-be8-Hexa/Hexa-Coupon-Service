package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.Dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.exception.CouponPolicyNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponPolicyRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    // 모든 쿠폰 정책 조회
    @GetMapping
    public ResponseEntity<List<CouponPolicy>> getAllPolicies(
            @RequestParam(name = "deleted", required = false, defaultValue = "false") Boolean deleted
    ) {
        List<CouponPolicy> policies = couponPolicyService.getAllPolicies(deleted);
        return ResponseEntity.ok(policies);
    }

    // 특정 쿠폰 정책 조회
    @GetMapping("/{policyId}")
    public ResponseEntity<CouponPolicy> getPolicyById(@PathVariable(name = "policyId") Long policyId) {
        CouponPolicy policy = couponPolicyService.getPolicyById(policyId);
        return ResponseEntity.ok(policy);
    }

    // 쿠폰 정책 생성
    @PostMapping
    public ResponseEntity<CouponPolicy> createPolicy(@RequestBody @Valid CouponPolicyRequestDTO couponPolicy) {
        CouponPolicy createdPolicy = couponPolicyService.createPolicy(couponPolicy);
        return ResponseEntity.ok(createdPolicy);
    }

    // 쿠폰 정책 수정
    @PatchMapping("/{policyId}")
    public ResponseEntity<CouponPolicy> updatePolicy(@PathVariable(name = "policyId") Long policyId, @RequestBody @Valid CouponPolicyRequestDTO updatedPolicy) {
        CouponPolicy policy = couponPolicyService.updatePolicy(policyId, updatedPolicy);
        return ResponseEntity.ok(policy);

    }

    // 쿠폰 정책 삭제
    @DeleteMapping("/{policyId}")
    public ResponseEntity<String> deletePolicy(@PathVariable(name = "policyId") Long policyId) {
        couponPolicyService.deletePolicy(policyId);
        return ResponseEntity.ok("Coupon policy deleted successfully");
    }

}
