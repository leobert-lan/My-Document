package com.lht.creationspace.hybrid.native4js.expandreqbean;

/**
 * <p><b>Package:</b> com.lht.creationspace.native4js.expandreqbean </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> NF_ProjectDetailRedirectReqBean </p>
 * <p><b>Description:</b> 项目详情跳转请求参数 </p>
 * Created by leobert on 2017/3/8.
 */

public class NF_ProjectDetailNavigateReqBean extends NF_GeneralNavigateReqBean {

    private String oid;

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
