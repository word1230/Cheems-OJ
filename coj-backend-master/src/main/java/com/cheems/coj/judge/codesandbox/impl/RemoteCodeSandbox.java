package com.cheems.coj.judge.codesandbox.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.cheems.coj.common.ErrorCode;
import com.cheems.coj.exception.BusinessException;
import com.cheems.coj.judge.codesandbox.CodeSandbox;
import com.cheems.coj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cheems.coj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * 远程代码沙箱（携带 API 签名认证）
 */
public class RemoteCodeSandbox implements CodeSandbox {

    private static final String SANDBOX_URL = "http://sandbox:8090/executeCode";

    private static final String HEADER_ACCESS_KEY = "X-Access-Key";
    private static final String HEADER_TIMESTAMP  = "X-Timestamp";
    private static final String HEADER_NONCE      = "X-Nonce";
    private static final String HEADER_SIGNATURE  = "X-Signature";

    private final String accessKey;
    private final String secretKey;

    public RemoteCodeSandbox(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        byte[] bodyBytes = json.getBytes(StandardCharsets.UTF_8);

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonce = UUID.randomUUID().toString().replace("-", "");
        String bodyHash = sha256Hex(bodyBytes);
        String signStr = accessKey + "\n" + timestamp + "\n" + nonce + "\n" + bodyHash;
        String signature = hmacSha256Hex(signStr, secretKey);

        HttpResponse response = HttpRequest.post(SANDBOX_URL)
                .header(HEADER_ACCESS_KEY, accessKey)
                .header(HEADER_TIMESTAMP, timestamp)
                .header(HEADER_NONCE, nonce)
                .header(HEADER_SIGNATURE, signature)
                .body(json)
                .execute();

        String responseStr = response.body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR,
                    "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private static String hmacSha256Hex(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HmacSHA256 failed", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
