package com.nhnacademy.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.entity.Coupon;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.dto.CouponRequestDTO;
import com.nhnacademy.coupon.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;

@WebMvcTest(CouponController.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper objectMapper;

    private CouponPolicy couponPolicy;

    @BeforeEach
    void setUp() {
        couponPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Holiday Discount")
                .minPurchaseAmount(1000)
                .discountType("PERCENTAGE")
                .discountValue(10)
                .maxDiscountAmount(500)
                .isDeleted(false)
                .eventType("birthday")
                .createdAt(ZonedDateTime.now())
                .build();
    }
    // 1. 모든 쿠폰 조회 테스트
    @Test
    void testGetCouponsByActive() throws Exception {

        List<Coupon> coupons = Arrays.asList(
                Coupon.builder()
                        .couponId(1L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build(),
                Coupon.builder()
                        .couponId(2L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon2")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build()
        );

        when(couponService.getCouponsByActive(true)).thenReturn(coupons);

        mockMvc.perform(get("/api/coupons")
                        .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponName").value("New Year Coupon"))
                .andDo(document("get-coupons-by-active",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("active").description("횔성화 여부를 나타내는 값 (true 또는 false)")
                        ),
                        responseFields(
                                fieldWithPath("[].couponId").description("쿠폰 ID"),
                                fieldWithPath("[].couponName").description("쿠폰 이름"),
                                fieldWithPath("[].couponTarget").description("쿠폰 대상"),
                                fieldWithPath("[].couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("[].couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("[].couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("[].couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("[].couponUsedAt").description("쿠폰 사용일").optional(),
                                fieldWithPath("[].couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("[].couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("[].couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("[].couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("[].couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("[].couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("[].couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("[].couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("[].couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("[].couponPolicy.deleted").description("쿠폰 정책 삭제 여부")
                        )
                ));

    }

    // 2. 쿠폰 생성 테스트
    @Test
    void testCreateCoupons() throws Exception {
        CouponRequestDTO requestDTO = new CouponRequestDTO(1L, "Coupon1", "user", 100L, null);

        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.createCoupon(requestDTO, 1)).thenReturn(Arrays.asList(coupon));

        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .param("count", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].couponName").value("Coupon1"))
                .andDo(document("create-coupons",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("count").description("생성 할 쿠폰 개수")
                        ),
                        requestFields(
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("couponName").description("쿠폰 이름"),
                                fieldWithPath("couponTarget").description("쿠폰 대상"),
                                fieldWithPath("couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("couponDeadline").description("쿠폰 유효 기간")
                                ),
                        responseFields(
                                fieldWithPath("[].couponId").description("쿠폰 ID"),
                                fieldWithPath("[].couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("[].couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("[].couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("[].couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("[].couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("[].couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("[].couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("[].couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("[].couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("[].couponPolicy.deleted").description("쿠폰 정책 삭제 여부"),
                                fieldWithPath("[].couponName").description("쿠폰 이름"),
                                fieldWithPath("[].couponTarget").description("쿠폰 대상"),
                                fieldWithPath("[].couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("[].couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("[].couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("[].couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("[].couponUsedAt").description("쿠폰 사용일").optional()
                        )
                ));
    }

    // 3. 쿠폰 사용 테스트
    @Test
    void testUseCoupon() throws Exception {
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.useCoupon(1L)).thenReturn(coupon);

        mockMvc.perform(post("/api/coupons/{couponId}/use", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("Coupon1"))
                .andDo(document("use-coupon",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponId").description("쿠폰 Id")
                        ),
                        responseFields(
                                fieldWithPath("couponId").description("쿠폰 ID"),
                                fieldWithPath("couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("couponPolicy.deleted").description("쿠폰 정책 삭제 여부"),
                                fieldWithPath("couponName").description("쿠폰 이름"),
                                fieldWithPath("couponTarget").description("쿠폰 대상"),
                                fieldWithPath("couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("couponUsedAt").description("쿠폰 사용일").optional()
                        )
                ));
    }

    // 4. 쿠폰 비활성화 테스트
    @Test
    void testDeactivateCoupon() throws Exception {
        doNothing().when(couponService).deactivateCoupon(1L);

        mockMvc.perform(post("/api/coupons/{couponId}/deactivate", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coupon deactivated successfully"))
                .andDo(document("deactivate-coupon",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponId").description("쿠폰 Id")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }

    @Test
    void getCouponById() throws Exception {
        Coupon coupon = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon1")
                .couponTarget("user")
                .couponTargetId(100L)
                .couponDeadline(ZonedDateTime.now().plusDays(30))
                .couponCreatedAt(ZonedDateTime.now())
                .couponIsActive(true)
                .couponUsedAt(null)
                .build();

        when(couponService.getCouponById(1L)).thenReturn(coupon);

        mockMvc.perform(get("/api/coupons/{couponId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponName").value("Coupon1"))
                .andDo(document("get-coupon-by-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponId").description("쿠폰 Id")
                        ),
                        responseFields(
                                fieldWithPath("couponId").description("쿠폰 ID"),
                                fieldWithPath("couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("couponPolicy.deleted").description("쿠폰 정책 삭제 여부"),
                                fieldWithPath("couponName").description("쿠폰 이름"),
                                fieldWithPath("couponTarget").description("쿠폰 대상"),
                                fieldWithPath("couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("couponUsedAt").description("쿠폰 사용일").optional()
                        )
                ));
    }

    @Test
    void getCouponsByActive_couponIdsProvided_returnsFilteredCoupons() throws Exception {
        // given
        List<Long> couponIds = Arrays.asList(1L, 2L, 3L);
        Coupon coupon1 = Coupon.builder()
                .couponId(1L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon 1")
                .couponTarget("USER")
                .couponTargetId(100L)
                .couponIsActive(true)
                .couponCreatedAt(java.time.ZonedDateTime.now())
                .build();
        Coupon coupon2 = Coupon.builder()
                .couponId(2L)
                .couponPolicy(couponPolicy)
                .couponName("Coupon 2")
                .couponTarget("USER")
                .couponTargetId(101L)
                .couponIsActive(true)
                .couponCreatedAt(java.time.ZonedDateTime.now())
                .build();

        List<Coupon> coupons = Arrays.asList(coupon1, coupon2);

        when(couponService.getCouponsByIdsAndActive(couponIds, true)).thenReturn(coupons);

        // when & then
        mockMvc.perform(get("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1, 2, 3]")
                        .param("active", "true")
                        .param("couponIds", "1, 2, 3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].couponId").value(1L))
                .andExpect(jsonPath("$[1].couponId").value(2L))
                .andExpect(jsonPath("$[0].couponName").value("Coupon 1"))
                .andExpect(jsonPath("$[1].couponName").value("Coupon 2"))
                .andDo(document("get-coupons-by-ids-and-active",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("active").description("활성화 여부를 나타내는 값 (true 또는 false)"),
                                parameterWithName("couponIds").description("쿠폰 아이디 리스트")
                        ),
                        requestFields(
                                fieldWithPath("[]").description("쿠폰 ID 리스트")
                        ),
                        responseFields(
                                fieldWithPath("[].couponId").description("쿠폰 ID"),
                                fieldWithPath("[].couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("[].couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("[].couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("[].couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("[].couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("[].couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("[].couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("[].couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("[].couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("[].couponPolicy.deleted").description("쿠폰 정책 삭제 여부"),
                                fieldWithPath("[].couponName").description("쿠폰 이름"),
                                fieldWithPath("[].couponTarget").description("쿠폰 대상"),
                                fieldWithPath("[].couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("[].couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("[].couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("[].couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("[].couponUsedAt").description("쿠폰 사용일").optional()
                        )
                ));

        // verify
        verify(couponService, times(1)).getCouponsByIdsAndActive(couponIds, true);
    }

    @Test
    void createCoupons_countIsNegative_returnsBadRequest() throws Exception {
        // given
        CouponRequestDTO couponRequestDTO = new CouponRequestDTO(1L, "Coupon 1", "USER", 100L, ZonedDateTime.now().plusDays(1));

        // when & then
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"couponPolicyId\": 1, \"couponName\": \"Coupon 1\", \"couponTarget\": \"USER\", \"couponTargetId\": 100, \"couponDeadline\": \"2026-12-19T00:00:00Z\"}")
                        .param("count", "-1"))  // count = -1
                .andExpect(status().isBadRequest()) // 상태 코드 400을 확인
                .andExpect(content().json("[]"))
                .andDo(document("create-coupons-count-negative",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(  // 요청 본문 필드 문서화
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("couponName").description("쿠폰 이름"),
                                fieldWithPath("couponTarget").description("쿠폰 대상"),
                                fieldWithPath("couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("couponDeadline").description("쿠폰 유효 기간")
                        ),
                        responseFields(  // 응답 필드 문서화
                                fieldWithPath("[]").description("응답 내용은 빈 배열임")
                        )
                ));


        // verify
        verify(couponService, times(0)).createCoupon(any(), anyInt());  // couponService의 메서드가 호출되지 않음
    }

    @Test
    void testGetCouponsByCouponName() throws Exception {
        // given
        String couponName = "New Year Coupon";

        List<Coupon> coupons = Arrays.asList(
                Coupon.builder()
                        .couponId(1L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build(),
                Coupon.builder()
                        .couponId(2L)
                        .couponPolicy(couponPolicy)
                        .couponName("New Year Coupon")
                        .couponTarget("USER")
                        .couponTargetId(123L)
                        .couponDeadline(ZonedDateTime.now().plusDays(30))
                        .couponCreatedAt(ZonedDateTime.now())
                        .couponIsActive(true)
                        .couponUsedAt(null)
                        .build()
        );

        // when
        when(couponService.getCouponsByCouponName(couponName)).thenReturn(coupons);

        // then
        mockMvc.perform(get("/api/coupons/{couponName}/name", couponName))
                .andExpect(status().isOk())  // 상태 코드 200 OK
                .andExpect(jsonPath("$.length()").value(2))  // 응답이 2개의 쿠폰인지 확인
                .andExpect(jsonPath("$[0].couponName").value("New Year Coupon"))  // 첫 번째 쿠폰 이름 확인
                .andExpect(jsonPath("$[1].couponName").value("New Year Coupon"))  // 두 번째 쿠폰 이름 확인
                .andDo(document("get-coupons-by-name",  // 문서화
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(  // path 파라미터 문서화
                                parameterWithName("couponName").description("조회할 쿠폰 이름")
                        ),
                        responseFields(  // 응답 필드 문서화
                                fieldWithPath("[].couponId").description("쿠폰 ID"),
                                fieldWithPath("[].couponName").description("쿠폰 이름"),
                                fieldWithPath("[].couponTarget").description("쿠폰 대상"),
                                fieldWithPath("[].couponTargetId").description("쿠폰 대상 ID"),
                                fieldWithPath("[].couponDeadline").description("쿠폰 유효 기간"),
                                fieldWithPath("[].couponCreatedAt").description("쿠폰 생성일"),
                                fieldWithPath("[].couponIsActive").description("쿠폰 활성화 여부"),
                                fieldWithPath("[].couponUsedAt").description("쿠폰 사용일").optional(),
                                fieldWithPath("[].couponPolicy").description("쿠폰 정책"),
                                fieldWithPath("[].couponPolicy.couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("[].couponPolicy.couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("[].couponPolicy.minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("[].couponPolicy.discountType").description("할인 유형"),
                                fieldWithPath("[].couponPolicy.discountValue").description("할인 값"),
                                fieldWithPath("[].couponPolicy.maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("[].couponPolicy.eventType").description("이벤트 유형").optional(),
                                fieldWithPath("[].couponPolicy.createdAt").description("쿠폰 정책 생성일"),
                                fieldWithPath("[].couponPolicy.deleted").description("쿠폰 정책 삭제 여부")
                        )
                ));
    }
}
