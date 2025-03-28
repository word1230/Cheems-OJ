package com.cheems.cojbackendjudgeservice.judge;

import com.cheems.cojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.cheems.cojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.cheems.cojbackendjudgeservice.judge.strategy.JudgeContext;
import com.cheems.cojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.cheems.cojbackendmodel.model.codesandbox.JudgeInfo;
import com.cheems.cojbackendmodel.model.entity.QuestionSubmit;
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
