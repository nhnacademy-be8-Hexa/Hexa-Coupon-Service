package com.nhnacademy.coupon.repository;

import com.nhnacademy.coupon.entity.Coupon;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByCouponIsActive(@NotNull boolean couponIsActive);

    List<Coupon> findByCouponIdInAndCouponIsActive(List<Long> couponIds, @NotNull boolean couponIsActive);

}
