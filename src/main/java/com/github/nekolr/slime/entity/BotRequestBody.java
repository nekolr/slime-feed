package com.github.nekolr.slime.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class BotRequestBody {

    /**
     * token
     */
    private String sessionKey;

    /**
     * 目标
     */
    private String target;

    /**
     * 消息链
     */
    private List<Map<String, String>> messageChain;
}
