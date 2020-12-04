package io.renren.modules.qq.utils;


import io.renren.modules.qq.entity.QqBotEntity;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @Classname MiraiThread
 * @Description TODO
 * @Date 2020/11/30 0030 下午 9:37
 * @Created by Administrator
 */

@Deprecated
@Data
@Component("miraiTask")
@Scope("prototype")
public class MiraiTask extends Thread {

    private QqBotEntity qqBotEntity;

    @Override
    public void run() {
//        MiraiWebSocket miraiWebSocket = null;
//        try {
//            miraiWebSocket = new MiraiWebSocket(new URI("ws://" + qqBotEntity.getServerIp() + ":" + qqBotEntity.getServerPort()
//                    + "/message?sessionKey=" + qqBotEntity.getSessionKey()));
//            miraiWebSocket.connect();
//
//            while (!Thread.currentThread().isInterrupted()) {
//                TimeUnit.SECONDS.sleep(5);
//                System.out.println("T: " + Thread.currentThread().getName() + "  ,sessionKey: " + qqBotEntity.getSessionKey());
//            }
//        } catch (Exception e) {
//            miraiWebSocket.close();
//            Thread.currentThread().interrupt();
//            e.printStackTrace();
//        }

    }
}
