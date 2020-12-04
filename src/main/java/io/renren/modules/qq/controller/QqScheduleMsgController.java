package io.renren.modules.qq.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.modules.qq.entity.QqScheduleMsgEntity;
import io.renren.modules.qq.service.QqScheduleMsgService;
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
 * qq定时发送消息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-04 00:21:29
 */
@RestController
@RequestMapping("generator/qqschedulemsg")
public class QqScheduleMsgController {
    @Autowired
    private QqScheduleMsgService qqScheduleMsgService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:qqschedulemsg:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = qqScheduleMsgService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:qqschedulemsg:info")
    public R info(@PathVariable("id") Long id){
		QqScheduleMsgEntity qqScheduleMsg = qqScheduleMsgService.getQqScheduleMsgEntityById(id);

        return R.ok().put("qqScheduleMsg", qqScheduleMsg);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("generator:qqschedulemsg:save")
    public R save(@RequestBody QqScheduleMsgEntity qqScheduleMsg){
		qqScheduleMsgService.saveQqScheduleMsgEntity(qqScheduleMsg);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:qqschedulemsg:update")
    public R update(@RequestBody QqScheduleMsgEntity qqScheduleMsg){
		qqScheduleMsgService.updateQqScheduleMsgEntity(qqScheduleMsg);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:qqschedulemsg:delete")
    public R delete(@RequestBody Long[] ids){
		qqScheduleMsgService.deleteQqScheduleMsgByIds(ids);

        return R.ok();
    }

}
