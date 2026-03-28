package com.cheems.coj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cheems.coj.model.entity.MqOutbox;
import com.cheems.coj.service.MqOutboxService;
import com.cheems.coj.mapper.MqOutboxMapper;
import org.springframework.stereotype.Service;

/**
* @author cheems
* @description 针对表【mq_outbox(MQ 消息发件箱表（Transactional Outbox Pattern）)】的数据库操作Service实现
* @createDate 2026-03-28 00:04:05
*/
@Service
public class MqOutboxServiceImpl extends ServiceImpl<MqOutboxMapper, MqOutbox>
    implements MqOutboxService{

}




