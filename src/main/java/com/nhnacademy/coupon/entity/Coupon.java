package com.nhnacademy.coupon.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @ManyToOne
    @NotNull
    private CouponPolicy couponPolicy;

    @NotBlank
    @Length(max = 50)
    private String couponName;

    @NotBlank
    @Length(max = 20)
    private String couponTarget;

    private Long couponTargetId;

    private ZonedDateTime couponDeadline;

    @NotNull
    private ZonedDateTime couponCreatedAt;

    @NotNull
    private boolean couponIsActive;

    private ZonedDateTime couponUsedAt;

    public static Coupon of(CouponPolicy couponPolicy,
                            String couponName,
                            String couponTarget,
                            Long couponTargetId,
                            ZonedDateTime couponDeadline) {
        return Coupon.builder()
                .couponPolicy(couponPolicy)
                .couponName(couponName)
                .couponTarget(couponTarget)
                .couponTargetId(couponTargetId)
                .couponDeadline(couponDeadline)
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();
    }

    public void markAsUsed() {
        if (!this.couponIsActive) {
            throw new IllegalStateException("Coupon has already been used");
        }
        this.couponIsActive = false;
        this.couponUsedAt = ZonedDateTime.now();
    }

    public void deActivate() {
        if (!this.couponIsActive) {
            throw new IllegalStateException("Coupon has already deleted");
        }
        this.couponIsActive = false;
    }

}
