package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Feed;

import java.util.Date;
import java.util.List;

public interface FeedService {

    Feed save(Feed entity);

    Boolean existsByGuid(String guid);

    List<Feed> findByGuid(String guid);

    void deleteInBatch(List<Feed> entries);

    List<Feed> findByCreateTimeLessThanEqual(Date time);

    List<Feed> findByGuidAndPushed(String guid, Boolean pushed);

    List<Feed> findByCategoryAndPushed(String group, Boolean pushed);
}
