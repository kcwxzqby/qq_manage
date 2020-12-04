package io.renren.modules.qq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.qq.entity.QqReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 自动回复表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@Mapper
@Repository
public interface QqReplyDao extends BaseMapper<QqReplyEntity> {

    void saveReplyOss(Long replyId, Long ossId);

    void deleteReplyOssById(Long replyId);

    List<Long> getReplyOssIds(Long replyId);

}
