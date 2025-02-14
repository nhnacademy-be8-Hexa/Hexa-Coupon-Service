package com.nhnacademy.coupon.log;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogCrashRequest {
    private String projectName;
    private String projectVersion;
    private String logVersion;
    private String body;
    private String logSource;
    private String logType;
    private String host;
    private String logLevel;

    private static final String DEFAULT_PROJECT_NAME = "nMWnKdBvAFvUW8XL";
    private static final String DEFAULT_PROJECT_VERSION = "1.0.0";
    private static final String DEFAULT_LOG_VERSION = "v2";
    private static final String DEFAULT_LOG_LEVEL= "ERROR";
    private static final String DEFAULT_LOG_SOURCE = "Hexa-Coupon";
    private static final String DEFAULT_LOG_TYPE = "log";
    private static final String DEFAULT_HOST = "Hexa";

    public LogCrashRequest(String body) {
        this.body = body;
        this.projectName = DEFAULT_PROJECT_NAME;
        this.projectVersion = DEFAULT_PROJECT_VERSION;
        this.logVersion = DEFAULT_LOG_VERSION;
        this.logSource = DEFAULT_LOG_SOURCE;
        this.logType = DEFAULT_LOG_TYPE;
        this.host = DEFAULT_HOST;
        this.logLevel = DEFAULT_LOG_LEVEL;
    }
}