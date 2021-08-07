package com.github.nekolr.slime.executor.node;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.config.FeedConfig;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.entity.BotRequestBody;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.Shape;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.service.FeedService;
import com.github.nekolr.slime.support.ExpressionParser;
import com.github.nekolr.slime.util.ImageUtils;
import com.github.nekolr.slime.util.MessageOutputType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
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
     * 分组
     */
    private static final String CATEGORY = "category";

    /**
     * 消息的唯一标识
     */
    private static final String MESSAGE_GUID = "guid";

    /**
     * 请求的 Token
     */
    private static final String MESSAGE_PUSH_TOKEN = "sessionKey";

    /**
     * 消息推送地址
     */
    private static final String MESSAGE_PUSH_URL = "messagePushUrl";

    /**
     * 消息输出形式
     */
    private static final String MESSAGE_OUTPUT_TYPE = "messageOutputType";

    /**
     * 推送消息的目标（多个目标以逗号分隔）
     */
    private static final String MESSAGE_PUSH_TARGET = "messagePushTarget";

    /**
     * 消息推送的请求方法
     */
    private static final String MESSAGE_PUSH_METHOD = "messagePushMethod";


    @Resource
    private FeedConfig feedConfig;

    @Resource
    private FeedService feedService;

    @Resource
    private ExpressionParser expressionParser;


    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String guid = node.getJsonProperty(MESSAGE_GUID);
        String category = node.getJsonProperty(CATEGORY);
        String token = node.getJsonProperty(MESSAGE_PUSH_TOKEN);
        String target = node.getJsonProperty(MESSAGE_PUSH_TARGET);
        String messagePushUrl = node.getJsonProperty(MESSAGE_PUSH_URL);
        String messageOutputType = node.getJsonProperty(MESSAGE_OUTPUT_TYPE);
        String messagePushMethod = node.getJsonProperty(MESSAGE_PUSH_METHOD);

        String[] targets = StringUtils.split(target, ",");
        for (String messagePushTarget : targets) {
            // 单条输出
            if (MessageOutputType.SINGLE.getCode().equals(Integer.valueOf(messageOutputType))) {
                try {
                    // 解析 token
                    Object tokenObj = expressionParser.parse(token, variables);
                    if (!Objects.isNull(tokenObj)) {
                        log.debug("Token {} 的结果为 {}", token, tokenObj);
                    }
                    // 解析 Guid
                    Object guidObj = expressionParser.parse(guid, variables);
                    if (!Objects.isNull(tokenObj)) {
                        log.debug("Guid {} 的结果为 {}", guid, guidObj);
                    }
                    List<Feed> feeds = feedService.findByGuidAndPushed((String) guidObj, Boolean.FALSE);
                    if (!feeds.isEmpty()) {
                        HttpRequest request = HttpUtil.createRequest(Method.valueOf(messagePushMethod), messagePushUrl);

                        BotRequestBody requestBody = new BotRequestBody();
                        // 设置请求的 Token
                        requestBody.setSessionKey((String) tokenObj);
                        // 设置推送对象
                        requestBody.setTarget(messagePushTarget);
                        // 消息链
                        List<Map<String, String>> messageChains = new ArrayList<>(feeds.size());
                        // 填充消息链
                        this.fillMessageChains(messageChains, feeds, messageOutputType);
                        // 设置消息链
                        requestBody.setMessageChain(messageChains);
                        // 设置请求 body
                        request.body(JSON.toJSONString(requestBody));
                        // 发起请求
                        request.execute();
                    }
                } catch (Throwable t) {
                    log.error("推送消息出错", t);
                }
            }
            // 合并输出
            else if (MessageOutputType.MERGE.getCode().equals(Integer.valueOf(messageOutputType))) {
                try {
                    // 解析 token
                    Object tokenObj = expressionParser.parse(token, variables);
                    if (!Objects.isNull(tokenObj)) {
                        log.debug("Token {} 的结果为 {}", token, tokenObj);
                    }
                    List<Feed> feeds = feedService.findByCategoryAndPushed(category, Boolean.FALSE);
                    if (!feeds.isEmpty()) {
                        HttpRequest request = HttpUtil.createRequest(Method.valueOf(messagePushMethod), messagePushUrl);

                        BotRequestBody requestBody = new BotRequestBody();
                        // 设置请求的 Token
                        requestBody.setSessionKey((String) tokenObj);
                        // 设置推送对象
                        requestBody.setTarget(messagePushTarget);
                        // 消息链
                        List<Map<String, String>> messageChains = new ArrayList<>();
                        // 填充消息链
                        this.fillMessageChains(messageChains, feeds, messageOutputType);
                        // 设置消息链
                        requestBody.setMessageChain(messageChains);
                        // 设置请求 body
                        request.body(JSON.toJSONString(requestBody));
                        // 发起请求
                        request.execute();
                    }
                } catch (Throwable t) {
                    log.error("推送消息出错", t);
                }
            }
        }
    }

    /**
     * 填充消息链
     */
    private void fillMessageChains(List<Map<String, String>> messageChains,
                                   List<Feed> feeds, String messageOutputType) {
        if (MessageOutputType.SINGLE.getCode().equals(Integer.valueOf(messageOutputType))) {
            // 使用第一个 feed 的内容作为文本消息
            Feed firstFeed = feeds.get(0);
            Map<String, String> titleMessage = new HashMap<>();
            titleMessage.put("type", "Plain");
            titleMessage.put("text", "标题：" + firstFeed.getTitle());
            messageChains.add(titleMessage);
            Map<String, String> authorMessage = new HashMap<>();
            authorMessage.put("type", "Plain");
            authorMessage.put("text", "作者：" + firstFeed.getAuthor());
            messageChains.add(authorMessage);
            // 构建所有的图片消息
            for (Feed feed : feeds) {
                if (StringUtils.isNotBlank(feed.getImgUrl())) {
                    String base64Image = this.getBase64Image(feed);
                    if (StringUtils.isNotBlank(base64Image)) {
                        Map<String, String> imageMessage = new HashMap<>();
                        imageMessage.put("type", "Image");
                        imageMessage.put("base64", base64Image);
                        messageChains.add(imageMessage);
                    }
                }
                // 默认只要推送过，不管是否成功都算成功（可能会存在漏推的情况，无伤大雅）
                feed.setPushed(Boolean.TRUE);
                feedService.save(feed);
            }
            Map<String, String> pubDateMessage = new HashMap<>();
            pubDateMessage.put("type", "Plain");
            pubDateMessage.put("text", "发布时间：" + DateFormatUtils.format(firstFeed.getPublishDate(), "yyyy-MM-dd HH:mm:ss"));
            messageChains.add(pubDateMessage);
        }
        // 合并输出
        else if (MessageOutputType.MERGE.getCode().equals(Integer.valueOf(messageOutputType))) {
            Map<String, String> titleMessage = new HashMap<>();
            titleMessage.put("type", "Plain");
            String message = feeds.stream().map(Feed::getTitle).collect(Collectors.joining("\r\n"));
            titleMessage.put("text", message);
            messageChains.add(titleMessage);
            feeds.stream().forEach(feed -> {
                feed.setPushed(Boolean.TRUE);
                feedService.save(feed);
            });

        }
    }

    /**
     * 将图片转换成 Base64 编码的文本
     */
    private String getBase64Image(Feed feed) {
        // 去掉域名，只保留图片名称
        String imgPath = StringUtils.replace(feed.getImgUrl(), feedConfig.getPixivHost(), "");
        try {
            File imgFile = new File(feedConfig.getPixivSavePath() + File.separator + imgPath);
            if (imgFile.exists() && imgFile.isFile()) {
                ImageUtils.compressImage(imgFile, feedConfig.getImageFileMaxSize().toBytes(), 30 * 1024 * 1024);
                byte[] bytes = FileUtils.readFileToByteArray(imgFile);
                return Base64.getEncoder().encodeToString(bytes);
            } else {
                return null;
            }
        } catch (IOException e) {
            return null;
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
