package com.github.nekolr.slime.job;

import com.github.nekolr.slime.config.FeedConfig;
import com.github.nekolr.slime.dao.FeedRepository;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.util.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class FeedCleanJob {

    @Resource
    private FeedConfig feedConfig;

    @Resource
    private FeedRepository feedRepository;

    /**
     * 在使用了 @EnableWebSocket 之后，自动配置类就不会生效，需要手动创建
     */
    @Resource(name = "feedCleanThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler scheduler;


    @Bean
    public ThreadPoolTaskScheduler feedCleanThreadPoolTaskScheduler(TaskSchedulerBuilder builder) {
        builder.poolSize(8);
        return builder.build();
    }

    public void run() {
        // 延迟 10 秒再开始执行
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MILLISECOND, 10000);
        scheduler.scheduleWithFixedDelay(this::clean, now.getTime(), feedConfig.getCheckInterval().toMillis());
    }

    @PostConstruct
    public void initialize() {
        run();
    }

    public void clean() {
        log.debug("开始清理过期的 feed");
        Date beforeTime = TimeUtils.getBeforeTime(feedConfig.getCheckTimeBefore());
        List<Feed> feeds = feedRepository.findByCreateTimeLessThanEqual(beforeTime);
        // 删除对应的文件
        feeds.stream().forEach(feed -> {
            String imgPath = StringUtils.replace(feed.getImgUrl(), feedConfig.getPixivHost(), "");
            File file = new File(feedConfig.getPixivSavePath() + File.separator + imgPath);
            if (file.exists()) {
                file.delete();
            }
        });
        // 批量删除
        feedRepository.deleteInBatch(feeds);
        log.debug("清理过期的 feed 完毕");
    }
}
