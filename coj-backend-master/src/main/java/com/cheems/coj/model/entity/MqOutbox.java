package com.cheems.coj.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * MQ 消息发件箱表（Transactional Outbox Pattern）
 * @TableName mq_outbox
 */
@TableName(value ="mq_outbox")
@Data
public class MqOutbox implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 事件类型（如 JUDGE_SUBMIT）
     */
    private String eventType;

    /**
     * 聚合根 ID（questionSubmitId）
     */
    private Long aggregateId;

    /**
     * 消息体（JSON 格式）
     */
    private String payload;

    /**
     * 状态：0-待发送，1-已发送，2-失败
     */
    private Integer status;

    /**
     * 已重试次数
     */
    private Integer retryCount;

    /**
     * 下次重试时间
     */
    private Date nextRetryTime;

    /**
     * 最后一次错误信息
     */
    private String lastError;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除：0-未删除，1-已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}