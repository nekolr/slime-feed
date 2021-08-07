package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, JpaSpecificationExecutor<Feed> {

    Boolean existsByGuid(String guid);

    List<Feed> findByGuid(String guid);

    List<Feed> findByGuidAndPushed(String guid, Boolean pushed);

    List<Feed> findByCategoryAndPushed(String group, Boolean pushed);

    List<Feed> findByCreateTimeLessThanEqual(Date time);
}
