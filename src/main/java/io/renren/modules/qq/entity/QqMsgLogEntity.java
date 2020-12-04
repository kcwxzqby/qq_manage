package io.renren.modules.qq.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * qq消息日志
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-12-02 21:26:57
 */
@Data
@TableName("qq_msg_log")
public class QqMsgLogEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	@TableId
	private Long id;
	/**
	 * 目标号码
	 */
	private Long target;
	/**
	 * 消息类型： 1好友 2群
	 */
	private Integer type;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 发送是否成功: 0失败，1成功
	 */
	private Integer success;
	/**
	 * 失败原因
	 */
	private String cause;
	/**
	 * 创建时间
	 */
	private Date time;
}
