package com.nhnacademy.coupon.log;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class LogCrashRequestTest {

    @Test
    void testLogCrashRequestConstructor() {
        // given
        String body = "Test crash log body";

        // when
        LogCrashRequest logCrashRequest = new LogCrashRequest(body);

        // then
        assertNotNull(logCrashRequest);
        assertEquals("nMWnKdBvAFvUW8XL", logCrashRequest.getProjectName());
        assertEquals("1.0.0", logCrashRequest.getProjectVersion());
        assertEquals("v2", logCrashRequest.getLogVersion());
        assertEquals("ERROR", logCrashRequest.getLogLevel());
        assertEquals("Hexa-Coupon", logCrashRequest.getLogSource());
        assertEquals("log", logCrashRequest.getLogType());
        assertEquals("Hexa", logCrashRequest.getHost());
        assertEquals(body, logCrashRequest.getBody());
    }

    @Test
    void testLogCrashRequestWithNullBody() {
        // given
        String body = null;

        // when
        LogCrashRequest logCrashRequest = new LogCrashRequest(body);

        // then
        assertNotNull(logCrashRequest);
        assertEquals("nMWnKdBvAFvUW8XL", logCrashRequest.getProjectName());
        assertEquals("1.0.0", logCrashRequest.getProjectVersion());
        assertEquals("v2", logCrashRequest.getLogVersion());
        assertEquals("ERROR", logCrashRequest.getLogLevel());
        assertEquals("Hexa-Coupon", logCrashRequest.getLogSource());
        assertEquals("log", logCrashRequest.getLogType());
        assertEquals("Hexa", logCrashRequest.getHost());
        assertNull(logCrashRequest.getBody()); // body는 null이어야 함
    }
  
}