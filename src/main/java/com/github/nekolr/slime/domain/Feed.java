package com.github.nekolr.slime.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

/**
 * Feed 实体类
 */
@Table(name = "slime_sp_feed")
@Entity
@Getter
@Setter
@ToString
public class Feed {

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    /**
     * 唯一地址
     */
    private String guid;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 描述
     */
    @Column(columnDefinition = "longtext")
    private String description;

    /**
     * 图片地址
     */
    @Column(name = "img_url")
    private String imgUrl;

    /**
     * 发布时间
     */
    @Column(name = "publish_date")
    private Date publishDate;

    /**
     * 创建时间
     */
    @Column(name = "create_time", insertable = false, updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Date createTime;
}
