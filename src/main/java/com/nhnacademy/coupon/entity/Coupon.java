package com.nhnacademy.coupon.entity;

import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @ManyToOne
    @NotNull
    private CouponPolicy couponPolicy;

    @NotNull
    private String couponName;

    @NotNull
    private String couponTarget;

    private Long couponTargetId;

    private ZonedDateTime couponDeadline;

    private ZonedDateTime couponCreatedAt;

    private boolean couponIsActive;

    private ZonedDateTime couponUsedAt;

    @Builder
    public Coupon(CouponPolicy couponPolicy, String couponName, String couponTarget, Long couponTargetId, ZonedDateTime couponDeadline) {
        this.couponPolicy = couponPolicy;
        this.couponName = couponName;
        this.couponTarget = couponTarget;
        this.couponTargetId = couponTargetId;
        this.couponDeadline = couponDeadline;
        this.couponCreatedAt = ZonedDateTime.now();
        this.couponIsActive = true;
        this.couponUsedAt = null;

        if (couponPolicy == null) {
            throw new IllegalArgumentException("CouponPolicy must not be null");
        }
        if (couponName == null || couponName.isBlank()) {
            throw new IllegalArgumentException("CouponName must not be null or empty");
        }
        if (couponTarget == null || couponTarget.isBlank()) {
            throw new IllegalArgumentException("CouponTarget must not be null or empty");
        }
        if (couponDeadline == null) {
            throw new IllegalArgumentException("CouponDeadline must not be null");
        }

    }

    public static Coupon of(CreatCouponDTO couponDTO, CouponPolicy couponPolicy) {
        return Coupon.builder()
                .couponPolicy(couponPolicy)
                .couponName(couponDTO.couponName())
                .couponTarget(couponDTO.couponTarget())
                .couponTargetId(couponDTO.couponTargetId())
                .couponDeadline(couponDTO.couponDeadline())
                .build();
    }

    public void markAsUsed(ZonedDateTime usedAt) {
        if (this.couponUsedAt != null) {
            throw new IllegalStateException("Coupon has already been used");
        }
        this.couponUsedAt = usedAt;
    }

    public void deactivate() {
        this.couponIsActive = false;
    }
}
