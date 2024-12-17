package com.nhnacademy.coupon.entity.Dto;

import jakarta.validation.constraints.NotNull;

public record UpdateCouponPolicyDTO(
        @NotNull String couponPolicyName,
        @NotNull int minPurchaseAmount,
        @NotNull String discountType,
        @NotNull int discountValue,
        int maxDiscountAmount,
        String eventType
) {}
