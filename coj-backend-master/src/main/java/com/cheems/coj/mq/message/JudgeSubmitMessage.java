package com.cheems.coj.mq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class JudgeSubmitMessage implements Serializable {


    private static final long serialVersionUID = -2221904094659234716L;

    private Long submissionId;
    private Long questionId;
    private Long userId;
    private String traceId;
    private Long createdAt;
    private Integer schemaVersion;


}
