package com.github.nekolr.slime.dao;

import com.github.nekolr.slime.domain.Feed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Date;
import java.util.List;

public interface FeedRepository extends JpaRepository<Feed, Long>, JpaSpecificationExecutor<Feed> {

    List<Feed> findByCreateTimeLessThanEqual(Date time);
}
