package io.renren.modules.qq.entity;

import lombok.Data;

import java.util.List;

/**
 * @Classname MiraiMsgEntity
 * @Description TODO
 * @Date 2020/12/1 0001 下午 4:06
 * @Created by Administrator
 */
@Data
public class MiraiMsgEntity {

    // 消息类型：GroupMessage或FriendMessage或TempMessage或各类Event
    private String type;

    // 消息链，是一个消息对象构成的数组
    private List<MessageChain> messageChain;

    // 发送者信息
    private Sender sender;

    @Data
    public static
    class MessageChain {
        private Long id;
        private String type;
        private Long time;
        private String text;
        private Long target;
        private String display;
        private String imageId;
        private String url;
        private String path;
    }

    @Data
    public static
    class Sender {

        // 发送者的QQ号码
        private Long id;

        // 发送者的群名片
        private String memberName;

        // 发送者的群限权：OWNER、ADMINISTRATOR或MEMBER
        private String permission;

        // 消息发送群的信息
        private Group group;

    }

    @Data
    public static
    class Group {

        // 发送群的群号
        private Long id;

        // 发送群的群名称
        private String name;

        // 发送群中，Bot的群限权
        private String permission;

    }


}
