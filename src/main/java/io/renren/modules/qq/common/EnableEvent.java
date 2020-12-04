package io.renren.modules.qq.common;

import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqGroup;
import io.renren.modules.qq.service.QqBotService;
import io.renren.modules.qq.utils.Global;
import io.renren.modules.qq.utils.MiraiHttpApi;
import io.renren.modules.qq.utils.MiraiWebSocket;
import org.java_websocket.client.WebSocketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

/**
 * @Classname EnableEvent
 * @Description TODO
 * @Date 2020/12/3 0003 下午 3:43
 * @Created by Administrator
 */
@Component
public class EnableEvent implements CommandLineRunner {

    @Autowired
    private QqBotService qqBotService;

    @Autowired
    private MiraiHttpApi miraiHttpApi;

    @Override
    public void run(String... args) throws Exception {
        for (QqBotEntity qqBotEntity : qqBotService.list()) {
            if(qqBotEntity.getEnable() == 1) {
                if(qqBotEntity.getSessionKey() != null && qqBotEntity.getSessionKey().length() > 0) {
                    List<QqGroup> groups = miraiHttpApi.getGroups(qqBotEntity.getServerIp(), qqBotEntity.getServerPort(), qqBotEntity.getSessionKey());
                    if (groups != null) {
                        WebSocketClient socketClient = new MiraiWebSocket(new URI("ws://" + qqBotEntity.getServerIp() + ":" + qqBotEntity.getServerPort()
                                + "/message?sessionKey=" + qqBotEntity.getSessionKey()), qqBotEntity);
                        socketClient.connect();
                        Global.T_MiraiTask.put(qqBotEntity.getId(), socketClient);
                        continue;
                    }
                }
                qqBotEntity.setEnable(0);
                qqBotEntity.setSessionKey("");
                qqBotService.updateById(qqBotEntity);
            }
        }
    }

}
