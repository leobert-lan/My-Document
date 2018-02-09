package com.lht.creationspace.base.model.apimodel;

import com.lht.creationspace.base.model.apimodel.IApiRequestModel;

/**
 * <p><b>Package</b> com.lht.vsocyy.interfaces.net
 * <p><b>Project</b> VsoCyy
 * <p><b>Classname</b> IPagedApiRequestModel
 * <p><b>Description</b>: 分页类型的API-Model
 * <p> Create by Leobert on 2016/8/23
 */
public interface IPagedApiRequestModel extends IApiRequestModel {

    void setParams(String usr, int offset);

    void setParams(String usr, int offset, int limit);
}
