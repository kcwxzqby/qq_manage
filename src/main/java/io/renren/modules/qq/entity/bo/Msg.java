package io.renren.modules.qq.entity.bo;

import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;
import lombok.Data;

import java.util.List;

/**
 * @Classname Msg
 * @Description TODO
 * @Date 2020/12/2 0002 下午 6:57
 * @Created by Administrator
 */
@Data
public class Msg {

    private Long qqId;

    private List<QqFriend> friends;

    private List<QqGroup> groups;

    private String content;

    private List<Long> images;

}
