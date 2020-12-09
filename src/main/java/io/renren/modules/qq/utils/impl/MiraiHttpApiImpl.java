package io.renren.modules.qq.utils.impl;

import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;
import io.renren.modules.qq.entity.QqMsgLogEntity;
import io.renren.modules.qq.service.QqMsgLogService;
import io.renren.modules.qq.utils.MiraiHttpApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Classname MiraiHttpApiImpl
 * @Description TODO
 * @Date 2020/11/30 0030 下午 5:15
 * @Created by Administrator
 */
@Component("miraiHttpApi")
public class MiraiHttpApiImpl implements MiraiHttpApi {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private QqMsgLogService qqMsgLogService;

    private String formatServer(String server, String port, String path) {
        return "http://" + (server.endsWith("/") ? server.substring(0, server.length() - 1) : server + ":") + port + path;
    }

    @Override
    public String authentication(String server, String port, String authKey) {
        Map map = new HashMap();
        map.put("authKey", authKey);

        HttpEntity<Map> params = new HttpEntity<>(map);

        Map<String, Object> re = restTemplate.postForObject(formatServer(server, port, "/auth"), params, Map.class);
        return re.get("code") == Integer.valueOf(0) ? (String) re.get("session") : "";
    }

    @Override
    public boolean authorization(String server, String port, String qq, String sessionKey) {
        Map map = new HashMap();
        map.put("qq", qq);
        map.put("sessionKey", sessionKey);

        HttpEntity<Map> params = new HttpEntity<>(map);

        Map<String, Object> re = restTemplate.postForObject(formatServer(server, port, "/verify"), params, Map.class);
        return re.get("code") == Integer.valueOf(0) ? true : false;
    }

    @Override
    public boolean openClientWebsocket(String server, String port, String sessionKey) {
        Map map = new HashMap();
        map.put("sessionKey", sessionKey);
        map.put("cacheSize", 4096);
        map.put("enableWebsocket", true);

        HttpEntity<Map> params = new HttpEntity<>(map);

        Map<String, Object> re = restTemplate.postForObject(formatServer(server, port, "/config"), params, Map.class);
        return re.get("code") == Integer.valueOf(0) ? true : false;
    }

    @Override
    public boolean releaseSession(String server, String port, String qq, String sessionKey) {
        Map map = new HashMap();
        map.put("qq", qq);
        map.put("sessionKey", sessionKey);

        HttpEntity<Map> params = new HttpEntity<>(map);

        Map<String, Object> re = restTemplate.postForObject(formatServer(server, port, "/release"), params, Map.class);
        return re.get("code") == Integer.valueOf(0) ? true : false;
    }

    @Override
    public List<QqGroup> getGroups(String server, String port, String sessionKey) {
        List<QqGroup> groups = null;
        try {
            groups = restTemplate.getForObject(formatServer(server, port, "/groupList?sessionKey=" + sessionKey), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public List<QqFriend> getFriends(String server, String port,String sessionKey) {
        List<QqFriend> qqFriends = null;
        try {
            qqFriends = restTemplate.getForObject(formatServer(server, port, "/friendList?sessionKey=" + sessionKey), List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qqFriends;
    }

    @Override
    public void sendMsg(String server, String port, String sessionKey, int msgType, Long target, String content, List<String> images) {
        Map map = new HashMap();
        map.put("target", target);
        map.put("sessionKey", sessionKey);
        // 添加发送内容
        List<Map<String, String>> messageChain = new ArrayList<>();
        // 添加文本内容
        Map c = new HashMap();
        c.put("type", "Plain");
        c.put("text", content.replaceAll("#换行#", "\n"));
        messageChain.add(c);
        // 添加发送图片
        Map i = new HashMap();
        if(images != null) {
            for (String image : images) {
                i = new HashMap();
                i.put("type", "Image");
                i.put("url", image);
                messageChain.add(i);
            }
        }

        map.put("messageChain", messageChain);
        HttpEntity<Map> params = new HttpEntity<>(map);
        QqMsgLogEntity qqMsgLogEntity = new QqMsgLogEntity();
        qqMsgLogEntity.setType(msgType);
        qqMsgLogEntity.setContent(content);
        qqMsgLogEntity.setTarget(target);
        qqMsgLogEntity.setTime(new Date());
        try {
            if(msgType == MiraiHttpApi.MSG_TYPE_FRIEND) {
                Map resultMap = restTemplate.postForObject(formatServer(server, port, "/sendFriendMessage"), params, Map.class);
                if(resultMap.get("code") == (Integer)0) {
                    qqMsgLogEntity.setSuccess(1);
                } else {
                    qqMsgLogEntity.setSuccess(0);
                    qqMsgLogEntity.setCause((String) resultMap.get("msg"));
                }
            } else if (msgType == MiraiHttpApi.MSG_TYPE_GROUP) {
                Map resultMap = restTemplate.postForObject(formatServer(server, port, "/sendGroupMessage"), params, Map.class);
                if(resultMap.get("code") == (Integer)0) {
                    qqMsgLogEntity.setSuccess(1);
                } else {
                    qqMsgLogEntity.setSuccess(0);
                    qqMsgLogEntity.setCause((String) resultMap.get("msg"));
                }
            }
            qqMsgLogService.saveQqMsgLog(qqMsgLogEntity, images);
        } catch (Exception e) {
            qqMsgLogEntity.setSuccess(0);
            qqMsgLogEntity.setCause("机器人不在线");
        } finally {
            qqMsgLogService.saveQqMsgLog(qqMsgLogEntity, images);
        }
    }

}
