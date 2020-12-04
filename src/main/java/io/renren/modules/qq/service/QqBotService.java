package io.renren.modules.qq.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.entity.bo.Msg;

import java.util.List;
import java.util.Map;

/**
 * qq机器人表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
public interface QqBotService extends IService<QqBotEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveQqBot(QqBotEntity qqBot) throws Exception;

    void updateQqBot(QqBotEntity qqBot) throws Exception;

    QqBotEntity getQqBotInfo(Long id);

    List<QqGroup> getGroups(Long qqId);

    List<QqFriend> getFriends(Long qqId);

    void sendMsg(Msg msg);
}

