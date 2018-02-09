package com.lht.cloudjob.interfaces.net;

/**
 * <p><b>Package</b> com.lht.cloudjob.interfaces.net
 * <p><b>Project</b> Chuangyiyun
 * <p><b>Classname</b> IPagedApiRequestModel
 * <p><b>Description</b>: 分页类型的API-Model
 * <p> Create by Leobert on 2016/8/23
 */
public interface IPagedApiRequestModel extends IApiRequestModel {

    void setParams(String usr, int offset);

    void setParams(String usr, int offset, int limit);
}
