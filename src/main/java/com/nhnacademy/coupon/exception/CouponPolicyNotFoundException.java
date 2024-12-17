package com.nhnacademy.coupon.exception;

public class CouponPolicyNotFoundException extends RuntimeException {
    public CouponPolicyNotFoundException(Long policyId) {
        super("Coupon policy with ID " + policyId + " not found.");
    }

    public CouponPolicyNotFoundException(String message) {
        super(message);
    }

    public CouponPolicyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
