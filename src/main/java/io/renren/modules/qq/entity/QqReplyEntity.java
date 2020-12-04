package io.renren.modules.qq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 自动回复表
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2020-11-30 15:47:54
 */
@Data
@TableName("qq_reply")
public class QqReplyEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final long REPLY_TYPE_TEXT = 0;
	public static final long REPLY_TYPE_IMAGE = 1;
	public static final long REPLY_TYPE_API = 2;

	/**
	 * 自动回复记录id
	 */
	@TableId(type = IdType.AUTO)
	private Long replyId;
	/**
	 * 回复标题
	 */
	private String replyName;
	/**
	 * 回复类型: 0文本, 1图文, 2自定义接口
	 */
	private Integer replyType;
	/**
	 * 回复关键词
	 */
	private String replyKeys;
	/**
	 * 关键词完整匹配: 0否, 1是
	 */
	private Integer replyMatch;
	/**
	 * 回复文本
	 */
	private String replyText;
	/**
	 * 自定义接口回复,收到消息转发到此API接口
	 */
	private String replyApi;
	/**
	 * 转发消息自定义json参数
	 */
	private String replyApiJson;
	/**
	 * 个人私聊回复：0关闭， 1开启
	 */
	private Integer replyTargetPersonal;
	/**
	 * 群消息回复：0关闭， 1开启
	 */
	private Integer replyTargetGroup;

	@TableField(exist = false)
	private List<Image> images = new ArrayList<>();

	@Data
	@AllArgsConstructor
	public static
	class Image {
		private Long id;
		private String url;
	}
}


