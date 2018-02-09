package com.lht.lhtwebviewlib.business.bean;

/**
 * @ClassName: BaseResponseBean
 * @Description: Native返回JS的基本数据结构
 * @date 2016年2月23日 上午9:16:47
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class BaseResponseBean<T> {

    public static final int STATUS_SUCCESS = 1;

    public static final int STATUS_FAILURE = 0;

	public static final String  MSG_DEFAULT = "";

	/**
	 * ret:返回码
	 */
	private int ret;

	/**
	 *
	 */
	private int status;

	/**
	 * msg:扩展消息
	 */
	private String msg = MSG_DEFAULT;

	/**
	 * data:业务处理结果
	 */
	private T data;

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

    public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
