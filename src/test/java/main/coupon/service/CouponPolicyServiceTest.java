package main.coupon.service;

import main.coupon.entity.CouponPolicy;
import main.coupon.repository.CouponPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CouponPolicyServiceTest {

    @MockBean
    private CouponPolicyRepository couponPolicyRepository;

    @Autowired
    private CouponPolicyService couponPolicyService;

    private CouponPolicy couponPolicy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        couponPolicy = new CouponPolicy(
                1L, "Discount Policy", 20000, "percent", 20, 10000, false,
                "welcome", ZonedDateTime.now()
        );
    }

    @Test
    public void testCreatePolicy() {
        CouponPolicy policyToCreate = new CouponPolicy();
        policyToCreate.setCoupon_policy_name("New Year Discount");
        policyToCreate.setMin_purchase_amount(30000);
        policyToCreate.setDiscount_type("percent");
        policyToCreate.setDiscount_value(15);
        policyToCreate.setMax_discount_amount(5000);
        policyToCreate.setEvent_type("new_year");
        policyToCreate.setCreated_at(ZonedDateTime.now());

        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(policyToCreate);

        CouponPolicy createdPolicy = couponPolicyService.createPolicy(policyToCreate);

        assertNotNull(createdPolicy);
        assertEquals("New Year Discount", createdPolicy.getCoupon_policy_name());
        assertEquals(30000, createdPolicy.getMin_purchase_amount());
    }

    @Test
    @Transactional
    public void testUpdatePolicy() {

        CouponPolicy updatedPolicy = new CouponPolicy();
        updatedPolicy.setCoupon_policy_name("Updated Policy");
        updatedPolicy.setMin_purchase_amount(25000);
        updatedPolicy.setDiscount_type("percent");
        updatedPolicy.setDiscount_value(10);
        updatedPolicy.setMax_discount_amount(3000);
        updatedPolicy.setEvent_type("holiday");
        updatedPolicy.setCreated_at(ZonedDateTime.now());

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(updatedPolicy);

        CouponPolicy result = couponPolicyService.updatePolicy(1L, updatedPolicy);

        assertNotNull(result);
        assertEquals("Updated Policy", result.getCoupon_policy_name());
        assertEquals(25000, result.getMin_purchase_amount());

        // save 메소드가 2번 호출되었는지 확인 (기존 정책 비활성화, 새로운 정책 저장)
        verify(couponPolicyRepository, times(2)).save(any(CouponPolicy.class));

        // 추가 검증 (첫 번째 save는 비활성화 처리, 두 번째 save는 새로운 정책 저장)
        ArgumentCaptor<CouponPolicy> captor = ArgumentCaptor.forClass(CouponPolicy.class);
        verify(couponPolicyRepository, times(2)).save(captor.capture());

        // 첫 번째 save는 기존 정책이 비활성화 된 상태여야 함
        CouponPolicy firstSave = captor.getAllValues().get(0);
        assertTrue(firstSave.is_deleted(), "First save should have set is_deleted to true");

        // 두 번째 save는 새로운 정책이 저장되어야 함
        CouponPolicy secondSave = captor.getAllValues().get(1);
        assertEquals("Updated Policy", secondSave.getCoupon_policy_name(), "Second save should have the updated policy name");
    }

    @Test
    public void testDeletePolicy() {

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));
        when(couponPolicyRepository.save(any(CouponPolicy.class))).thenReturn(couponPolicy);

        couponPolicyService.deletePolicy(1L);

        assertTrue(couponPolicy.is_deleted());
        verify(couponPolicyRepository, times(1)).save(any(CouponPolicy.class));
    }

    @Test
    public void testGetAllPolicies() {

        CouponPolicy policy1 = new CouponPolicy(1L, "Policy 1", 20000, "amount", 5000, 5000, false, "welcome", ZonedDateTime.now());
        CouponPolicy policy2 = new CouponPolicy(2L, "Policy 2", 30000, "percent", 10, 10000, false, "birthday", ZonedDateTime.now());

        when(couponPolicyRepository.findAll()).thenReturn(List.of(policy1, policy2));

        List<CouponPolicy> policies = couponPolicyService.getAllPolicies();

        assertEquals(2, policies.size());
        assertFalse(policies.get(0).is_deleted());
        assertFalse(policies.get(1).is_deleted());
    }

    @Test
    public void testGetPolicyById() {

        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.of(couponPolicy));

        CouponPolicy result = couponPolicyService.getPolicyById(1L);

        assertNotNull(result);
        assertEquals("Discount Policy", result.getCoupon_policy_name());
    }

    @Test
    public void testGetPolicyByIdNotFound() {
        when(couponPolicyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> couponPolicyService.getPolicyById(1L));
    }
}
