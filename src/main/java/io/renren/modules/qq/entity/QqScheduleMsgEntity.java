package io.renren.modules.qq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * qq定时发送消息
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-04 00:21:29
 */
@Data
@TableName("qq_schedule_msg")
public class QqScheduleMsgEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 计划名称
	 */
	private String name;
	/**
	 * 消息类型
	 */
	private Integer type;
	/**
	 * 机器人ID
	 */
	private Long botId;
	/**
	 * 发送消息对象
	 */
	private Long target;
	/**
	 * 消息文本
	 */
	private String text;
	/**
	 * 自定义消息接口
	 */
	private String api;
	/**
	 * 自定义消息接口参数
	 */
	private String json;
	/**
	 * cron表达式
	 */
	private String cron;
	/**
	 * 任务创建时间
	 */
	private Date time;

	@TableField(exist = false)
	private transient List<QqReplyEntity.Image> images = new ArrayList<>();
}
