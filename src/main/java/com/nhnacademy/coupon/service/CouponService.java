package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Dto.CreatCouponDTO;
import com.nhnacademy.coupon.exception.CouponNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponRequestException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import com.nhnacademy.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Validated // 검증 활성화
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 객체 하나를 여러 개 생성
    public List<Coupon> createCoupon(@Valid CreatCouponDTO couponDTO, int count) {
        if (count <= 0) {
            throw new InvalidCouponRequestException("쿠폰 개수는 1개 이상이어야 합니다.");
        }

        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponDTO.couponPolicyId())
                .orElseThrow(() -> new InvalidCouponRequestException("Invalid coupon policy ID"));

        // @Future 조건 수동 체크
        if (couponDTO.couponDeadline().isBefore(ZonedDateTime.now())) {
            throw new InvalidCouponRequestException("쿠폰 마감일은 현재 시점 이후여야 합니다.");
        }

        List<Coupon> coupons = new ArrayList<>();

        // 요청된 개수만큼 쿠폰을 복제하여 생성
        for (int i = 0; i < count; i++) {
            Coupon coupon = Coupon.of(couponDTO, couponPolicy);
            coupons.add(coupon);
        }

        // 생성된 쿠폰들을 저장
        return couponRepository.saveAll(coupons);
    }

    // 쿠폰 조회 (쿠폰 ID로 조회)
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경
    }

    // 쿠폰 ID 리스트로 쿠폰 조회
    public List<Coupon> getCouponsByIds(List<Long> couponIds) {
        // 널 값 체크
        if (couponIds == null || couponIds.isEmpty()) {
            throw new InvalidCouponRequestException("Coupon ID list cannot be null or empty");
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
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경

        // 쿠폰 사용 처리
        coupon.markAsUsed(ZonedDateTime.now());

        return couponRepository.save(coupon);
    }

    // 쿠폰 삭제 (비활성화로 처리)
    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경

        // 쿠폰 비활성화 처리
        coupon.deactivate();

        couponRepository.save(coupon);
    }

    // 전체 쿠폰 중 사용된 쿠폰
    public List<Coupon> getUsedCoupons() {
        return couponRepository.findUsedCoupons();
    }

    // 쿠폰 ID 리스트 중 사용된 쿠폰
    public List<Coupon> getUsedCouponsByIds(List<Long> couponIds) {
        if (couponIds == null || couponIds.isEmpty()) {
            throw new InvalidCouponRequestException("Coupon ID list cannot be null or empty");
        }
        return couponRepository.findUsedCouponsByIds(couponIds);
    }

}
