package com.lht.creationspace.base.domain.interactors;

import android.support.annotation.NonNull;

import com.lht.creationspace.base.domain.repository.BaseRepository;
import com.lht.creationspace.base.domain.repository.Repository;

import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.interactors </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> DbInteractorFactory </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public class DbInteractorFactory<Model> {

    public static class NumKeyDbInteractorFactory<Model> extends DbInteractorFactory<Model> {
        private BaseRepository.NumKeyDbRepository<Model> numKeyRepository;

        protected NumKeyDbInteractorFactory(BaseRepository.NumKeyDbRepository<Model> repository) {
            super(repository);
            this.numKeyRepository = repository;
        }

        public final AbsDbInteractor<Model> newQueryByIdInteractor(final long key,
                                                                   BaseRepository.OnQueryTaskFinishListener<Model> listener) {
            return new AbsDbInteractor<Model>(listener) {
                @Override
                protected Model runTask() {
                    return numKeyRepository.queryById(key);
                }
            };
        }
    }

    public static class StringKeyDbInteractorFactory<Model> extends DbInteractorFactory<Model> {
        private BaseRepository.StringKeyDbRepository<Model> stringKeyRepository;

        protected StringKeyDbInteractorFactory(BaseRepository.StringKeyDbRepository<Model> repository) {
            super(repository);
            this.stringKeyRepository = repository;
        }

        public final AbsDbInteractor<Model> newQueryByIdInteractor(final String key,
                                                                   BaseRepository.OnQueryTaskFinishListener<Model> listener) {
            return new AbsDbInteractor<Model>(listener) {
                @Override
                protected Model runTask() {
                    return stringKeyRepository.queryById(key);
                }
            };
        }
    }

    ////////##############################################
    ////////##############################################


    private Repository<Model> repository;

    private DbInteractorFactory(Repository<Model> repository) {
        this.repository = repository;
    }

    public static <M> DbInteractorFactory<M>
    getInstance(@NonNull Repository<M> repository) {
        return new DbInteractorFactory<>(repository);
    }

    public static <M> NumKeyDbInteractorFactory<M>
    getNumKDIFInstance(@NonNull BaseRepository.NumKeyDbRepository<M> repository) {
        return new NumKeyDbInteractorFactory<>(repository);
    }

    public static <M> StringKeyDbInteractorFactory<M>
    getStringKDIFInstance(@NonNull BaseRepository.StringKeyDbRepository<M> repository) {
        return new StringKeyDbInteractorFactory<>(repository);
    }

    public final AbsDbInteractor<Void> newSaveOrUpdateInteractor(final Model model) {
        return new AbsDbInteractor<Void>() {
            @Override
            protected Void runTask() {
                return repository.saveOrUpdate(model);
            }
        };
    }

    public final AbsDbInteractor<Void> newSaveOrUpdateInteractor(final Model model,
                                                                 BaseRepository.SimpleOnTaskFinishListener listener) {
        return new AbsDbInteractor<Void>(listener) {
            @Override
            protected Void runTask() {
                return repository.saveOrUpdate(model);
            }
        };
    }

    public final AbsDbInteractor<List<Model>> newQueryAllInteractor(AbsDbInteractor.OnTaskFinishListener<List<Model>> listener) {
        return new AbsDbInteractor<List<Model>>(listener) {
            @Override
            protected List<Model> runTask() {
                return repository.queryAll();
            }
        };
    }

    public final AbsDbInteractor<Void> newDeleteAllInteractor(BaseRepository.SimpleOnTaskFinishListener listener) {
        return new AbsDbInteractor<Void>(listener) {
            @Override
            protected Void runTask() {
                return repository.deleteAll();
            }
        };
    }


    public final AbsDbInteractor<Void> newDeleteInteractor(final Model model) {
        return new AbsDbInteractor<Void>() {
            @Override
            protected Void runTask() {
                return repository.delete(model);
            }
        };
    }

    public final AbsDbInteractor<Void> newDeleteInteractor(final Model model,
                                                           BaseRepository.SimpleOnTaskFinishListener listener) {
        return new AbsDbInteractor<Void>(listener) {
            @Override
            protected Void runTask() {
                return repository.delete(model);
            }
        };
    }


}
