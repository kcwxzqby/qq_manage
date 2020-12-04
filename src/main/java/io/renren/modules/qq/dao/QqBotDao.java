package io.renren.modules.qq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * qq机器人表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@Repository
@Mapper
public interface QqBotDao extends BaseMapper<QqBotEntity> {

    List<QqReplyEntity> getQqBotReplyList(Long botId);

    void saveQqBotReply(Long botId, Long replyId);

    void deleteQqBotReplyByBotId(Long botId);

}
