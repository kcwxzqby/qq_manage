package io.renren.modules.qq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * qq机器人表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@Data
@TableName("qq_bot")
public class QqBotEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 唯一ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;
	/**
	 * 机器人号码
	 */
	private String qqId;
	/**
	 * 机器人IP
	 */
	private String serverIp;
	/**
	 * 机器人端口
	 */
	private String serverPort;
	/**
	 * 认证Key
	 */
	private String authKey;
	/**
	 * 会话Key
	 */
	@JsonIgnore
	private String sessionKey;
	/**
	 * 是否启动 0关闭 1启动
	 */
	private Integer enable;

	@TableField(exist = false)
	private List<QqReplyEntity> replyList;

}
