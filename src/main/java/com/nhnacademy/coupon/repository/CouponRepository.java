package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.couponUsedAt IS NOT NULL")
    List<Coupon> findUsedCoupons();

    @Query("SELECT c FROM Coupon c WHERE c.couponId IN :ids AND c.couponUsedAt IS NOT NULL")
    List<Coupon> findUsedCouponsByIds(@Param("ids") List<Long> ids);

}
