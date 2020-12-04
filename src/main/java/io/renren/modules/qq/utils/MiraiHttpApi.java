package io.renren.modules.qq.utils;

import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;

import java.util.List;

/**
 * @Classname MiraiHttpApi
 * @Description MiraiHttpApi工具类
 * @Date 2020/11/30 0030 下午 5:06
 * @Created by Administrator
 */
public interface MiraiHttpApi {

    static final int MSG_TYPE_FRIEND = 1;

    static final int MSG_TYPE_GROUP = 2;

    String authentication(String server, String port, String authKey);

    boolean authorization(String server, String port, String qq, String sessionKey);

    boolean openClientWebsocket(String server, String port, String sessionKey);

    boolean releaseSession(String server, String port, String qq, String sessionKey);

    List<QqGroup> getGroups(String server, String port, String sessionKey);

    List<QqFriend> getFriends(String server, String port, String sessionKey);

    void sendMsg(String server, String port, String sessionKey, int msgType, Long target, String content, List<String> images);

}
