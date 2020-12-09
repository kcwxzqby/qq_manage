package io.renren.modules.qq.service.impl;

import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqMsgLogDao;
import io.renren.modules.qq.entity.QqMsgLogEntity;
import io.renren.modules.qq.service.QqMsgLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.transaction.annotation.Transactional;


@Service("qqMsgLogService")
public class QqMsgLogServiceImpl extends ServiceImpl<QqMsgLogDao, QqMsgLogEntity> implements QqMsgLogService {

    @Autowired
    private QqMsgLogDao qqMsgLogDao;

    @Autowired
    private SysOssService sysOssService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QqMsgLogEntity> page = this.page(
                new Query<QqMsgLogEntity>().getPage(params),
                new QueryWrapper<QqMsgLogEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQqMsgLog(QqMsgLogEntity qqMsgLogEntity, List<String> images) {
        if(images != null && save(qqMsgLogEntity)) {
            for (String image : images) {
                SysOssEntity sysOssEntity = sysOssService.getOne(new QueryWrapper<SysOssEntity>().eq("url", image));
                if(sysOssEntity != null) {
                    qqMsgLogDao.saveMsgOss(qqMsgLogEntity.getId(), sysOssEntity.getId());
                }
            }
        } else {
            save(qqMsgLogEntity);
        }
    }


}
