package com.github.nekolr.slime.service.impl;

import com.github.nekolr.slime.dao.FeedRepository;
import com.github.nekolr.slime.domain.Feed;
import com.github.nekolr.slime.service.FeedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {

    @Resource
    private FeedRepository feedRepository;

    @Override
    public Boolean existsByGuid(String guid) {
        return feedRepository.existsByGuid(guid);
    }

    @Override
    public List<Feed> findByGuid(String guid) {
        return feedRepository.findByGuid(guid);
    }

    @Override
    public List<Feed> findByGuidAndPushed(String guid, Boolean pushed) {
        return feedRepository.findByGuidAndPushed(guid, pushed);
    }

    @Override
    public List<Feed> findByCreateTimeLessThanEqual(Date time) {
        return feedRepository.findByCreateTimeLessThanEqual(time);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Feed save(Feed entity) {
        return feedRepository.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInBatch(List<Feed> entries) {
        feedRepository.deleteInBatch(entries);
    }
}
