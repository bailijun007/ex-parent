/**
 * @author corleone
 * @date 2018/8/14 0014
 */
package com.hp.sh.expv3.match.msg;

import java.io.Serializable;

public class BaseMessageDto implements Serializable {

    /**
     * search EventMsgTypeEnum
     */
    private int msgType;

    private String messageId;

    public String getMessageId() {
        return messageId;
    }

    public BaseMessageDto setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public BaseMessageDto setMsgType(int msgType) {
        this.msgType = msgType;
        return this;
    }
}
