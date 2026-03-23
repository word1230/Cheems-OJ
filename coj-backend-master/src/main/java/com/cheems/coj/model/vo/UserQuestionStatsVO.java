package com.cheems.coj.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户题目提交统计 VO
 */
@Data
public class UserQuestionStatsVO implements Serializable {

    /**
     * 总提交次数
     */
    private long submitCount;

    /**
     * 通过次数（status = 2）
     */
    private long acceptedCount;

    private static final long serialVersionUID = 1L;
}
