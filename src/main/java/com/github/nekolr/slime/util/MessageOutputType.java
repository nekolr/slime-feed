package com.github.nekolr.slime.util;

import lombok.Getter;

public enum MessageOutputType {

    SINGLE(0, "单条输出"),
    MERGE(1, "合并输出");

    @Getter
    private Integer code;

    @Getter
    private String desc;

    MessageOutputType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
