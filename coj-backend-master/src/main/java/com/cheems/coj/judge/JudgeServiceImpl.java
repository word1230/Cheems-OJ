package com.cheems.coj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheems.coj.common.ErrorCode;
import com.cheems.coj.exception.BusinessException;
import com.cheems.coj.judge.codesandbox.CodeSandbox;
import com.cheems.coj.judge.codesandbox.CodeSandboxFactory;
import com.cheems.coj.judge.codesandbox.CodeSandboxProxy;
import com.cheems.coj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cheems.coj.judge.codesandbox.model.ExecuteCodeResponse;
import com.cheems.coj.judge.strategy.JudgeContext;
import com.cheems.coj.model.dto.question.JudgeCase;
import com.cheems.coj.judge.codesandbox.model.JudgeInfo;
import com.cheems.coj.model.entity.Question;
import com.cheems.coj.model.entity.QuestionSubmit;
import com.cheems.coj.model.enums.QuestionSubmitStatusEnum;
import com.cheems.coj.service.QuestionService;
import com.cheems.coj.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Value("${codesandbox.auth.access-key:}")
    private String sandboxAccessKey;

    @Value("${codesandbox.auth.secret-key:}")
    private String sandboxSecretKey;


    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1）传入题目的提交 id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "题目不存在");
        }
        // 2）如果题目提交状态不为等待中，就不用重复执行了
        if (!QuestionSubmitStatusEnum.WAITING.getValue().equals(questionSubmit.getStatus())) {
            log.info("跳过重复判题，questionSubmitId={},status={}", questionSubmitId, questionSubmit.getStatus());
            return questionSubmit;
        }
        // 3）更改判题（题目提交）的状态为 “判题中”，防止重复执行
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        QueryWrapper<QuestionSubmit> queryWrapper =  new QueryWrapper<>();
        queryWrapper.eq("id",questionSubmitId)
                .eq("status",QuestionSubmitStatusEnum.WAITING.getValue());
        boolean update = questionSubmitService.update(questionSubmitUpdate,queryWrapper);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        try {
            // 4）调用沙箱，获取到执行结果
            CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type, sandboxAccessKey, sandboxSecretKey);
            codeSandbox = new CodeSandboxProxy(codeSandbox);
            String language = questionSubmit.getLanguage();
            String code = questionSubmit.getCode();
            // 获取输入用例
            String judgeCaseStr = question.getJudgeCase();
            List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
            List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
            ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                    .code(code)
                    .language(language)
                    .inputList(inputList)
                    .build();
            ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
            List<String> outputList = executeCodeResponse.getOutputList();
            // 5）根据沙箱的执行结果，设置题目的判题状态和信息
            JudgeContext judgeContext = new JudgeContext();
            judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
            judgeContext.setInputList(inputList);
            judgeContext.setOutputList(outputList);
            judgeContext.setJudgeCaseList(judgeCaseList);
            judgeContext.setQuestion(question);
            judgeContext.setQuestionSubmit(questionSubmit);
            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
            // 6）修改数据库中的判题结果
            questionSubmitUpdate = new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            update = questionSubmitService.updateById(questionSubmitUpdate);
            if (!update) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionSubmitId);
            return questionSubmitResult;
        } catch (Exception e) {
            log.error("判题出错，questionSubmitId={}", questionSubmitId, e);

            QuestionSubmit questionFailSubmit = new QuestionSubmit();
            questionFailSubmit.setId(questionSubmitId);
            questionFailSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());

            JudgeInfo judgeInfo = new JudgeInfo();

            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.trim().isEmpty()) {
                errorMessage = "未知异常";
            }
            judgeInfo.setMessage("系统执行错误：" + errorMessage.substring(0, Math.min(errorMessage.length(), 100)));

            judgeInfo.setMemory(null);
            judgeInfo.setTime(null);

            questionFailSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
            boolean failUpdate = questionSubmitService.updateById(questionFailSubmit);
            if (!failUpdate) {
                log.error("判题失败后更新状态失败");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }
}
