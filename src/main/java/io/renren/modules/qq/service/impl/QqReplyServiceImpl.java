package io.renren.modules.qq.service.impl;

import io.renren.modules.oss.entity.SysOssEntity;
import io.renren.modules.oss.service.SysOssService;
import io.renren.modules.qq.dao.QqReplyDao;
import io.renren.modules.qq.entity.QqReplyEntity;
import io.renren.modules.qq.service.QqReplyService;
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


@Service("qqReplyService")
public class QqReplyServiceImpl extends ServiceImpl<QqReplyDao, QqReplyEntity> implements QqReplyService {

    @Autowired
    private SysOssService sysOssService;

    @Autowired
    private QqReplyDao qqReplyDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<QqReplyEntity> page = this.page(
                new Query<QqReplyEntity>().getPage(params),
                new QueryWrapper<QqReplyEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public QqReplyEntity getQqReplyEntityById(Long replyId) {
        QqReplyEntity qqReplyEntity = getById(replyId);
        if(qqReplyEntity.getReplyType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 添加关联图片
            List<Long> replyOss = qqReplyDao.getReplyOssIds(replyId);
            replyOss.forEach(id -> {
                SysOssEntity oss = sysOssService.getById(id);
                qqReplyEntity.getImages().add(new QqReplyEntity.Image(oss.getId(), oss.getUrl()));
            });
        }

        return qqReplyEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveQqReplyEntity(QqReplyEntity qqReply) {
        save(qqReply);
        if(qqReply.getReplyType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 保存图片
            qqReply.getImages().forEach(image -> {
                qqReplyDao.saveReplyOss(qqReply.getReplyId(), image.getId());
            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateQqReplyEntity(QqReplyEntity qqReply) {
        updateById(qqReply);
        if(qqReply.getReplyType() == QqReplyEntity.REPLY_TYPE_IMAGE) {
            // 保存图片
            qqReplyDao.deleteReplyOssById(qqReply.getReplyId());
            qqReply.getImages().forEach(image -> {
                qqReplyDao.saveReplyOss(qqReply.getReplyId(), image.getId());
            });
        }
    }

}
