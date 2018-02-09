package com.lht.creationspace.db.repository.impl;

import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.cfg.DBConfig;
import com.lht.creationspace.db.model.DesktopBadgeNumModel;
import com.lht.creationspace.db.repository.DesktopBadgeNumRepository;

/**
 * <p><b>Package:</b> com.lht.creationspace.db.repository.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> DesktopBadgeNumRespositoryImpl </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public class DesktopBadgeNumRepositoryImpl
        extends BaseRepository<DesktopBadgeNumModel>
        implements DesktopBadgeNumRepository {
    public DesktopBadgeNumRepositoryImpl() {
        super(DesktopBadgeNumModel.class);
    }

    @Override
    protected String getDatabaseName() {
        return DBConfig.BadgeNumberDb.DB_NAME;
    }
}
