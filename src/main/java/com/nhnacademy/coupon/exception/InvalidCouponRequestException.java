package com.nhnacademy.coupon.exception;

public class InvalidCouponRequestException extends RuntimeException {
    public InvalidCouponRequestException(String message) {
        super(message);
    }
}
