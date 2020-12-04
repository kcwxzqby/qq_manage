package io.renren.modules.qq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.qq.entity.QqScheduleMsgEntity;

import java.util.Map;

/**
 * qq定时发送消息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-04 00:21:29
 */
public interface QqScheduleMsgService extends IService<QqScheduleMsgEntity> {

    PageUtils queryPage(Map<String, Object> params);

    QqScheduleMsgEntity getQqScheduleMsgEntityById(Long scheduleId);

    void saveQqScheduleMsgEntity(QqScheduleMsgEntity qqScheduleMsg);

    void updateQqScheduleMsgEntity(QqScheduleMsgEntity qqScheduleMsg);

    void deleteQqScheduleMsgByIds(Long[] ids);
}

