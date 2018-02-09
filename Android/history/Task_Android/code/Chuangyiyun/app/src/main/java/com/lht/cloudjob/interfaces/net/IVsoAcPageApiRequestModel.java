package com.lht.cloudjob.interfaces.net;

/**
 * Created by chhyu on 2016/12/16.
 */

public interface IVsoAcPageApiRequestModel extends IApiRequestModel {

    void setParams(int offset);

    void setParams(int offset, int limit);
}
