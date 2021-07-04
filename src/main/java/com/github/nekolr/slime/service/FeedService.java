package com.github.nekolr.slime.service;

import com.github.nekolr.slime.domain.Feed;

import java.util.Date;
import java.util.List;

public interface FeedService extends BaseService<Feed> {

    Boolean existsByGuid(String guid);

    List<Feed> findByGuid(String guid);

    List<Feed> findByGuidAndPushed(String guid, Boolean pushed);

    List<Feed> findByCreateTimeLessThanEqual(Date time);
}
