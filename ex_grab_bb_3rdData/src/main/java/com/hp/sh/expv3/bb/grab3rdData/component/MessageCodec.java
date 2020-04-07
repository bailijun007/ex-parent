package com.hp.sh.expv3.bb.grab3rdData.component;

/**
 * 消息编解码器
 * @author wangjg
 * @param <D> 数据类型
 */
public interface MessageCodec {

	Object encode(Object msg);

	public Object decode(Object data);
}
