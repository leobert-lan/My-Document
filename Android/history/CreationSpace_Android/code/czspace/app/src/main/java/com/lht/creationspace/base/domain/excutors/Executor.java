package com.lht.creationspace.base.domain.excutors;

import com.lht.creationspace.base.domain.interactors.AbsInteractor;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.excutors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> Executor </p>
 * <p><b>Description:</b> Backend job executor </p>
 * Created by leobert on 2017/8/4.
 */

public interface Executor {
    void execute(final AbsInteractor absInteractor);
}
