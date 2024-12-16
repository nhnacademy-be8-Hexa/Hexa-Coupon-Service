package com.nhnacademy.coupon.controller;

import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.service.CouponService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    // 쿠폰 생성
    @PostMapping("/create")
    public ResponseEntity<Coupon> createCoupon(
            @RequestBody @Valid CreatCouponDTO creatCouponDTO) {

        Coupon coupon = couponService.createCoupon(creatCouponDTO);
        return ResponseEntity.ok(coupon);
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


}
