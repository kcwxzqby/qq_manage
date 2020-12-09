package io.renren.modules.qq.utils;

import com.google.gson.Gson;
import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqBotDao;
import io.renren.modules.qq.dao.QqReplyDao;
import io.renren.modules.qq.entity.MiraiMsgEntity;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.service.QqBotService;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname MiraiWebSocket
 * @Description TODO
 * @Date 2020/11/30 0030 下午 9:29
 * @Created by Administrator
 */
public class MiraiWebSocket extends WebSocketClient {

    private QqBotEntity qqBotEntity;

    private static final Gson gson = new Gson();

    private QqBotService qqBotService;

    private SysOssService sysOssService;

    private QqBotDao qqBotDao;

    private QqReplyDao qqReplyDao;

    private List<QqReplyEntity> replyList;

    private MiraiHttpApi miraiHttpApi;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public MiraiWebSocket(URI serverUri, QqBotEntity qqBot) {
        super(serverUri);
        try {
            this.qqBotEntity = qqBot;
            qqBotService = SpringContextUtils.getBean("qqBotService", QqBotService.class);
            qqBotDao = SpringContextUtils.getBean("qqBotDao", QqBotDao.class);
            sysOssService = SpringContextUtils.getBean("sysOssService", SysOssService.class);
            qqReplyDao = SpringContextUtils.getBean("qqReplyDao", QqReplyDao.class);
            miraiHttpApi = SpringContextUtils.getBean("miraiHttpApi", MiraiHttpApi.class);

            this.replyList = new ArrayList<>();
            for (QqReplyEntity qqReplyEntity : qqBotDao.getQqBotReplyList(qqBot.getId())) {
                // 图文回复添加图片
                if(qqReplyEntity.getReplyType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
                    List<Long> replyOssIds = qqReplyDao.getReplyOssIds(qqReplyEntity.getReplyId());
                    if(replyOssIds != null) {
                        qqReplyEntity.setImages(new ArrayList<>());
                        for (SysOssEntity oss : sysOssService.listByIds(replyOssIds)) {
                            qqReplyEntity.getImages().add(new QqReplyEntity.Image(oss.getId(), oss.getUrl()));
                        }
                    }
                }
                this.replyList.add(qqReplyEntity);
            }
        } catch (Exception e) {
            logger.error("MiraiWebSocket初始化异常", e);
        }
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("机器人【"+qqBotEntity.getQqId()+"】启动！滴滴滴滴！");
    }

    @Override
    public void onMessage(String data) {
        MiraiMsgEntity miraiMsgEntity = gson.fromJson(data, MiraiMsgEntity.class);
        System.out.println(miraiMsgEntity);
        for (QqReplyEntity qqReplyEntity : replyList) {
            List<String> images = null;
            if(qqReplyEntity.getImages() != null) {
                images = new ArrayList<>();
                for (QqReplyEntity.Image image : qqReplyEntity.getImages()) {
                    images.add(image.getUrl());
                }
            }
            // 消息匹配
            boolean match = false;
            List<MiraiMsgEntity.MessageChain> messageChain = miraiMsgEntity.getMessageChain();
            for (MiraiMsgEntity.MessageChain chain : messageChain) {
                if("Plain".equals(chain.getType())) {
                    if(qqReplyEntity.getReplyMatch() == 1) {
                        // 完整匹配
                        if(qqReplyEntity.getReplyKeys().equals(chain.getText())) {
                            match = true;
                        }
                    } else {
                        // 模糊匹配
                        if(qqReplyEntity.getReplyKeys().indexOf(chain.getText()) != -1) {
                            match = true;
                        }
                    }
                }
            }
            if(!match) {
                continue;
            }
            // 发送消息
            // 消息对象匹配
            if(qqReplyEntity.getReplyTargetPersonal() == 1) {
                // 好友消息匹配
                if("FriendMessage".equals(miraiMsgEntity.getType())) {
                    miraiHttpApi.sendMsg(
                            qqBotEntity.getServerIp(),
                            qqBotEntity.getServerPort(),
                            qqBotEntity.getSessionKey(),
                            MiraiHttpApi.MSG_TYPE_FRIEND,
                            miraiMsgEntity.getSender().getId(),
                            qqReplyEntity.getReplyText(),
                            images
                    );
                    return;
                }
            }
            if(qqReplyEntity.getReplyTargetGroup() == 1) {
                // 群消息消息匹配
                if("GroupMessage".equals(miraiMsgEntity.getType())) {
                    miraiHttpApi.sendMsg(
                            qqBotEntity.getServerIp(),
                            qqBotEntity.getServerPort(),
                            qqBotEntity.getSessionKey(),
                            MiraiHttpApi.MSG_TYPE_GROUP,
                            miraiMsgEntity.getSender().getGroup().getId(),
                            qqReplyEntity.getReplyText(),
                            images
                    );
                    return;
                }
            }
        }
        System.out.println(miraiMsgEntity);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        qqBotEntity.setEnable(2);
        qqBotEntity.setSessionKey("");
        qqBotService.updateById(qqBotEntity);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }
}
