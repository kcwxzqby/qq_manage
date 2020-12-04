package io.renren.modules.qq.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqFriend;
import io.renren.modules.qq.entity.QqGroup;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.entity.bo.Msg;
import io.renren.modules.qq.service.QqBotService;
import io.renren.modules.qq.service.QqReplyService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;



/**
 * qq机器人表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@RestController
@RequestMapping("generator/qqbot")
public class QqBotController {
    @Autowired
    private QqBotService qqBotService;

    @Autowired
    private QqReplyService qqReplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:qqbot:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = qqBotService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 全部列表
     */
    @RequestMapping("/all")
    @RequiresPermissions("generator:qqbot:list")
    public R all(@RequestParam Map<String, Object> params){
        List<QqBotEntity> list = qqBotService.list();

        return R.ok().put("list", list);
    }

    /**
     * 全部群以及好友
     */
    @RequestMapping("/allGroupsAndFriends")
    @RequiresPermissions("generator:qqbot:list")
    public R allGroupsAndFriends(@RequestParam Long qqId){
        List<QqGroup> groups = qqBotService.getGroups(qqId);
        List<QqFriend> friends = qqBotService.getFriends(qqId);

        return R.ok().put("groups", groups).put("friends", friends);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:qqbot:info")
    public R info(@PathVariable("id") Long id){
		QqBotEntity qqBot = qqBotService.getQqBotInfo(id);
        List<QqReplyEntity> list = qqReplyService.list();

        return R.ok().put("qqBot", qqBot).put("replyList", list);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:qqbot:save")
    public R save(@RequestBody QqBotEntity qqBot) throws Exception {
		qqBotService.saveQqBot(qqBot);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:qqbot:update")
    public R update(@RequestBody QqBotEntity qqBot) throws Exception {
		qqBotService.updateQqBot(qqBot);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:qqbot:delete")
    public R delete(@RequestBody Long[] ids){
        for (QqBotEntity qqBotEntity : qqBotService.listByIds(Arrays.asList(ids))) {
            if(qqBotEntity.getEnable() == 1) {
                return R.error("请先停止ID=" + qqBotEntity.getId() + "的机器人");
            }
        }
		qqBotService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    /**
     * 发送消息
     */
    @RequestMapping("/sendMsg")
    @RequiresPermissions("generator:qqbot:info")
    public R sendMsg(@RequestBody Msg msg){
        qqBotService.sendMsg(msg);

        return R.ok();
    }

}
