package com.cheems.coj.mq;

public final class MqConstants {

    private MqConstants() {
    }

    public static final String JUDGE_EXCHANGE = "coj.judge.direct";
    public static final String JUDGE_QUEUE = "coj.judge.submit";
    public static final String JUDGE_ROUTING_KEY = "judge.submit";
    public static final String JUDGE_DLX = "coj.judge.dlx";
    public static final String JUDGE_DLQ = "coj.judge.submit.dlq";
    public static final String JUDGE_DLQ_ROUTING_KEY = "judge.submit.dlq";


    public static final String JUDGE_SUBMIT = "JUDGE_SUBMIT";
}
