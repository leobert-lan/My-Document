package com.lht.creationspace.module.proj.interactors;

import com.lht.creationspace.base.domain.interactors.AbsDbInteractor;
import com.lht.creationspace.base.domain.interactors.DbInteractorFactory;
import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.module.proj.model.ProjTypeDbModel;
import com.lht.creationspace.module.proj.repository.ProjTypeRepository;

/**
 * <p><b>Package:</b> com.lht.creationspace.module.proj.interactors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ProjTypeInteractorFactory </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public class ProjTypeInteractorFactory
        extends DbInteractorFactory.NumKeyDbInteractorFactory<ProjTypeDbModel> {

    private ProjTypeRepository repository;

    private ProjTypeInteractorFactory(ProjTypeRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public static ProjTypeInteractorFactory
    getInstance(ProjTypeRepository repository) {
        return new ProjTypeInteractorFactory(repository);
    }

    public AbsDbInteractor<ProjTypeDbModel>
    newQueryLastInteractor(BaseRepository.OnQueryTaskFinishListener<ProjTypeDbModel>
                                   listener) {
        return new AbsDbInteractor<ProjTypeDbModel>(listener) {
            @Override
            protected ProjTypeDbModel runTask() {
                return repository.queryLast();
            }
        };
    }
}
