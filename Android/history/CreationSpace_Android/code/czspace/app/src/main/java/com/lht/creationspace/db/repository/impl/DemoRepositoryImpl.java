package com.lht.creationspace.db.repository.impl;

import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.db.model.DemoModel;
import com.lht.creationspace.db.repository.DemoRepository;

/**
 * <p><b>Package:</b> com.lht.creationspace.db.repository.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> DemoRepositoryImpl </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public class DemoRepositoryImpl extends BaseRepository<DemoModel>
implements DemoRepository{
    public DemoRepositoryImpl() {
        super(DemoModel.class);
    }

    @Override
    protected String getDatabaseName() {
        return "Test.db";
    }
}
