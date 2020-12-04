package io.renren.modules.qq.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.exception.RRException;
import io.renren.common.utils.*;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqBotDao;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.entity.bo.Msg;
import io.renren.modules.qq.service.QqBotService;
import io.renren.modules.qq.utils.Global;
import io.renren.modules.qq.utils.MiraiHttpApi;
import io.renren.modules.qq.utils.MiraiWebSocket;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.transaction.annotation.Transactional;


@Service("qqBotService")
public class QqBotServiceImpl extends ServiceImpl<QqBotDao, QqBotEntity> implements QqBotService {

    @Autowired
    private MiraiHttpApi miraiHttpApi;

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private QqBotDao qqBotDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QqBotEntity> page = this.page(
                new Query<QqBotEntity>().getPage(params),
                new QueryWrapper<QqBotEntity>()
        );

        return new PageUtils(page);
    }

    private void authSession(QqBotEntity qqBot) throws Exception {
        String sessionKey = miraiHttpApi.authentication(qqBot.getServerIp(), qqBot.getServerPort(), qqBot.getAuthKey());
        boolean isAuth = miraiHttpApi.authorization(qqBot.getServerIp(), qqBot.getServerPort(), qqBot.getQqId(), sessionKey);
        boolean isOpenWs = miraiHttpApi.openClientWebsocket(qqBot.getServerIp(), qqBot.getServerPort(), sessionKey);
        if(!sessionKey.isEmpty() && isAuth && isOpenWs) {
            qqBot.setSessionKey(sessionKey);
            // 创建websocket线程
            synchronized (Global.T_MiraiTask) {
                // 销毁原socket
                WebSocketClient socketClient = (WebSocketClient) Global.T_MiraiTask.get(qqBot.getId());
                if(socketClient != null) {
                    socketClient.close();
                    while (!socketClient.isClosed()){
                        TimeUnit.SECONDS.sleep(1);
                    }
                }
                // 创建新线程
                socketClient = new MiraiWebSocket(new URI("ws://" + qqBot.getServerIp() + ":" + qqBot.getServerPort()
                        + "/message?sessionKey=" + qqBot.getSessionKey()), qqBot);
                socketClient.connect();
                Global.T_MiraiTask.put(qqBot.getId(), socketClient);
            }
        } else {
            qqBot.setEnable(2);
        }
        updateById(qqBot);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQqBot(QqBotEntity qqBot) throws Exception {
        // 一个QQ只能配置一次
        if(getOne(new QueryWrapper<QqBotEntity>().eq("qq_id", qqBot.getQqId())) != null) {
            throw new RRException("一个QQ只能配置一次");
        }

        save(qqBot);

        // 保存回复模板
        for (QqReplyEntity qqReplyEntity : qqBot.getReplyList()) {
            qqBotDao.saveQqBotReply(qqBot.getId(), qqReplyEntity.getReplyId());
        }

        if(qqBot.getEnable() == 1) {
            authSession(qqBot);
        }
    }

    @Transactional
    @Override
    public void updateQqBot(QqBotEntity qqBot) throws Exception {
        QqBotEntity oldBot = getById(qqBot.getId());
        // 更新回复模板
        qqBotDao.deleteQqBotReplyByBotId(qqBot.getId());
        for (QqReplyEntity qqReplyEntity : qqBot.getReplyList()) {
            qqBotDao.saveQqBotReply(qqBot.getId(), qqReplyEntity.getReplyId());
        }
        // 释放原session
        if(oldBot.getEnable() == 1 && !oldBot.getSessionKey().isEmpty()) {
            miraiHttpApi.releaseSession(oldBot.getServerIp(), oldBot.getServerPort(), oldBot.getQqId(), oldBot.getSessionKey());
        }
        // 重新申请session
        if(qqBot.getEnable() == 1) {
            authSession(qqBot);
        } else {
            synchronized (Global.T_MiraiTask) {
                // 销毁原socket
                WebSocketClient socketClient = (WebSocketClient) Global.T_MiraiTask.get(qqBot.getId());
                if(socketClient != null) {
                    socketClient.close();
                }
                qqBot.setSessionKey("");
            }
        }
        updateById(qqBot);
    }

    @Override
    public QqBotEntity getQqBotInfo(Long id) {
        QqBotEntity qqBot = getById(id);
        qqBot.setReplyList(qqBotDao.getQqBotReplyList(id));
        return qqBot;
    }

    @Override
    public List<QqGroup> getGroups(Long qqId) {
        QqBotEntity qqBotEntity = getOne(new QueryWrapper<QqBotEntity>().eq("qq_id", qqId));
        return miraiHttpApi.getGroups(qqBotEntity.getServerIp(),qqBotEntity.getServerPort(), qqBotEntity.getSessionKey());
    }

    @Override
    public List<QqFriend> getFriends(Long qqId) {
        QqBotEntity qqBotEntity = getOne(new QueryWrapper<QqBotEntity>().eq("qq_id", qqId));
        return miraiHttpApi.getFriends(qqBotEntity.getServerIp(),qqBotEntity.getServerPort(), qqBotEntity.getSessionKey());
    }

    @Override
    public void sendMsg(Msg msg) {
        QqBotEntity qqBotEntity = getOne(new QueryWrapper<QqBotEntity>().eq("qq_id", msg.getQqId()));
        if(qqBotEntity == null || qqBotEntity.getSessionKey() == null || qqBotEntity.getSessionKey().length() < 1) {
            throw new RRException("QQ机器人不可用");
        }

        List<Long> imageIds = msg.getImages();
        String content = msg.getContent();

        List<String> images = new ArrayList<>();
        for (SysOssEntity sysOssEntity : sysOssService.listByIds(imageIds)) {
            images.add(sysOssEntity.getUrl());
        }

        // 发送群消息
        if(msg.getGroups() != null) {
            for (QqGroup group : msg.getGroups()) {
                miraiHttpApi.sendMsg(qqBotEntity.getServerIp(),
                        qqBotEntity.getServerPort(),
                        qqBotEntity.getSessionKey(),
                        MiraiHttpApi.MSG_TYPE_GROUP,
                        group.getId(),
                        content,
                        images);
            }
        }
        // 发送好友消息
        if(msg.getFriends() != null) {
            for (QqFriend friend : msg.getFriends()) {
                miraiHttpApi.sendMsg(qqBotEntity.getServerIp(),
                        qqBotEntity.getServerPort(),
                        qqBotEntity.getSessionKey(),
                        MiraiHttpApi.MSG_TYPE_FRIEND,
                        friend.getId(),
                        content,
                        images);
            }
        }
    }

}
