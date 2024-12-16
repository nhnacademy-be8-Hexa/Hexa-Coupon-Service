package com.nhnacademy.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long coupon_id;

    @ManyToOne
    @NotNull
    private CouponPolicy couponPolicy;

    @NotNull
    private String coupon_name;

    @NotNull
    private String coupon_target;

    @Setter
    private Long coupon_target_id;

    @Setter
    private ZonedDateTime coupon_deadline;

    @Setter
    private ZonedDateTime coupon_created_at;

    @Setter
    private boolean coupon_is_active;

    @Setter
    private ZonedDateTime coupon_used_at;

    public void setCouponPolicy(@NotNull CouponPolicy couponPolicy) {
        this.couponPolicy = couponPolicy;
    }

    public void setCoupon_name(@NotNull String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public void setCoupon_target(@NotNull String coupon_target) {
        this.coupon_target = coupon_target;
    }

}
