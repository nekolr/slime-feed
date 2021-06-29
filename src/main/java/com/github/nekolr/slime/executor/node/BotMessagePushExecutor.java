package com.github.nekolr.slime.executor.node;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.github.nekolr.slime.config.FeedConfig;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.dao.FeedRepository;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.entity.BotRequestBody;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.support.ExpressionParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 机器人消息推送执行器
 */
@Component
@Slf4j
public class BotMessagePushExecutor implements NodeExecutor {

    /**
     * 推送消息的目标（多个目标以逗号分隔）
     */
    private static final String MESSAGE_PUSH_TARGET = "target";

    /**
     * 请求的 Token
     */
    private static final String MESSAGE_PUSH_TOKEN = "sessionKey";

    /**
     * 消息推送地址
     */
    private static final String MESSAGE_PUSH_URL = "messagePushUrl";

    /**
     * 消息推送的请求方法
     */
    private static final String MESSAGE_PUSH_METHOD = "messagePushMethod";

    @Resource
    private FeedConfig feedConfig;

    @Resource
    private FeedRepository feedRepository;

    @Resource
    private ExpressionParser expressionParser;


    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String token = node.getJsonProperty(MESSAGE_PUSH_TOKEN);
        String target = node.getJsonProperty(MESSAGE_PUSH_TARGET);
        String messagePushUrl = node.getJsonProperty(MESSAGE_PUSH_URL);
        String messagePushMethod = node.getJsonProperty(MESSAGE_PUSH_METHOD);

        String[] targets = StringUtils.split(target, ",");
        for (String messagePushTarget : targets) {
            try {
                // 解析表达式
                Object tokenObj = expressionParser.parse(token, variables);
                if (!Objects.isNull(tokenObj)) {
                    log.debug("表达式 {} 的结果为 {}", token, tokenObj);
                }
                List<Feed> feeds = feedRepository.findByGuid("");
                HttpRequest request = HttpUtil.createRequest(Method.valueOf(messagePushMethod), messagePushUrl);

                BotRequestBody requestBody = new BotRequestBody();
                requestBody.setSessionKey((String) tokenObj);
                requestBody.setTarget(messagePushTarget);
                List<Map<String, String>> messageChains = new ArrayList<>(feeds.size());
                if (feeds.size() > 1) {
                    List<String> images = feeds.stream().map(feed -> {
                        String imgPath = StringUtils.replace(feed.getImgUrl(), feedConfig.getPixivHost(), "");
                        try {
                            File imgFile = new File(feedConfig.getPixivSavePath() + File.separator + imgPath);
                            byte[] bytes = FileUtils.readFileToByteArray(imgFile);
                            return Base64.encodeBase64String(bytes);
                        } catch (IOException e) {
                            return null;
                        }
                    }).collect(Collectors.toList());
                }
                // 发起请求
                request.execute();

            } catch (Throwable t) {
                log.error("解析表达式 {} 出错", token, t);
            }
        }
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
