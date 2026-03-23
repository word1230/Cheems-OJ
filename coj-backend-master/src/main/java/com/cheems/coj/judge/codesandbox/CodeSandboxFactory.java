package com.cheems.coj.judge.codesandbox;

import com.cheems.coj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.cheems.coj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.cheems.coj.judge.codesandbox.impl.ThirdPartyCodeSandbox;

/**
 * 代码沙箱工厂（根据字符串参数创建指定的代码沙箱实例）
 */
public class CodeSandboxFactory {

    /**
     * 创建代码沙箱实例
     *
     * @param type      沙箱类型
     * @param accessKey API 签名 accessKey
     * @param secretKey API 签名 secretKey
     * @return CodeSandbox 实例
     */
    public static CodeSandbox newInstance(String type, String accessKey, String secretKey) {
        switch (type) {
            case "remote":
                return new RemoteCodeSandbox(accessKey, secretKey);
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            case "example":
            default:
                return new ExampleCodeSandbox();
        }
    }
}
