package com.akaxin.site.web.admin.exception;

public class UserException extends Exception {
    //异常的描述信息
    private String msgDes;

    public UserException(String message, String msgDes) {
        super(message);
        this.msgDes = msgDes;
    }

    public UserException(String msgDes) {
        this.msgDes = msgDes;
    }

    public String getMsgDes() {
        return msgDes;
    }
}
