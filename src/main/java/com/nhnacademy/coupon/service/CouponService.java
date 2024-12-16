package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import com.nhnacademy.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 생성
    public Coupon createCoupon(CreatCouponDTO creatCouponDTO) {

        // 쿠폰 정책 가져오기
        CouponPolicy policy = couponPolicyRepository.findById(creatCouponDTO.couponPolicyId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid policy ID"));

        // 쿠폰 생성
        Coupon coupon = Coupon.of(creatCouponDTO, policy);

        // 유효한 만료일 체크
        if (creatCouponDTO.couponDeadline().isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Coupon deadline must be in the future");
        }

        return couponRepository.save(coupon);
    }

    // 쿠폰 조회 (쿠폰 ID로 조회)
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
    }

    // 쿠폰 ID 리스트로 쿠폰 조회
    public List<Coupon> getCouponsByIds(List<Long> couponIds) {
        // 널 값 체크
        if (couponIds == null || couponIds.isEmpty()) {
            throw new IllegalArgumentException("Coupon ID list cannot be null or empty");
        }
        // 쿠폰 조회
        return couponRepository.findAllById(couponIds);
    }

    // 모든 쿠폰 조회
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }

    // 쿠폰 사용 (사용된 날짜 기록)
    @Transactional
    public Coupon useCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        // 쿠폰 사용 처리
        coupon.markAsUsed(ZonedDateTime.now());

        return couponRepository.save(coupon);
    }

    // 쿠폰 삭제 (비활성화로 처리)
    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        // 쿠폰 비활성화 처리
        coupon.deactivate();

        couponRepository.save(coupon);
    }

}
