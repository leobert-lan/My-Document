package com.lht.creationspace.base.domain.repository;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.util.Log;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.TableManager;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLStatement;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.db.model.RelationKey;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p><b>Package:</b> com.lht.creationspace.base.model.dbmodel </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> LiteOrmContext </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2017/7/6.
 */

public class LiteOrmContext implements DataBase {
    private static final String TAG = "LiteOrmContext";

    private boolean isConnected = false;

    private LiteOrm liteOrm;

    private
    @InstanceType
    int type;

    private LiteOrmContext(@NonNull LiteOrm liteOrm, @InstanceType int type) {
        this.liteOrm = liteOrm;
        this.type = type;
        openOrCreateDatabase();
    }

    public static final int SINGLE = 1;

    private static final int CASCADE = 2;

    @IntDef({SINGLE, CASCADE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface InstanceType {
    }

    public static LiteOrmContext getLiteOrmContext(Context context,
                                                   String dbName,
                                                   @InstanceType int type) {
        LiteOrm liteOrm;
        if (type == SINGLE) {
            liteOrm = LiteOrm.newSingleInstance(context, dbName);
        } else {
            liteOrm = LiteOrm.newCascadeInstance(context, dbName);
        }
        return new LiteOrmContext(liteOrm, type);
    }


    private synchronized void checkContext(boolean isMainForbidden) {
        if (isMainForbidden) {
            boolean isMain = Looper.myLooper() == Looper.getMainLooper();
            if (isMain)
                Log.e(TAG, "not suggest to call at main thread!");
        }

        if (liteOrm.getTableManager() == null || !isConnected) {
            DataBaseConfig config = liteOrm.getDataBaseConfig();
            isConnected = true;
            if (type == SINGLE) {
                liteOrm = LiteOrm.newSingleInstance(config);
            } else {
                liteOrm = LiteOrm.newCascadeInstance(config);
            }
        }
    }

    @Override
    public synchronized SQLiteDatabase openOrCreateDatabase() {
        checkContext(false);
        return liteOrm.openOrCreateDatabase();
    }

    @Override
    public synchronized long save(Object o) {
        checkContext(true);
        long ret = liteOrm.save(o);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int save(Collection<T> collection) {
        checkContext(true);
        int ret = liteOrm.save(collection);
        close();
        return ret;
    }

    @Override
    public synchronized long insert(Object o) {
        checkContext(true);
        long ret = liteOrm.insert(o);
        close();
        return ret;
    }

    @Override
    public synchronized long insert(Object o, ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        long ret = liteOrm.insert(o, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int insert(Collection<T> collection) {
        checkContext(true);
        int ret = liteOrm.save(collection);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int insert(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.insert(collection, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized int update(Object o) {
        checkContext(true);
        int ret = liteOrm.update(o);
        close();
        return ret;
    }

    @Override
    public synchronized int update(Object o, ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.update(o, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized int update(Object o, ColumnsValue columnsValue,
                                   ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.update(o, columnsValue, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int update(Collection<T> collection) {
        checkContext(true);
        int ret = liteOrm.update(collection);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int update(Collection<T> collection, ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.update(collection, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int update(Collection<T> collection,
                                       ColumnsValue columnsValue,
                                       ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.update(collection, columnsValue, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized int update(WhereBuilder whereBuilder, ColumnsValue columnsValue, ConflictAlgorithm conflictAlgorithm) {
        checkContext(true);
        int ret = liteOrm.update(whereBuilder, columnsValue, conflictAlgorithm);
        close();
        return ret;
    }

    @Override
    public synchronized int delete(Object o) {
        checkContext(true);
        int ret = liteOrm.delete(o);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int delete(Class<T> aClass) {
        checkContext(true);
        int ret = liteOrm.delete(aClass);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int deleteAll(Class<T> aClass) {
        checkContext(true);
        int ret = liteOrm.deleteAll(aClass);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int delete(Class<T> aClass,
                                       long l, long l1, String s) {
        checkContext(true);
        int ret = liteOrm.delete(aClass, l, l1, s);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int delete(Collection<T> collection) {
        checkContext(true);
        int ret = liteOrm.delete(collection);
        close();
        return ret;
    }

    @Override
    public synchronized <T> int delete(Class<T> aClass, WhereBuilder whereBuilder) {
        checkContext(true);
        int ret = liteOrm.delete(aClass, whereBuilder);
        close();
        return ret;
    }

    @Override
    public synchronized int delete(WhereBuilder whereBuilder) {
        checkContext(true);
        int ret = liteOrm.delete(whereBuilder);
        close();
        return ret;
    }

    @Override
    public synchronized <T> ArrayList<T> query(Class<T> aClass) {
        checkContext(true);
        ArrayList<T> ret = liteOrm.query(aClass);
        close();
        return ret;
    }

    @Override
    public synchronized <T> ArrayList<T> query(QueryBuilder<T> queryBuilder) {
        checkContext(true);
        ArrayList<T> ret = liteOrm.query(queryBuilder);
        close();
        return ret;
    }

    @Override
    public synchronized <T> T queryById(long l, Class<T> aClass) {
        checkContext(true);
        T ret = liteOrm.queryById(l, aClass);
        close();
        return ret;
    }

    @Override
    public synchronized <T> T queryById(String s, Class<T> aClass) {
        checkContext(true);
        T ret = liteOrm.queryById(s, aClass);
        close();
        return ret;
    }

    @Override
    public synchronized <T> long queryCount(Class<T> aClass) {
        checkContext(true);
        long ret = liteOrm.queryCount(aClass);
        close();
        return ret;
    }

    @Override
    public synchronized long queryCount(QueryBuilder queryBuilder) {
        checkContext(true);
        long ret = liteOrm.queryCount(queryBuilder);
        close();
        return ret;
    }

    @Override
    public synchronized SQLStatement createSQLStatement(String s, Object[] objects) {
        return liteOrm.createSQLStatement(s, objects);
    }

    @Override
    public synchronized boolean execute(SQLiteDatabase sqLiteDatabase, SQLStatement sqlStatement) {
        return liteOrm.execute(sqLiteDatabase, sqlStatement);
    }

    @Override
    public synchronized boolean dropTable(Object o) {
        checkContext(true);
        boolean ret = liteOrm.dropTable(o);
        close();
        return ret;
    }

    @Override
    public synchronized boolean dropTable(Class<?> aClass) {
        checkContext(true);
        boolean ret = liteOrm.dropTable(aClass);
        close();
        return ret;
    }

    @Override
    public synchronized boolean dropTable(String s) {
        checkContext(true);
        boolean ret = liteOrm.dropTable(s);
        close();
        return ret;
    }

    @Override
    public synchronized ArrayList<RelationKey> queryRelation(Class aClass, Class aClass1, List<String> list) {
        checkContext(true);
        ArrayList<RelationKey> ret = liteOrm.queryRelation(aClass, aClass1, list);
        close();
        return ret;
    }

    @Override
    public synchronized <E, T> boolean mapping(Collection<E> collection, Collection<T> collection1) {
        checkContext(true);
        boolean ret = liteOrm.mapping(collection, collection1);
        close();
        return ret;
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        checkContext(false);
        return liteOrm.getReadableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        checkContext(false);
        return liteOrm.getWritableDatabase();
    }

    @Override
    public synchronized TableManager getTableManager() {
        checkContext(false);
        return liteOrm.getTableManager();
    }

    @Override
    public synchronized SQLiteHelper getSQLiteHelper() {
        checkContext(false);
        return liteOrm.getSQLiteHelper();
    }

    @Override
    public synchronized DataBaseConfig getDataBaseConfig() {
        return liteOrm.getDataBaseConfig();
    }

    @Override
    public synchronized SQLiteDatabase openOrCreateDatabase(String s, SQLiteDatabase.CursorFactory cursorFactory) {
        return liteOrm.openOrCreateDatabase(s, cursorFactory);
    }

    @Override
    public synchronized boolean deleteDatabase() {
        checkContext(false);
        return liteOrm.deleteDatabase();
    }

    @Override
    public synchronized boolean deleteDatabase(File file) {
        checkContext(false);
        return liteOrm.deleteDatabase(file);
    }

    @Override
    public synchronized void close() {
        if (isConnected) {
            isConnected = false;
            liteOrm.close();
        }
    }


    public boolean isConnected() {
        return isConnected;
    }
}
