package com.nhnacademy.coupon.exception.credentials;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KeyManagerExceptionTest {

    @Test
    void testKeyManagerExceptionMessage() {
        // given
        String expectedMessage = "Key manager error occurred";

        // when
        KeyManagerException exception = new KeyManagerException(expectedMessage);

        // then
        assertNotNull(exception); // 객체가 null이 아님을 확인
        assertEquals(expectedMessage, exception.getMessage()); // 예외 메시지가 올바른지 확인
    }

    @Test
    void testKeyManagerExceptionWithoutMessage() {
        // when
        KeyManagerException exception = new KeyManagerException(null);

        // then
        assertNotNull(exception); // 객체가 null이 아님을 확인
        assertNull(exception.getMessage()); // 메시지가 null로 설정되었는지 확인
    }
}
