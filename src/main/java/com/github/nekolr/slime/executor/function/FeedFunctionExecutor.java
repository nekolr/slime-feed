package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.executor.FunctionExecutor;
import com.github.nekolr.slime.service.FeedService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class FeedFunctionExecutor implements FunctionExecutor {

    private static FeedService feedService;

    @Override
    public String getFunctionPrefix() {
        return "feed";
    }

    @Comment("添加 feed 到数据库中")
    public static void add(String guid, String title, String author,
                           String desc, List<String> imgUrls, Date pubDate) {
        boolean exists = feedService.existsByGuid(guid);
        if (exists) {
            List<Feed> feeds = feedService.findByGuid(guid);
            if (!Objects.isNull(imgUrls) && !imgUrls.isEmpty()) {
                imgUrls.stream().filter(StringUtils::isNoneBlank).forEach(imgUrl -> {
                    boolean matched = false;
                    if (!feeds.isEmpty()) {
                        for (Feed feed : feeds) {
                            if (imgUrl.equals(feed.getImgUrl())) {
                                matched = true;
                            }
                        }
                    }
                    if (!matched) {
                        saveFeed(guid, title, author, desc, imgUrl, pubDate);
                    }
                });
            }
        } else {
            if (!Objects.isNull(imgUrls) && !imgUrls.isEmpty()) {
                imgUrls.stream().forEach(imgUrl -> saveFeed(guid, title, author, desc, imgUrl, pubDate));
            }
        }
    }

    private static void saveFeed(String guid, String title, String author,
                                 String desc, String imgUrl, Date pubDate) {
        Feed feed = new Feed();
        feed.setGuid(guid);
        feed.setTitle(title);
        feed.setAuthor(author);
        feed.setDescription(desc);
        feed.setImgUrl(imgUrl);
        feed.setPublishDate(pubDate);
        feed.setPushed(Boolean.FALSE);
        feedService.save(feed);
    }

    @Autowired
    public void setFeedRepository(FeedService feedService) {
        FeedFunctionExecutor.feedService = feedService;
    }
}
