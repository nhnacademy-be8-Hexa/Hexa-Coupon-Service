package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.CouponPolicy;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
    // 삭제 여부에 따른 리스트 조회
    List<CouponPolicy> findByIsDeleted(@NotNull boolean deleted);

    // 이벤트 타입에 따른 조회 welcome, birthday
    CouponPolicy findByEventType(@NotNull String eventType);
}
