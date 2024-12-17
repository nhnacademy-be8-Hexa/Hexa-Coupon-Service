package com.nhnacademy.coupon.exception;

// 쿠폰이 존재하지 않을 경우 던지는 예외
public class CouponNotFoundException extends RuntimeException {
    public CouponNotFoundException(Long couponId) {
        super("Coupon with ID " + couponId + " not found");
    }
}


