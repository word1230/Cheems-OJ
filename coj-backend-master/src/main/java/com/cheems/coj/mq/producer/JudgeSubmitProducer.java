package com.cheems.coj.mq.producer;


import com.cheems.coj.mq.MqConstants;
import com.cheems.coj.mq.message.JudgeSubmitMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class JudgeSubmitProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendJudgeSubmitMessage(JudgeSubmitMessage message) {

        rabbitTemplate.convertAndSend(MqConstants.JUDGE_EXCHANGE, MqConstants.JUDGE_ROUTING_KEY, message);
        log.info("已发送判题消息，submissionId={},traceId={}", message.getSubmissionId(), message.getTraceId());
    }
}
