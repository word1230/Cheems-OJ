package com.cheems.coj.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cheems.coj.model.entity.MqOutbox;

/**
* @author cheems
* @description 针对表【mq_outbox(MQ 消息发件箱表（Transactional Outbox Pattern）)】的数据库操作Service
* @createDate 2026-03-28 00:04:05
*/
public interface MqOutboxService extends IService<MqOutbox> {

}
