package com.coj.codesandbox.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "api-auth")
public class ApiAuthProperties {

    private String accessKey;

    private String secretKey;

    private long timestampExpireSeconds = 300;

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getTimestampExpireSeconds() {
        return timestampExpireSeconds;
    }

    public void setTimestampExpireSeconds(long timestampExpireSeconds) {
        this.timestampExpireSeconds = timestampExpireSeconds;
    }
}
