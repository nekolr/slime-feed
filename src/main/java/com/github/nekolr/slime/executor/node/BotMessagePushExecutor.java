package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 机器人消息推送执行器
 */
@Component
public class BotMessagePushExecutor implements NodeExecutor {

    /**
     * 消息推送地址
     */
    private static final String MESSAGE_PUSH_URL = "messagePushUrl";

    /**
     * 消息推送的请求方法
     */
    private static final String MESSAGE_PUSH_METHOD = "messagePushMethod";


    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String messagePushUrl = node.getJsonProperty(MESSAGE_PUSH_URL);
    }

    @Override
    public String supportType() {
        return "bot";
    }

    @Override
    public Shape shape() {
        Shape shape = new Shape();
        shape.setImage("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAABZklEQVRYhe1Xy7GDMAx0CVyIdXwlUAIlpARKoAQ6WHVACSmBEighJVDCvsMjDBDbMT8fMs8zusnsarWyjTH/68DKwULAMjlwBmZW+RAlRUmr7JMSsGD9Ap8CbNIRUOKNgLJNRkDA0qFAlYyAMUsVrBJJwY0x5gbek1afg4UF67HyVpTdrAWdKFurhIBNDhbnAs9GLjasshfw5xC4gJVVDlvBF0TAei94cwR4pcY2kwpYHq3cMaZlFPh4zPangv+pMMRWX50NvskPexy/QYXHZwWUz9XGTsBmPANiWuPND7ZBwHI83Z6zDcjAbEWwDbn9U/4NvL8Z0nW9WuWw/lhApe35cz94qup8anl8sil/4QcXgVC/XF7YkR8m8OrXm5yut8CO/CgCopyu2gzMou6GyPwFAecb7+pYvyHH8bjsFJwrlIOFb2LM1QR8Rg069qyI+n8Iufwg+OCaEu+aPHFOlIefZ1+7fgG+YN4X55+CqgAAAABJRU5ErkJggg==");
        shape.setName("bot");
        shape.setTitle("Bot");
        shape.setLabel("Bot");
        return shape;

    }
}
