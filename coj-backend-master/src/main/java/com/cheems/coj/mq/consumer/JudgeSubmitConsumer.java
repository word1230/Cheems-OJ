package com.cheems.coj.mq.consumer;

import com.cheems.coj.judge.JudgeService;
import com.cheems.coj.mq.MqConstants;
import com.cheems.coj.mq.message.JudgeSubmitMessage;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@Component
public class JudgeSubmitConsumer {

    @Resource
    private JudgeService judgeService;


    @RabbitListener(queues = MqConstants.JUDGE_QUEUE)
    public void receiveJudgeSubmitMessage(JudgeSubmitMessage judgeSubmitMessage, Message message, Channel channel) throws IOException {


        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        if (judgeSubmitMessage == null) {
            log.warn("判题消息为空");
            channel.basicReject(deliveryTag, false);
            return;
        }

        if (judgeSubmitMessage.getSubmissionId() == null) {
            log.warn("submissionId 为空");
            channel.basicReject(deliveryTag, false);
            return;
        }

        log.info("接收判题消息：questionSubmitId:{},traceId:{}", judgeSubmitMessage.getSubmissionId(), judgeSubmitMessage.getTraceId());

        try {
            judgeService.doJudge(judgeSubmitMessage.getSubmissionId());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("判题失败，submissionId={},traceId={}", judgeSubmitMessage.getSubmissionId(), judgeSubmitMessage.getTraceId(), e);
            channel.basicReject(deliveryTag, false);
        }


    }


}
