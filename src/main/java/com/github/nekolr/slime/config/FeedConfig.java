package com.github.nekolr.slime.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.time.Duration;

@Configuration
@Getter
public class FeedConfig {

    /**
     * pixiv 图片保存的路径
     */
    @Value("${feed.pixiv.save-path}")
    private String pixivSavePath;

    /**
     * pixiv 图片服务域名地址
     */
    @Value("${feed.pixiv.host}")
    private String pixivHost;

    /**
     * feed 清理任务执行间隔
     */
    @Value("${feed.check-interval}")
    private Duration checkInterval;

    /**
     * 清理多长时间之前的 feed
     */
    @Value("${feed.check-time-before}")
    private Duration checkTimeBefore;

    /**
     * 图片文件最大限制
     */
    @Value("${feed.image-file-max-size}")
    private DataSize imageFileMaxSize;
}
