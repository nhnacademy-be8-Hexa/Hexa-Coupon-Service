package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 생성
    @PostMapping("/create")
    public ResponseEntity<List<Coupon>> createCoupons(
            @RequestParam("count") int count,
            @RequestBody @Valid CreatCouponDTO couponDTO) {

        if (count <= 0) {
            return ResponseEntity.badRequest().body(List.of()); // count가 0 이하일 경우 400 응답
        }

        List<Coupon> coupons = couponService.createCoupon(couponDTO, count);
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 조회 (쿠폰 ID로 조회)
    @GetMapping("/{couponId}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        return ResponseEntity.ok(coupon);
    }

    // 모든 쿠폰 조회
    @GetMapping("/all")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 ID 리스트로 쿠폰 조회
    @GetMapping("/list")
    public ResponseEntity<List<Coupon>> getCouponsByIds(@RequestParam List<Long> couponIds) {
        if (couponIds == null || couponIds.isEmpty()) {
            return ResponseEntity.badRequest().body(List.of()); // 쿠폰 ID가 없을 경우 400 응답
        }
        List<Coupon> coupons = couponService.getCouponsByIds(couponIds);
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 사용 (쿠폰 사용 처리)
    @PostMapping("/use/{couponId}")
    public ResponseEntity<Coupon> useCoupon(@PathVariable Long couponId) {
        Coupon coupon = couponService.useCoupon(couponId);
        return ResponseEntity.ok(coupon);
    }

    // 쿠폰 비활성화 (삭제 처리)
    @PostMapping("/deactivate/{couponId}")
    public ResponseEntity<String> deactivateCoupon(@PathVariable Long couponId) {
        couponService.deactivateCoupon(couponId);
        return ResponseEntity.ok("Coupon deactivated successfully");
    }

    // 전체 쿠폰 중 사용된 쿠폰 조회
    @GetMapping("/used")
    public ResponseEntity<List<Coupon>> getUsedCoupons() {
        List<Coupon> usedCoupons = couponService.getUsedCoupons();
        return ResponseEntity.ok(usedCoupons);
    }

    // 쿠폰 ID 리스트 중 사용된 쿠폰 조회
    @GetMapping("/used/by-ids")
    public ResponseEntity<List<Coupon>> getUsedCouponsByIds(@RequestParam List<Long> couponIds) {
        if (couponIds == null || couponIds.isEmpty()) {
            return ResponseEntity.badRequest().build(); // ID 리스트가 비어 있으면 400 응답
        }

        List<Coupon> usedCoupons = couponService.getUsedCouponsByIds(couponIds);

        return ResponseEntity.ok(usedCoupons);
    }
}
