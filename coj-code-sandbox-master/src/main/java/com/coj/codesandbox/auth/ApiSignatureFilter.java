package com.coj.codesandbox.auth;

import com.coj.codesandbox.utils.ApiSignatureUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

@Component
public class ApiSignatureFilter implements Filter {

    private static final String HEADER_ACCESS_KEY = "X-Access-Key";
    private static final String HEADER_TIMESTAMP  = "X-Timestamp";
    private static final String HEADER_NONCE      = "X-Nonce";
    private static final String HEADER_SIGNATURE  = "X-Signature";

    @Resource
    private ApiAuthProperties apiAuthProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // /health 等非业务接口直接放行
        if (!"/executeCode".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        // 用 CachedBodyRequestWrapper 包装，使 Body 可被多次读取
        CachedBodyRequestWrapper wrappedRequest = new CachedBodyRequestWrapper(request);

        String accessKey = wrappedRequest.getHeader(HEADER_ACCESS_KEY);
        String timestamp  = wrappedRequest.getHeader(HEADER_TIMESTAMP);
        String nonce      = wrappedRequest.getHeader(HEADER_NONCE);
        String signature  = wrappedRequest.getHeader(HEADER_SIGNATURE);

        // 1. 必填字段校验
        if (isBlank(accessKey) || isBlank(timestamp) || isBlank(nonce) || isBlank(signature)) {
            writeError(response, 401, "Missing required signature headers");
            return;
        }

        // 2. accessKey 校验
        if (!apiAuthProperties.getAccessKey().equals(accessKey)) {
            writeError(response, 401, "Invalid access key");
            return;
        }

        // 3. 时间戳防重放校验
        long requestTs;
        try {
            requestTs = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            writeError(response, 401, "Invalid timestamp format");
            return;
        }
        long currentTs = System.currentTimeMillis() / 1000L;
        if (Math.abs(currentTs - requestTs) > apiAuthProperties.getTimestampExpireSeconds()) {
            writeError(response, 401, "Request timestamp expired");
            return;
        }

        // 4. 直接取已缓存的请求体字节
        byte[] bodyBytes = wrappedRequest.getCachedBody();

        // 5. 计算请求体哈希
        String bodyHash = ApiSignatureUtil.sha256Hex(bodyBytes);

        // 6. 拼接签名字符串
        String signStr = accessKey + "\n" + timestamp + "\n" + nonce + "\n" + bodyHash;

        // 7. 计算期望签名
        String expectedSig = ApiSignatureUtil.hmacSha256Hex(signStr, apiAuthProperties.getSecretKey());

        // 8. 防时序攻击的常量时间比对
        if (!MessageDigest.isEqual(
                expectedSig.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                signature.getBytes(java.nio.charset.StandardCharsets.UTF_8))) {
            writeError(response, 401, "Signature verification failed");
            return;
        }

        // 验证通过，继续传递（传入包装后的 request，确保 Controller 仍可读取 Body）
        chain.doFilter(wrappedRequest, response);
    }

    private void writeError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.write("{\"error\":\"" + message + "\"}");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
