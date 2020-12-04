package io.renren.modules.qq.service.impl;

import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqScheduleMsgDao;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.entity.QqScheduleMsgEntity;
import io.renren.modules.qq.service.QqScheduleMsgService;
import io.renren.modules.qq.utils.QqScheduleUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;


@Service("qqScheduleMsgService")
public class QqScheduleMsgServiceImpl extends ServiceImpl<QqScheduleMsgDao, QqScheduleMsgEntity> implements QqScheduleMsgService {

    @Autowired
    private QqScheduleMsgDao qqScheduleMsgDao;

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器
     */
    @PostConstruct
    public void init(){
        List<QqScheduleMsgEntity> scheduleJobList = this.list();
        for(QqScheduleMsgEntity scheduleJob : scheduleJobList){
            CronTrigger cronTrigger = QqScheduleUtils.getCronTrigger(scheduler, scheduleJob.getId());
            //如果不存在，则创建
            if(cronTrigger == null) {
                QqScheduleUtils.createScheduleJob(scheduler, scheduleJob);
            }else {
                QqScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
            }
        }
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QqScheduleMsgEntity> page = this.page(
                new Query<QqScheduleMsgEntity>().getPage(params),
                new QueryWrapper<QqScheduleMsgEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public QqScheduleMsgEntity getQqScheduleMsgEntityById(Long scheduleId) {
        QqScheduleMsgEntity qqScheduleMsgEntity = getById(scheduleId);
        if(qqScheduleMsgEntity.getType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 添加关联图片
            List<Long> ossIds = qqScheduleMsgDao.getScheduleOssIds(scheduleId);
            ossIds.forEach(id -> {
                SysOssEntity oss = sysOssService.getById(id);
                qqScheduleMsgEntity.getImages().add(new QqReplyEntity.Image(oss.getId(), oss.getUrl()));
            });
        }

        return qqScheduleMsgEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQqScheduleMsgEntity(QqScheduleMsgEntity qqScheduleMsg) {
        qqScheduleMsg.setTime(new Date());
        save(qqScheduleMsg);
        if(qqScheduleMsg.getType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 保存图片
            qqScheduleMsg.getImages().forEach(image -> {
                qqScheduleMsgDao.saveScheduleOss(qqScheduleMsg.getId(), image.getId());
            });
        }

        QqScheduleUtils.createScheduleJob(scheduler, qqScheduleMsg);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateQqScheduleMsgEntity(QqScheduleMsgEntity qqScheduleMsg) {
        updateById(qqScheduleMsg);
        if(qqScheduleMsg.getType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 保存图片
            qqScheduleMsgDao.deleteScheduleOssById(qqScheduleMsg.getId());
            qqScheduleMsg.getImages().forEach(image -> {
                qqScheduleMsgDao.saveScheduleOss(qqScheduleMsg.getId(), image.getId());
            });
        }

        QqScheduleUtils.updateScheduleJob(scheduler, qqScheduleMsg);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteQqScheduleMsgByIds(Long[] ids) {
        for (Long id : ids) {
            QqScheduleUtils.deleteScheduleJob(scheduler, id);
        }

        this.removeByIds(Arrays.asList(ids));
    }

}
