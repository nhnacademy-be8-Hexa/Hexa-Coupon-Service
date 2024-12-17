package com.nhnacademy.coupon.exception;

public class InvalidCouponPolicyRequestException extends RuntimeException {
    public InvalidCouponPolicyRequestException(String message) {
        super(message);
    }
  public InvalidCouponPolicyRequestException(String message, Throwable cause) {
    super(message, cause);
  }
}
