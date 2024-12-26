package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.dto.CouponRequestDTO;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/coupons")
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 조회 (쿠폰 ID로 조회)
    @GetMapping("/{couponId}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable(name = "couponId") Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        return ResponseEntity.ok(coupon);
    }

    // 모든 쿠폰 조회
    // Ids 리스트가 전달되면,
    @GetMapping
    public ResponseEntity<List<Coupon>> getCouponsByActive(
            @RequestBody(required = false) List<Long> couponIds,
            @RequestParam(name  = "active", required = false, defaultValue = "true") Boolean active
    ) {
        List<Coupon> couponList;
        if(couponIds != null) {
            couponList = couponService.getCouponsByIdsAndActive(couponIds, active);
        }
        else {
            couponList = couponService.getCouponsByActive(active);
        }
        return ResponseEntity.ok(couponList);
    }

    // 쿠폰 생성
    @PostMapping
    public ResponseEntity<List<Coupon>> createCoupons(
            @RequestParam(value = "count", required = false, defaultValue = "1") int count,
            @RequestBody @Valid CouponRequestDTO couponDTO) {
        if (count <= 0) {
            return ResponseEntity.badRequest().body(List.of()); // count가 0 이하일 경우 400 응답
        }
        List<Coupon> coupons = couponService.createCoupon(couponDTO, count);
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 사용 (쿠폰 사용 처리)
    @PostMapping("/{couponId}/use")
    public ResponseEntity<Coupon> useCoupon(@PathVariable(name = "couponId") Long couponId) {
        Coupon coupon = couponService.useCoupon(couponId);
        return ResponseEntity.ok(coupon);
    }

    // 쿠폰 비활성화 (삭제 처리)
    @PostMapping("/{couponId}/deactivate")
    public ResponseEntity<Map<String, String>> deactivateCoupon(@PathVariable(name = "couponId") Long couponId) {
        couponService.deactivateCoupon(couponId);

        // 응답 메시지를 Map으로 반환하여 JSON 형식으로 보내기
        Map<String, String> response = new HashMap<>();
        response.put("message", "Coupon deactivated successfully");

        return ResponseEntity.ok(response);
    }

}
