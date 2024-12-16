package com.nhnacademy.coupon.entity.Dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.ZonedDateTime;

public record CreatCouponDTO(@NotNull Long couponPolicyId, @NotNull String couponName, @NotNull String couponTarget, Long couponTargetId, @Future ZonedDateTime couponDeadline) {}

