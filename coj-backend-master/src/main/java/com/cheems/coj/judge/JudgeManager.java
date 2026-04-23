package com.cheems.coj.judge;

import com.cheems.coj.judge.strategy.DefaultJudgeStrategy;
import com.cheems.coj.judge.strategy.JavaLanguageJudgeStrategy;
import com.cheems.coj.judge.strategy.JudgeContext;
import com.cheems.coj.judge.strategy.JudgeStrategy;
import com.cheems.coj.judge.codesandbox.model.JudgeInfo;
import com.cheems.coj.model.entity.QuestionSubmit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {


    @Resource
    private List<JudgeStrategy> strategyList;

    @Resource
    private DefaultJudgeStrategy defaultJudgeStrategy;

    private Map<String,JudgeStrategy> strategyMap;


    @PostConstruct
    public void init(){
      strategyMap =   new ConcurrentHashMap<>();
      for(JudgeStrategy strategy :strategyList){
          strategyMap.put(strategy.getSupportedLanguages(),strategy);
      }
    }

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = strategyMap.getOrDefault(language,defaultJudgeStrategy);
        return judgeStrategy.doJudge(judgeContext);
    }

}
