package io.renren.modules.qq.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.modules.qq.entity.QqReplyEntity;
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
 * 自动回复表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@RestController
@RequestMapping("generator/qqreply")
public class QqReplyController {
    @Autowired
    private QqReplyService qqReplyService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:qqreply:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = qqReplyService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{replyId}")
    @RequiresPermissions("generator:qqreply:info")
    public R info(@PathVariable("replyId") Long replyId){
		QqReplyEntity qqReply = qqReplyService.getQqReplyEntityById(replyId);

        return R.ok().put("qqReply", qqReply);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:qqreply:save")
    public R save(@RequestBody QqReplyEntity qqReply){
		qqReplyService.saveQqReplyEntity(qqReply);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:qqreply:update")
    public R update(@RequestBody QqReplyEntity qqReply){
		qqReplyService.updateQqReplyEntity(qqReply);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:qqreply:delete")
    public R delete(@RequestBody Long[] replyIds){
		qqReplyService.removeByIds(Arrays.asList(replyIds));

        return R.ok();
    }

}
