package io.renren.modules.qq.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.renren.modules.qq.entity.QqScheduleMsgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * qq定时发送消息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-04 00:21:29
 */
@Repository
@Mapper
public interface QqScheduleMsgDao extends BaseMapper<QqScheduleMsgEntity> {

    void saveScheduleOss(Long scheduleId, Long ossId);

    void deleteScheduleOssById(Long scheduleId);

    List<Long> getScheduleOssIds(Long scheduleId);

}
