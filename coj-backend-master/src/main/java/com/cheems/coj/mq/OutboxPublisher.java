package com.cheems.coj.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cheems.coj.model.entity.MqOutbox;
import com.cheems.coj.model.enums.MqOutboxStatusEnum;
import com.cheems.coj.mq.message.JudgeSubmitMessage;
import com.cheems.coj.mq.producer.JudgeSubmitProducer;
import com.cheems.coj.service.MqOutboxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class OutboxPublisher {

    private static final int MAX_RETRY_COUNT = 3;

    @Resource
    private MqOutboxService mqOutboxService;

    @Resource
    private JudgeSubmitProducer judgeSubmitProducer;


    @Scheduled(fixedDelay = 5000)
    public void publish() {
        QueryWrapper<MqOutbox> mqOutboxQueryWrapper = new QueryWrapper<>();
        mqOutboxQueryWrapper.in("status", MqOutboxStatusEnum.PENDING.getValue(), MqOutboxStatusEnum.FAILED.getValue());
        mqOutboxQueryWrapper.eq("isDelete", 0);
        mqOutboxQueryWrapper.and(wrapper ->
                wrapper.isNull("nextRetryTime")
                        .or()
                        .le("nextRetryTime", new Date())
        );
        mqOutboxQueryWrapper.lt("retryCount", MAX_RETRY_COUNT);
        mqOutboxQueryWrapper.orderByAsc("id");
        mqOutboxQueryWrapper.last("limit 20");
        List<MqOutbox> mqOutboxList = mqOutboxService.list(mqOutboxQueryWrapper);

        for (MqOutbox mqOutbox : mqOutboxList) {

            try {
                String payload = mqOutbox.getPayload();
                JudgeSubmitMessage judgeSubmitMessage = JSONUtil.toBean(payload, JudgeSubmitMessage.class);
                judgeSubmitProducer.sendJudgeSubmitMessage(judgeSubmitMessage);
                mqOutbox.setStatus(MqOutboxStatusEnum.SENT.getValue());
                mqOutbox.setLastError(null);
                boolean b = mqOutboxService.updateById(mqOutbox);
                if (!b) {
                    log.error("消息已发送但更新 outbox 状态失败，可能导致重复投递，outboxId={},aggregateId={}", mqOutbox.getId(), mqOutbox.getAggregateId());
                }
                log.info("投递成功，outboxId={},aggregateId={}", mqOutbox.getId(), mqOutbox.getAggregateId());
            } catch (Exception e) {
                log.error("发送消息失败，outboxId={},aggregateId={}", mqOutbox.getId(), mqOutbox.getAggregateId(), e);

                Integer retryCount = mqOutbox.getRetryCount() + 1;
                mqOutbox.setStatus(MqOutboxStatusEnum.FAILED.getValue());
                mqOutbox.setLastError(e.getMessage());
                mqOutbox.setRetryCount(retryCount);

                if(retryCount < MAX_RETRY_COUNT) {
                    mqOutbox.setNextRetryTime(new Date(System.currentTimeMillis() + 30000));
                }else {
                    mqOutbox.setNextRetryTime(null);
                    log.error("达到最大重试次数，停止继续投递");
                }


                boolean b = mqOutboxService.updateById(mqOutbox);
                if (!b) {
                    log.error("消息发送失败且更新 outbox 状态失败，outboxId={},aggregateId={}", mqOutbox.getId(), mqOutbox.getAggregateId());
                }
            }
        }


    }

}
