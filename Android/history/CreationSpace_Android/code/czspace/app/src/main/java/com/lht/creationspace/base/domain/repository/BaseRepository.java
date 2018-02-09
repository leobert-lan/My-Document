package com.lht.creationspace.base.domain.repository;

import android.support.annotation.NonNull;

import com.lht.creationspace.base.MainApplication;
import com.lht.creationspace.base.domain.interactors.AbsDbInteractor;

import java.util.Collection;
import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.domain.repository </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> BaseRespository </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/8/4.
 */

public abstract class BaseRepository<Model> implements Repository<Model> {

    protected abstract String getDatabaseName();

    private final Class<Model> modelClass;

    public BaseRepository(Class<Model> model) {
        this.modelClass = model;
    }

    private LiteOrmContext liteOrmContext;

    protected final void connect() {
//        liteOrmContext =  LiteOrm.newSingleInstance(MainApplication.getOurInstance(),
//                getDatabaseName());
        liteOrmContext = LiteOrmContext.getLiteOrmContext(MainApplication.getOurInstance(),
                getDatabaseName(), LiteOrmContext.SINGLE);
    }

    protected final LiteOrmContext getConnectedInstance() {
        if (liteOrmContext == null /*|| !liteOrmContext.isConnected()*/) {
            connect();
        }
        return liteOrmContext;
    }


    public final Void saveOrUpdate(final Model model) {
        getConnectedInstance().save(model);
        return voidInstance;
    }

    @Override
    public final List<Model> queryAll() {
        return getConnectedInstance().query(modelClass);
    }

    @Override
    public final Void deleteAll() {
        getConnectedInstance().delete(modelClass);
        return voidInstance;
    }

    @Override
    public final Void delete(@NonNull final Model model) {
        getConnectedInstance().delete(model);
        return voidInstance;
    }

    public final Model queryById(@NonNull final long key) {
        if (this instanceof NumKeyDbRepository) {
            return getConnectedInstance().queryById(key, modelClass);
        }
        throw new IllegalStateException("this is not a Numeric Key typed repository! maybe you forget sth");
    }

    public final Model queryById(@NonNull final String key) {
        if (this instanceof StringKeyDbRepository) {
            return getConnectedInstance().queryById(key, modelClass);
        }
        throw new IllegalStateException("this is not a String Key typed repository! maybe you forget sth");
    }


    protected static Void voidInstance;

    static {
        try {
            voidInstance = Void.TYPE.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            voidInstance = null;
        }
    }

    public abstract static class SimpleOnTaskFinishListener
            implements AbsDbInteractor.OnTaskFinishListener<Void> {

        @Override
        public final void onSuccess(Void result) {
            onSuccess();
        }

        public abstract void onSuccess();
    }

    public abstract static class OnQueryTaskFinishListener<R>
            implements AbsDbInteractor.OnTaskFinishListener<R> {

        @Override
        public void onSuccess(R result) {
            if (result == null) {
                onNotExist();
                return;
            }

            if (result instanceof Collection) {
                if (((Collection) result).isEmpty()) {
                    onNotExist();
                    return;
                }
            }

            onExist(result);
        }

        public abstract void onNotExist();

        public abstract void onExist(R result);
    }

    public interface StringKeyDbRepository<Model> extends Repository<Model> {
        //       void deleteById(String key);
        Model queryById(String key);
    }

    public interface NumKeyDbRepository<Model> extends Repository<Model> {
        //        void deleteById(long id);
        Model queryById(long key);
    }
}
