package io.renren.modules.qq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.qq.entity.QqMsgLogEntity;

import java.util.List;
import java.util.Map;

/**
 * qq消息日志
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-02 21:26:57
 */
public interface QqMsgLogService extends IService<QqMsgLogEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveQqMsgLog(QqMsgLogEntity qqMsgLogEntity, List<String> images);
}

