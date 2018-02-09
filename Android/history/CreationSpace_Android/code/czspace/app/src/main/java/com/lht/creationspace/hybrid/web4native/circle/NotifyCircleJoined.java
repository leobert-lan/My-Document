package com.lht.creationspace.hybrid.web4native.circle;

import com.lht.creationspace.hybrid.web4native.AbsSimpleWebRequest;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native.project </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjectSearchFilter </p>
 * <p><b>Description:</b> TODO </p> 暂未使用
 * Created by leobert on 2017/3/9.
 */
@Deprecated
public class NotifyCircleJoined extends AbsSimpleWebRequest
        implements IJSFuncCollection.INotifyCircleJoined {

    private final int circleId;

    private final String username;

    public NotifyCircleJoined(int circleId, String username) {
        this.circleId = circleId;
        this.username = username;
    }

    @Override
    public String getReqWebHandlerName() {
        return API_NAME;
    }

//    @Override
//    public String getReqData() {
//        ReqData data = new ReqData();
//        data.setPid(String.valueOf(pid));
//        return JSON.toJSONString(data);
//    }
//
//
//    private static final class ReqData extends BaseWebFuncReqBean{
//        private String pid;
//
//        public String getPid() {
//            return pid;
//        }
//
//        public void setPid(String pid) {
//            this.pid = pid;
//        }
//    }

}
