package io.renren.modules.qq.utils;

import io.renren.common.utils.SpringContextUtils;
import io.renren.modules.job.entity.ScheduleJobEntity;
import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqScheduleMsgDao;
import io.renren.modules.qq.entity.QqBotEntity;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.entity.QqScheduleMsgEntity;
import io.renren.modules.qq.service.QqBotService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @Classname QqScheduleJob
 * @Description TODO
 * @Date 2020/12/4 0004 上午 1:23
 * @Created by Administrator
 */
public class QqScheduleJob extends QuartzJobBean {

    private MiraiHttpApi miraiHttpApi;

    private QqBotService qqBotService;

    private SysOssService sysOssService;

    private QqScheduleMsgDao qqScheduleMsgDao;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        miraiHttpApi = (MiraiHttpApi) SpringContextUtils.getBean("miraiHttpApi");
        qqBotService = (QqBotService) SpringContextUtils.getBean("qqBotService");
        sysOssService = (SysOssService) SpringContextUtils.getBean("sysOssService");
        qqScheduleMsgDao = (QqScheduleMsgDao) SpringContextUtils.getBean("qqScheduleMsgDao");

        QqScheduleMsgEntity qqScheduleMsg = (QqScheduleMsgEntity) context.getMergedJobDataMap().get("qqScheduleMsg");

        //任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            //执行任务
            logger.info("任务准备执行，任务ID：" + qqScheduleMsg.getId());

            List images = null;

            if (qqScheduleMsg.getType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
                images = new ArrayList();
                List<Long> ossIds = qqScheduleMsgDao.getScheduleOssIds(qqScheduleMsg.getId());
                for (SysOssEntity oss : sysOssService.listByIds(ossIds)) {
                    images.add(oss.getUrl());
                }
            }

            QqBotEntity qqBotEntity = qqBotService.getById(qqScheduleMsg.getBotId());
            if(qqBotEntity == null) {
                logger.error("QQ机器人不存在");
            } else {
                miraiHttpApi.sendMsg(
                        qqBotEntity.getServerIp(),
                        qqBotEntity.getServerPort(),
                        qqBotEntity.getSessionKey(),
                        MiraiHttpApi.MSG_TYPE_FRIEND,
                        qqScheduleMsg.getTarget(),
                        qqScheduleMsg.getText(),
                        images
                );
                miraiHttpApi.sendMsg(
                        qqBotEntity.getServerIp(),
                        qqBotEntity.getServerPort(),
                        qqBotEntity.getSessionKey(),
                        MiraiHttpApi.MSG_TYPE_GROUP,
                        qqScheduleMsg.getTarget(),
                        qqScheduleMsg.getText(),
                        images
                );
            }

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            logger.info("任务执行完毕，任务ID：" + qqScheduleMsg.getId() + "  总共耗时：" + times + "毫秒");
        } catch (Exception e) {
            logger.info("任务执行失败，任务ID：" + qqScheduleMsg.getId(), e);
        }finally {
        }
    }

}
