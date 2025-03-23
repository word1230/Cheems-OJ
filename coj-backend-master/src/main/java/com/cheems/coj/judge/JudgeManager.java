package com.cheems.coj.judge;

import com.cheems.coj.judge.strategy.DefaultJudgeStrategy;
import com.cheems.coj.judge.strategy.JavaLanguageJudgeStrategy;
import com.cheems.coj.judge.strategy.JudgeContext;
import com.cheems.coj.judge.strategy.JudgeStrategy;
import com.cheems.coj.judge.codesandbox.model.JudgeInfo;
import com.cheems.coj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
