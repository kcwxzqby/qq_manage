package io.renren.modules.qq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.qq.entity.QqReplyEntity;

import java.util.Map;

/**
 * 自动回复表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
public interface QqReplyService extends IService<QqReplyEntity> {

    PageUtils queryPage(Map<String, Object> params);

    QqReplyEntity getQqReplyEntityById(Long replyId);

    void saveQqReplyEntity(QqReplyEntity qqReply);

    void updateQqReplyEntity(QqReplyEntity qqReply);
}

