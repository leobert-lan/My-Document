package com.lht.creationspace.module.user.info.ui;

import com.lht.creationspace.base.vinterface.IActivityAsyncProtected;
import com.lht.creationspace.module.user.info.model.pojo.ThirdInfoListResBean;

/**
 * Created by chhyu on 2017/2/22.
 */

public interface IThirdAccountActivity extends IActivityAsyncProtected {
    void updateBindStateDisplay(ThirdInfoListResBean data);

    void updateView(int type);
}
