package com.nhnacademy.coupon.entity.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

public record CouponPolicyRequestDTO(
        @NotBlank
        @Length(max = 50) String couponPolicyName,
        @NotNull
        @PositiveOrZero int minPurchaseAmount,
        @NotBlank
        @Length(max = 20) String discountType,
        @NotNull
        @PositiveOrZero int discountValue,
        @PositiveOrZero int maxDiscountAmount,
        @NotNull boolean isDeleted,
        @Length(max = 20) String eventType,
        @NotNull ZonedDateTime createdAt
) {}
