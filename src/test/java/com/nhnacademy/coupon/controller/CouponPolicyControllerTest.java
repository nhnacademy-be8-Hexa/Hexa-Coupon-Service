package com.nhnacademy.coupon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.coupon.entity.CouponPolicy;
import com.nhnacademy.coupon.entity.dto.CouponPolicyRequestDTO;
import com.nhnacademy.coupon.service.CouponPolicyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

@WebMvcTest(CouponPolicyController.class)
@AutoConfigureRestDocs
@ActiveProfiles("test")
class CouponPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponPolicyService couponPolicyService;  // @MockBean을 사용해 Spring Context에 빈을 주입

    @Autowired
    private ObjectMapper objectMapper;

    // 1. 모든 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicies() throws Exception {
        List<CouponPolicy> policies = Arrays.asList(
                CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                        .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(null).build(),
                CouponPolicy.builder().couponPolicyId(2L).couponPolicyName("Policy2").minPurchaseAmount(2000)
                        .discountType("discount").discountValue(15).maxDiscountAmount(200).isDeleted(false).eventType("birthday").createdAt(null).build()
        );

        when(couponPolicyService.getAllPolicies(false)).thenReturn(policies);

        mockMvc.perform(get("/api/auth/policies")
                        .param("deleted", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].couponPolicyName").value("Policy1"))
                .andDo(document("get-policies",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("deleted").description("삭제 여부(true or false)")
                        ),
                        responseFields(
                                fieldWithPath("[].couponPolicyId").description("쿠폰 정책 ID"),
                                fieldWithPath("[].couponPolicyName").description("쿠폰 정책 이름"),
                                fieldWithPath("[].minPurchaseAmount").description("최소 구매 금액"),
                                fieldWithPath("[].discountType").description("할인 유형"),
                                fieldWithPath("[].discountValue").description("할인 값"),
                                fieldWithPath("[].maxDiscountAmount").description("최대 할인 금액"),
                                fieldWithPath("[].eventType").description("이벤트 타입"),
                                fieldWithPath("[].createdAt").description("생성일"),
                                fieldWithPath("[].deleted").description("삭제 여부")
                        )
                ));
    }

    // 2. 특정 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicyById() throws Exception {
        CouponPolicy policy = CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(ZonedDateTime.now().plusDays(30)).build();

        when(couponPolicyService.getPolicyById(1L)).thenReturn(policy);

        mockMvc.perform(get("/api/auth/policies/{couponPolicyId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"))
                .andDo(document("get-policy-by-id",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponPolicyId").description("쿠폰정책 Id")
                        ),
                        responseFields(
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("생성 일자").type(JsonFieldType.STRING),
                                fieldWithPath("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN)
                        )
                ));
    }

    // 3. 특정 이벤트 타입의 쿠폰 정책 조회 테스트
    @Test
    void testGetPolicyByEventType() throws Exception {
        CouponPolicy policy = CouponPolicy.builder().couponPolicyId(1L).couponPolicyName("Policy1").minPurchaseAmount(1000)
                .discountType("discount").discountValue(10).maxDiscountAmount(100).isDeleted(false).eventType("welcome").createdAt(ZonedDateTime.now().plusDays(30)).build();

        when(couponPolicyService.getPolicyByEventType("welcome")).thenReturn(policy);

        mockMvc.perform(get("/api/auth/policies/{eventType}/eventType", "welcome"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"))
                .andDo(document("get-policy-by-event-type",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("eventType").description("이벤트 타입")
                        ),
                        responseFields(
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("생성 일자").type(JsonFieldType.STRING),
                                fieldWithPath("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN)
                        )
                ));
    }

    // 4. 쿠폰 정책 생성 테스트
    @Test
    void testCreatePolicy() throws Exception {
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO("Policy1", 1000, "discount", 10, 100, "welcome");

        CouponPolicy createdPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Policy1")
                .minPurchaseAmount(1000)
                .discountType("discount")
                .discountValue(10)
                .maxDiscountAmount(100)
                .isDeleted(false)
                .eventType("welcome")
                .createdAt(ZonedDateTime.now().plusDays(30))
                .build();

        when(couponPolicyService.createPolicy(requestDTO)).thenReturn(createdPolicy);

        mockMvc.perform(post("/api/auth/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Policy1"))
                .andDo(document("create-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("생성 일자").type(JsonFieldType.STRING),
                                fieldWithPath("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN)
                        )
                ));
    }

    // 5. 쿠폰 정책 수정 테스트
    @Test
    void testUpdatePolicy() throws Exception {
        CouponPolicyRequestDTO requestDTO = new CouponPolicyRequestDTO( "Updated Policy", 2000, "discount", 20, 200, "birthday");
        CouponPolicy updatedPolicy = CouponPolicy.builder()
                .couponPolicyId(1L)
                .couponPolicyName("Updated Policy")
                .minPurchaseAmount(2000)
                .discountType("discount")
                .discountValue(20)
                .maxDiscountAmount(200)
                .isDeleted(false)
                .eventType("birthday")
                .createdAt(ZonedDateTime.now().plusDays(30))
                .build();

        when(couponPolicyService.updatePolicy(1L, requestDTO)).thenReturn(updatedPolicy);

        mockMvc.perform(patch("/api/auth/policies/{couponPolicyId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.couponPolicyName").value("Updated Policy"))
                .andDo(document("update-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponPolicyId").description("쿠폰정책 Id")
                        ),
                        requestFields(
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING)
                        ),
                        responseFields(
                                fieldWithPath("couponPolicyId").description("쿠폰 정책 ID").type(JsonFieldType.NUMBER),
                                fieldWithPath("couponPolicyName").description("쿠폰 정책 이름").type(JsonFieldType.STRING),
                                fieldWithPath("minPurchaseAmount").description("최소 구매 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("discountType").description("할인 타입").type(JsonFieldType.STRING),
                                fieldWithPath("discountValue").description("할인 값").type(JsonFieldType.NUMBER),
                                fieldWithPath("maxDiscountAmount").description("최대 할인 금액").type(JsonFieldType.NUMBER),
                                fieldWithPath("deleted").description("삭제 여부").type(JsonFieldType.BOOLEAN),
                                fieldWithPath("eventType").description("이벤트 타입").type(JsonFieldType.STRING),
                                fieldWithPath("createdAt").description("생성 일자").type(JsonFieldType.STRING)
                        )
                ));
    }

    // 6. 쿠폰 정책 삭제 테스트
    @Test
    void testDeletePolicy() throws Exception {
        doNothing().when(couponPolicyService).deletePolicy(1L);

        mockMvc.perform(delete("/api/auth/policies/{couponPolicyId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Coupon policy deleted successfully"))
                .andDo(document("delete-policy",
                        preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("couponPolicyId").description("쿠폰정책 Id")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메시지")
                        )
                ));
    }
}
