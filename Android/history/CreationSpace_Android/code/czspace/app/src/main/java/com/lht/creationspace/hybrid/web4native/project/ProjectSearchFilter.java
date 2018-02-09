package com.lht.creationspace.hybrid.web4native.project;

import com.alibaba.fastjson.JSON;
import com.lht.creationspace.hybrid.web4native.AbsSimpleWebRequest;
import com.lht.creationspace.hybrid.web4native.BaseWebFuncReqBean;

/**
 * <p><b>Package:</b> com.lht.creationspace.web4native.project </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjectSearchFilter </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/3/9.
 */

public class ProjectSearchFilter extends AbsSimpleWebRequest
        implements IJSFuncCollection.ISetProjectFilter {

    private final int pid;

    public ProjectSearchFilter(int pid) {
        this.pid = pid;
    }

    @Override
    public String getReqWebHandlerName() {
        return API_NAME;
    }

    @Override
    public String getReqData() {
        ReqData data = new ReqData();
        data.setPid(String.valueOf(pid));
        return JSON.toJSONString(data);
    }


    private static final class ReqData extends BaseWebFuncReqBean{
        private String pid;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }
    }

}
