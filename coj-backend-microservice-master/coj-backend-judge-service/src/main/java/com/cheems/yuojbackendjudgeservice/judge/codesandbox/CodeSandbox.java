package com.cheems.cojbackendjudgeservice.judge.codesandbox;

import com.cheems.cojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.cheems.cojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
