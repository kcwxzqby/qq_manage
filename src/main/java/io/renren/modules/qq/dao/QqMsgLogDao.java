package io.renren.modules.qq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.qq.entity.QqMsgLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * qq消息日志
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-02 21:26:57
 */
@Mapper
@Repository
public interface QqMsgLogDao extends BaseMapper<QqMsgLogEntity> {

    void saveMsgOss(Long msgId, Long ossId);

}
