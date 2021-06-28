package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.dao.FeedRepository;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeedFunctionExecutor implements FunctionExecutor {

    private static FeedRepository feedRepository;

    @Override
    public String getFunctionPrefix() {
        return "feed";
    }

    @Comment("添加 feed 到数据库中")
    public void add(String guid, String title, String author,
                    String desc, String imgUrl, Date pubDate) {
        Feed feed = new Feed();
        feed.setGuid(guid);
        feed.setTitle(title);
        feed.setAuthor(author);
        feed.setDescription(desc);
        feed.setImgUrl(imgUrl);
        feed.setPublishDate(pubDate);
        feedRepository.save(feed);
    }

    @Autowired
    public void setFeedRepository(FeedRepository feedRepository) {
        FeedFunctionExecutor.feedRepository = feedRepository;
    }
}
