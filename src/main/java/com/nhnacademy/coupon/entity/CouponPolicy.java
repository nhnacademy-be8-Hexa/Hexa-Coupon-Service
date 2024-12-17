package com.nhnacademy.coupon.entity;

import com.nhnacademy.coupon.entity.Dto.UpdateCouponPolicyDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long couponPolicyId;

    @NotBlank
    @Length(max = 50)
    private String couponPolicyName;

    @NotNull
    @PositiveOrZero
    private int minPurchaseAmount;

    @NotBlank
    @Length(max = 20)
    private String discountType;

    @NotNull
    @PositiveOrZero
    private int discountValue;

    @PositiveOrZero
    private int maxDiscountAmount;

    @NotNull
    private boolean isDeleted;

    @Length(max = 20)
    private String eventType;

    @NotNull
    private ZonedDateTime createdAt;

    // 새로운 정책을 생성하는 메서드
    public static CouponPolicy of(
             String couponPolicyName,
             int minPurchaseAmount,
             String discountType,
             int discountValue,
             int maxDiscountAmount
    ) {
        return CouponPolicy.builder()
                .couponPolicyName(couponPolicyName)  // 새로운 정책명
                .minPurchaseAmount(minPurchaseAmount) // 새로운 최소 구매 금액
                .discountType(discountType)           // 새로운 할인 타입
                .discountValue(discountValue)         // 새로운 할인 값
                .maxDiscountAmount(maxDiscountAmount) // 새로운 최대 할인 금액
                .isDeleted(false)
                .eventType(null)                        // 새로운 이벤트 타입
                .createdAt(ZonedDateTime.now())        // 새로운 생성 시간
                .build();
    }

    // 기존 정책을 비활성화하는 메서드
    public CouponPolicy markAsDeleted() {
        this.isDeleted = true;  // 'isDeleted'를 true로 변경하여 비활성화 처리
        return this;  // 변경된 객체를 반환
    }

}
