package com.nhnacademy.coupon.service;

import com.nhnacademy.coupon.entity.Dto.CouponRequestDTO;
import com.nhnacademy.coupon.exception.CouponNotFoundException;
import com.nhnacademy.coupon.exception.InvalidCouponRequestException;
import lombok.RequiredArgsConstructor;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.repository.CouponPolicyRepository;
import com.nhnacademy.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    // 쿠폰 객체 하나를 여러 개 생성
    @Transactional
    public List<Coupon> createCoupon(CouponRequestDTO couponDTO, int count) {
        if (count <= 0) {
            throw new InvalidCouponRequestException("쿠폰 개수는 1개 이상이어야 합니다.");
        }

        CouponPolicy couponPolicy = couponPolicyRepository.findById(couponDTO.couponPolicyId())
                .orElseThrow(() -> new InvalidCouponRequestException("Invalid coupon policy ID"));

//        // @Future 조건 수동 체크
//        if (couponDTO.couponDeadline().isBefore(ZonedDateTime.now())) {
//            throw new InvalidCouponRequestException("쿠폰 마감일은 현재 시점 이후여야 합니다.");
//        }

        List<Coupon> coupons = new ArrayList<>();

        // 요청된 개수만큼 쿠폰을 복제하여 생성
        for (int i = 0; i < count; i++) {
            Coupon coupon = Coupon.of(
                    couponPolicy,
                    couponDTO.couponName(),
                    couponDTO.couponTarget(),
                    couponDTO.couponTargetId(),
                    couponDTO.couponDeadline()
            );
            coupons.add(coupon);
        }

        // 생성된 쿠폰들을 저장
        return couponRepository.saveAll(coupons);
    }

    // 쿠폰 사용 (사용된 날짜 기록)
    @Transactional
    public Coupon useCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경

        // 쿠폰 사용 처리
        coupon.markAsUsed();

        return coupon;
    }

    // 쿠폰 삭제 (비활성화로 처리)
    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경

        // 쿠폰 비활성화 처리
        coupon.deActivate();
    }

    // 쿠폰 조회 (쿠폰 ID로 조회)
    public Coupon getCouponById(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponNotFoundException(couponId)); // CouponNotFoundException으로 변경
    }

    // 특정 유저가 가진 쿠폰 ID 리스트로 쿠폰 조회
    public List<Coupon> getCouponsByIdsAndActive(List<Long> couponIds, Boolean isActive) {
        // 널 값 체크
        if (couponIds == null) {
            throw new InvalidCouponRequestException("Coupon ID list cannot be null");
        }
        if(couponIds.isEmpty()) {
            return new ArrayList<>();
        }
        // 쿠폰 조회
        return couponRepository.findByCouponIdInAndCouponIsActive(couponIds, isActive);
    }

    // 전체 쿠폰 조회 by isActive
    public List<Coupon> getCouponsByActive(Boolean active) {
        return couponRepository.findByCouponIsActive(active);
    }

}
