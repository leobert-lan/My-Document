package com.lht.creationspace.module.proj.repository.impl;

import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.cfg.DBConfig;
import com.lht.creationspace.module.proj.model.ProjTypeDbModel;
import com.lht.creationspace.module.proj.repository.ProjTypeRepository;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;

/**
 * <p><b>Package:</b> com.lht.creationspace.module.proj.repository.impl </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjTypeRepositoryImpl </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public class ProjTypeRepositoryImpl extends BaseRepository<ProjTypeDbModel>
        implements ProjTypeRepository {
    public ProjTypeRepositoryImpl() {
        super(ProjTypeDbModel.class);
    }

    @Override
    protected String getDatabaseName() {
        return DBConfig.BasicDb.DB_NAME;
    }

    @Override
    public ProjTypeDbModel queryLast() {
        ArrayList<ProjTypeDbModel> beans =
                getConnectedInstance().query(
                        new QueryBuilder<>(ProjTypeDbModel.class)
                                .appendOrderDescBy("createTime")
                                .limit(0, 1));
        getConnectedInstance().close();
        if (beans == null || beans.isEmpty())
            return null;
        else
            return beans.get(0);

    }
}
