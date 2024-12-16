package com.nhnacademy.coupon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class CouponPolicy {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long coupon_policy_id;

    @NotNull
    private String coupon_policy_name;

    @NotNull
    private int min_purchase_amount;

    @NotNull
    private String discount_type;

    @NotNull
    private int discount_value;

    @Setter
    private int max_discount_amount;

    @NotNull
    private boolean is_deleted;

    @Setter
    private String event_type;

    @NotNull
    private ZonedDateTime created_at;

    public void setCoupon_policy_name(@NotNull String coupon_policy_name) {
        this.coupon_policy_name = coupon_policy_name;
    }

    public void setMin_purchase_amount(@NotNull int min_purchase_amount) {
        this.min_purchase_amount = min_purchase_amount;
    }

    public void setDiscount_type(@NotNull String discount_type) {
        this.discount_type = discount_type;
    }

    public void setDiscount_value(@NotNull int discount_value) {
        this.discount_value = discount_value;
    }

    public void setIs_deleted(@NotNull boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    public void setCreated_at(@NotNull ZonedDateTime created_at) {
        this.created_at = created_at;
    }
}
