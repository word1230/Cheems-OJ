package com.cheems.coj.model.enums;

import lombok.Getter;

@Getter
public enum MqOutboxStatusEnum {


    PENDING(0),
    SENT(1),
    FAILED(2);;


    private final Integer value;

    MqOutboxStatusEnum(Integer value) {
        this.value = value;
    }

    public static MqOutboxStatusEnum getEnumByValue(Integer value) {
        for (MqOutboxStatusEnum item : MqOutboxStatusEnum.values()) {
            if (item.value.equals(value)) {
                return item;
            }
        }
        return null;
    }

}
