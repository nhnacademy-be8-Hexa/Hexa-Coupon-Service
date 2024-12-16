package com.nhnacademy.coupon.entity;

import com.nhnacademy.coupon.entity.Dto.UpdateCouponPolicyDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Getter
public class CouponPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long couponPolicyId;

    @NotNull
    private String couponPolicyName;

    @NotNull
    private int minPurchaseAmount;

    @NotNull
    private String discountType;

    @NotNull
    private int discountValue;

    private int maxDiscountAmount;

    @NotNull
    private boolean isDeleted = false;

    private String eventType;

    @NotNull
    private ZonedDateTime createdAt;

    public CouponPolicy() {
        this.isDeleted = false; // 기본값을 'false'로 설정
    }

    @Builder
    public CouponPolicy(String couponPolicyName, int minPurchaseAmount, String discountType, int discountValue,
                        int maxDiscountAmount, String eventType, ZonedDateTime createdAt) {
        this.couponPolicyName = couponPolicyName;
        this.minPurchaseAmount = minPurchaseAmount;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscountAmount = maxDiscountAmount;
        this.isDeleted = false;  // 비활성화 상태는 'false'로 두고, 비활성화는 별도로 처리
        this.eventType = eventType;
        this.createdAt = createdAt != null ? createdAt : ZonedDateTime.now();  // 기존 생성일 유지
    }

    // 새로운 정책을 생성하는 메서드
    public static CouponPolicy of(UpdateCouponPolicyDTO updatedPolicyDTO) {
        return CouponPolicy.builder()
                .couponPolicyName(updatedPolicyDTO.couponPolicyName())  // 새로운 정책명
                .minPurchaseAmount(updatedPolicyDTO.minPurchaseAmount()) // 새로운 최소 구매 금액
                .discountType(updatedPolicyDTO.discountType())           // 새로운 할인 타입
                .discountValue(updatedPolicyDTO.discountValue())         // 새로운 할인 값
                .maxDiscountAmount(updatedPolicyDTO.maxDiscountAmount()) // 새로운 최대 할인 금액
                .eventType(updatedPolicyDTO.eventType())                 // 새로운 이벤트 타입
                .createdAt(ZonedDateTime.now())                           // 새로운 생성 시간
                .build();
    }

    // 기존 정책을 비활성화하는 메서드
    public CouponPolicy markAsDeleted() {
        this.isDeleted = true;  // 'isDeleted'를 true로 변경하여 비활성화 처리
        return this;  // 변경된 객체를 반환
    }

}
