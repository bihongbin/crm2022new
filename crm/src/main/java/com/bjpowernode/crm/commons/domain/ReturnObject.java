package com.bjpowernode.crm.commons.domain;

/**
 * @author shkstart
 * @date 2022/5/2 - 15:20
 */
public class ReturnObject {

    private String code;//处理结果返回是否成功信息，1-成功，0-失败
    private String message;//提示信息
    private Object retData;//携带数据

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRetData() {
        return retData;
    }

    public void setRetData(Object retData) {
        this.retData = retData;
    }
}
