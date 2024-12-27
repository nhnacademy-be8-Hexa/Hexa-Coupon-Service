package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.dto.CouponPolicyRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class CouponPolicyController {

    private final CouponPolicyService couponPolicyService;

    // 모든 쿠폰 정책 조회
    @GetMapping
    public ResponseEntity<List<CouponPolicy>> getPolicies(
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

    // 특정 이벤트 타입의 쿠폰 정책 조회
    @GetMapping("/{eventType}/eventType")
    public ResponseEntity<CouponPolicy> getPolicyByEventType(
            @PathVariable("eventType") String eventType
    ){
        CouponPolicy policy = couponPolicyService.getPolicyByEventType(eventType);
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
    public ResponseEntity<CouponPolicy> updatePolicy(
            @PathVariable(name = "policyId") Long policyId,
            @RequestBody @Valid CouponPolicyRequestDTO updatedPolicy
    ) {
        CouponPolicy policy = couponPolicyService.updatePolicy(policyId, updatedPolicy);
        return ResponseEntity.ok(policy);

    }

    // 쿠폰 정책 삭제
    @DeleteMapping("/{policyId}")
    public ResponseEntity<Map<String, String>>  deletePolicy(@PathVariable(name = "policyId") Long policyId) {
        couponPolicyService.deletePolicy(policyId);

        // 응답 메시지를 Map으로 반환하여 JSON 형식으로 보내기
        Map<String, String> response = new HashMap<>();
        response.put("message", "Coupon policy deleted successfully");

        return ResponseEntity.ok(response);
    }

}
