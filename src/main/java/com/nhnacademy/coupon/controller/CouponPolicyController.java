package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.Dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.exception.CouponPolicyNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponPolicyRequestException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CouponPolicy> createPolicy(@RequestBody @Valid CouponPolicyRequestDTO couponPolicy) {
        try {
            CouponPolicy createdPolicy = couponPolicyService.createPolicy(couponPolicy);
            return ResponseEntity.ok(createdPolicy);
        } catch (InvalidCouponPolicyRequestException ex) {
            return ResponseEntity.badRequest().body(null);  // 400 Bad Request
        }
    }

    // 쿠폰 정책 수정
    @PutMapping("/update/{policyId}")
    public ResponseEntity<CouponPolicy> updatePolicy(@PathVariable Long policyId, @RequestBody @Valid CouponPolicyRequestDTO updatedPolicy) {
        try {
            CouponPolicy policy = couponPolicyService.updatePolicy(policyId, updatedPolicy);
            return ResponseEntity.ok(policy);
        } catch (CouponPolicyNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 Not Found
        } catch (InvalidCouponPolicyRequestException ex) {
            return ResponseEntity.badRequest().body(null);  // 400 Bad Request
        }
    }

    // 쿠폰 정책 삭제
    @PostMapping("/delete/{policyId}")
    public ResponseEntity<String> deletePolicy(@PathVariable Long policyId) {
        try {
            couponPolicyService.deletePolicy(policyId);
            return ResponseEntity.ok("Coupon policy deleted successfully");
        } catch (CouponPolicyNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Coupon policy not found");
        }
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
        try {
            CouponPolicy policy = couponPolicyService.getPolicyById(policyId);
            return ResponseEntity.ok(policy);
        } catch (CouponPolicyNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 404 Not Found
        }
    }
}
