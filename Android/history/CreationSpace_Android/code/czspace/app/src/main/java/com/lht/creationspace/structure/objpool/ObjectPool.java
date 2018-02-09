package com.lht.creationspace.structure.objpool;

import com.lht.creationspace.util.debug.DLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p><b>Package:</b> com.lht.creationspace.clazz </p>
 * <p><b>Project:</b> czspace </p>
 * <p><b>Classname:</b> ObjectPool </p>
 * <p><b>Description:</b> 存储累积任务的数据结构 </p>
 * K必须是简单数据类型
 * Created by leobert on 2017/3/10.
 */

public abstract class ObjectPool<T, K, S> {

    /**
     * 原始池，一般用于保存原始内容
     */
    private ArrayList<T> cachePool;

    /**
     * 操作池
     */
    private ArrayList<T> dumpPool;

    /**
     * 保存池,一般用于保存操作成功的内容
     */
    private LinkedHashMap<K, S> savePool;

    public ObjectPool() {
        cachePool = new ArrayList<>();
        dumpPool = new ArrayList<>();
        savePool = new LinkedHashMap<>();
    }

    public synchronized void add(T item) {
        synchronized (this) {
            if (item == null)
                return;
            cachePool.add(item);
            dumpPool.add(item);
        }
    }

    public synchronized void add(Collection<T> collection) {
        synchronized (this) {
            if (collection == null || collection.isEmpty())
                return;
            cachePool.addAll(collection);
            dumpPool.addAll(collection);
        }
    }

    private boolean hasPreparedForSolveRemoved = false;
    private boolean hasPreparedForSolveAdded = false;

    public synchronized void prepare() {
        hasPreparedForSolveAdded = true;
        hasPreparedForSolveRemoved = true;

        dumpPool.clear();

        for (T t : cachePool) {
            if (savePool.containsKey(generateKey(t)))
                continue;
            dumpPool.add(t);
        }
        if (!dumpPool.isEmpty()) {
            DLog.e(getClass(), new DLog.LogLocation(),
                    "error on prepare,maybe jobs don't finish");
        }
    }

    public synchronized void solveRemovedItems(Collection<T> collection,
                                               EqualsComparator<T> comparator) {
        synchronized (this) {
            if (!hasPreparedForSolveRemoved) {
                DLog.e(getClass(), "call prepare() at first");
                return;
            }

            hasPreparedForSolveRemoved = false;

            if (collection == null || collection.isEmpty()) {
                //everything has removed
                cachePool.clear();
                dumpPool.clear();
                savePool.clear();
                return;
            }
            for (int i = 0; i < cachePool.size(); i++) {
                T itemInPool = cachePool.get(i);
                boolean isInCollection =
                        containsInCollection(collection, itemInPool, comparator);

                if (!isInCollection) {
                    //has been removed,mean no need to keep in pool
                    cachePool.remove(itemInPool);
                    dumpPool.remove(itemInPool); // 正常情况下，上一次业务全部处理完dumpPool中已经空了

                    savePool.remove(generateKey(itemInPool)); // 已经不再需要缓存该内容
                }
            }
        }
    }

    private boolean containsInCollection(Collection<T> collection,
                                         T item, EqualsComparator<T> comparator) {
        for (T t : collection) {
            int ret = comparator.compare(t, item);
            if (ret == EqualsComparator.RET_EQUAL)
                return true;
        }
        return false;
    }


    public synchronized void solveAddedItems(Collection<T> collection,
                                             EqualsComparator<T> comparator) {
        synchronized (this) {
            if (!hasPreparedForSolveAdded) {
                DLog.e(getClass(), "call prepare() at first");
                return;
            }
            hasPreparedForSolveAdded = false;

            if (collection == null || collection.isEmpty()) {
                //everything has been removed
                cachePool.clear();
                dumpPool.clear();
                savePool.clear();
                return;
            }

            for (T t : collection) {
                boolean isInPool =
                        containsInCollection(cachePool, t, comparator);

                if (!isInPool) {
                    //new aded
                    cachePool.add(t);
                    dumpPool.add(t);
                }
            }

        }
    }

    public synchronized boolean hasNext() {
        synchronized (this) {
            return !dumpPool.isEmpty();
        }
    }

    public void save(T keyItem, S saveItem) {
        savePool.put(generateKey(keyItem), saveItem);
    }

    public synchronized T next() {
        if (hasNext()) {
            return dumpPool.remove(0);
        }
        else
            return null;
    }

    public void clearAll() {
        cachePool.clear();
        savePool.clear();
        dumpPool.clear();
    }

    public boolean isEmpty() {
        return cachePool.isEmpty();
    }

    protected abstract K generateKey(T keyItem);

    public synchronized ArrayList<S> getSavedItems() {
        synchronized (this) {
            ArrayList<S> ret = new ArrayList<>();
            for (Map.Entry<K, S> ksEntry : savePool.entrySet()) {
                ret.add(ksEntry.getValue());
            }
            return ret;
        }
    }
}

