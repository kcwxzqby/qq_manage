package io.renren.modules.qq.controller;

import java.util.Arrays;
import java.util.Map;

import io.renren.modules.qq.entity.QqMsgLogEntity;
import io.renren.modules.qq.service.QqMsgLogService;
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
 * qq消息日志
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-02 21:26:57
 */
@RestController
@RequestMapping("generator/qqmsglog")
public class QqMsgLogController {
    @Autowired
    private QqMsgLogService qqMsgLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("generator:qqmsglog:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = qqMsgLogService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("generator:qqmsglog:info")
    public R info(@PathVariable("id") Long id){
		QqMsgLogEntity qqMsgLog = qqMsgLogService.getById(id);

        return R.ok().put("qqMsgLog", qqMsgLog);
    }

    /**
     * 保存
     */
//    @RequestMapping("/save")
//    @RequiresPermissions("generator:qqmsglog:save")
//    public R save(@RequestBody QqMsgLogEntity qqMsgLog){
//		qqMsgLogService.save(qqMsgLog);
//
//        return R.ok();
//    }

    /**
     * 修改
     */
//    @RequestMapping("/update")
//    @RequiresPermissions("generator:qqmsglog:update")
//    public R update(@RequestBody QqMsgLogEntity qqMsgLog){
//		qqMsgLogService.updateById(qqMsgLog);
//
//        return R.ok();
//    }

    /**
     * 删除
     */
//    @RequestMapping("/delete")
//    @RequiresPermissions("generator:qqmsglog:delete")
//    public R delete(@RequestBody Long[] ids){
//		qqMsgLogService.removeByIds(Arrays.asList(ids));
//
//        return R.ok();
//    }

}
