package com.lht.creationspace.base.model.apimodel;


/**
 * <p><b>Package</b> com.lht.vsocyy.mvp.model.bean
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> BaseVsoApiResBean
 * <p><b>Description</b>: TODO
 * Created by leobert on 2016/7/11.
 */
public class BaseVsoApiResBean {

    /**
     * 未登录 9003
     */
    public static final int RET_ERROR_UNLOGIN = 9003;

    /**
     * 检验失败 9004
     */
    public static final int RET_ERROR_AUTH_FAILURE = 9004;

    /**
     * token超时 9005
     */
    public static final int RET_ERROR_AUTH_OVERDUE = 9005;


    /**
     * 9008
     * token非法
     */
    public static final int RET_ERROR_AUTH_INVALID = 9008;


    /**
     * 9009
     * token超限
     */
    public static final int RET_ERROR_AUTH_OUTRANGE = 9009;

    /**
     * 9014
     * 您无权进行此操作，请先登录
     */
    public static final int RET_ERROR_AUTH_UNLOGIN2 = 9014;

    /**
     * 验证码过期
     */
    public static final int RET_ERROR_VALIDCODE = 13145;

    /**
     * 业务内部返回码
     */
    private int ret;

    /**
     * 业务处理状态，0 - 处理失败， 1 - 处理成功
     */
    private int status;

    private String message;

    private String data;

    /**
     * 网络错误级别的message
     */
    private String msg;

    public static final int STATUS_FAILURE = 0;

    public static final int STATUS_SUCCESS = 1;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        if (ret == RET_ERROR_VALIDCODE)
            return "验证码错误，请重新输入";
        if (message != null)
            return message;
        else
            return getMsg();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return status == STATUS_SUCCESS;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isAuthPermission() {
        switch (ret) {
            case RET_ERROR_UNLOGIN:
                return false;
            case RET_ERROR_AUTH_FAILURE:
                return false;
            case RET_ERROR_AUTH_OVERDUE:
                return false;
            case RET_ERROR_AUTH_INVALID:
                return false;
            case RET_ERROR_AUTH_OUTRANGE:
                return false;
            case RET_ERROR_AUTH_UNLOGIN2:
                return false;
            default:
                return true;
        }
    }
}
