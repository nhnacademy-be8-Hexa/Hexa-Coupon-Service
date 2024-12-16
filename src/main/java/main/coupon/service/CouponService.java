package main.coupon.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import main.coupon.entity.Coupon;
import main.coupon.entity.CouponPolicy;
import main.coupon.repository.CouponPolicyRepository;
import main.coupon.repository.CouponRepository;
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
    public Coupon createCoupon(Long couponPolicyId, String couponName, String couponTarget,
                               Long couponTargetId, ZonedDateTime couponDeadline) {

        // 쿠폰 정책 가져오기
        CouponPolicy policy = couponPolicyRepository.findById(couponPolicyId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid policy ID"));

        // 쿠폰 생성
        Coupon coupon = new Coupon();

        // 유효한 만료일 체크
        if (couponDeadline.isBefore(ZonedDateTime.now())) {
            throw new IllegalArgumentException("Coupon deadline must be in the future");
        }

        // 관리자가 입력한 값으로 쿠폰 설정
        coupon.setCouponPolicy(policy);  // 정책 설정
        coupon.setCoupon_name(couponName);  // 쿠폰 이름 설정
        coupon.setCoupon_target(couponTarget);  // 쿠폰 대상 설정
        coupon.setCoupon_target_id(couponTargetId); // null일 경우 0 설정 //couponTargetId != null ? couponTargetId : 0
        coupon.setCoupon_deadline(couponDeadline);  // 쿠폰 유효기간 설정

        coupon.setCoupon_created_at(ZonedDateTime.now());  // 생성 시간은 현재 시간
        coupon.setCoupon_is_active(true);  // 기본적으로 활성 상태
        coupon.setCoupon_used_at(null);  // 초기에는 사용되지 않음

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

        // 쿠폰이 이미 사용되었는지 확인
        if (coupon.getCoupon_used_at() != null) {
            throw new IllegalArgumentException("Coupon has already been used");
        }

        // 쿠폰 사용일 설정
        coupon.setCoupon_used_at(ZonedDateTime.now());

        // 쿠폰 업데이트
        return couponRepository.save(coupon);
    }

    // 쿠폰 삭제 (비활성화로 처리)
    @Transactional
    public void deactivateCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));

        coupon.setCoupon_is_active(false);
        couponRepository.save(coupon);
    }

}
