package com.cheems.coj.judge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author cheems
 * @description 判题分发服务 使用线程池来调用判题服务,防止高并发
 */
@Slf4j
@Service
public class JudgeDispatchServiceImpl implements JudgeDispatchService {

    @Resource
    private JudgeService judgeService;

    @Resource(name = "judgeExecutor")
    private ThreadPoolExecutor judgeExecutor;

    @Override
    public void dispatch(long questionSubmitId)  {
        judgeExecutor.execute(() -> {
            try {
                judgeService.doJudge(questionSubmitId);
            } catch (Exception e) {
                log.error("异步判题任务执行失败，questionSubmitId={}", questionSubmitId, e);
            }
        });
    }
}
