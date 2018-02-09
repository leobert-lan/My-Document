package com.lht.pan_android.util.database;

import java.util.ArrayList;

import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.bean.DownloadInfoBean;
import com.lht.pan_android.bean.ShareDownloadInfoBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName: DataBaseUtils
 * @Description: 数据库业务封装
 * @date 2015年12月3日 下午2:36:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
@SuppressLint("DefaultLocale")
public class DownLoadDataBaseUtils implements IKeyManager.DownLoadDataBaseKey {
	private static DownLoadDataBaseUtils dataBaseUtils = null;
	private Context context;

	private DownLoadDataBaseUtils(Context context) {
		this.context = context;
	}

	public static DownLoadDataBaseUtils getInstance(Context context) {
		if (dataBaseUtils == null) {
			dataBaseUtils = new DownLoadDataBaseUtils(context);
		}
		return dataBaseUtils;
	}

	public synchronized SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {
			sqliteDatabase = new DownLoadDataBaseHelper(context).getWritableDatabase();
		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

	/**
	 * @Title: insertDownloadJob
	 * @Description: 插入记录，返回行号
	 * @author: leobert.lan
	 * @param job
	 * @return
	 */
	public synchronized ArrayList<Integer> insertShareDownloadJob(ArrayList<ShareDownloadInfoBean> downInfoBeans) {

		ArrayList<Integer> ids = new ArrayList<Integer>();
		SQLiteDatabase database = getConnection();

		try {
			database.beginTransaction();

			for (ShareDownloadInfoBean downInfoBean : downInfoBeans) {
				String sql = getInsertSql();
				Object[] bindArgs = { downInfoBean.getUsername(), downInfoBean.getLocalpath(),
						downInfoBean.getRemotepath(), downInfoBean.getCompeleteSize(), downInfoBean.getSize(),
						downInfoBean.getBegin_time(), downInfoBean.getEnd_time(), downInfoBean.getStatus(),
						downInfoBean.getIcon() };
				database.execSQL(sql, bindArgs);

				Cursor cursor = database.rawQuery(SelectIdSql(), null);
				while (cursor.moveToNext()) {
					ids.add(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_ID)));
				}
				cursor.close();
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

		return ids;
	}

	public synchronized ArrayList<Integer> insertDownloadJob(ArrayList<DownloadInfoBean> downInfoBeans) {

		ArrayList<Integer> ids = new ArrayList<Integer>();
		SQLiteDatabase database = getConnection();

		try {
			database.beginTransaction();

			for (DownloadInfoBean downInfoBean : downInfoBeans) {
				String sql = getInsertSql();
				Object[] bindArgs = { downInfoBean.getUsername(), downInfoBean.getLocalpath(),
						downInfoBean.getRemotepath(), downInfoBean.getCompeleteSize(), downInfoBean.getSize(),
						downInfoBean.getBegin_time(), downInfoBean.getEnd_time(), downInfoBean.getStatus(),
						downInfoBean.getIcon() };
				database.execSQL(sql, bindArgs);

				Cursor cursor = database.rawQuery(SelectIdSql(), null);
				while (cursor.moveToNext()) {
					ids.add(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_ID)));
				}
				cursor.close();
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

		return ids;
	}

	/**
	 * @Title: getUnCompletedJobs
	 * @Description: 获取未完成的任务
	 * @author: leobert.lan
	 * @param username
	 * @return
	 */
	public synchronized ArrayList<DownloadInfoBean> getUnCompletedJobs(String username) {
		ArrayList<DownloadInfoBean> list = new ArrayList<DownloadInfoBean>();

		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = '%s' and %s < %d order by %s desc", DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_USERNAME, username, DOWNLOAD_KEY_STATUS, VALUE_STATUS_COMPLETE, DOWNLOAD_KEY_ID);
		if (db == null)
			return new ArrayList<DownloadInfoBean>();
		try {
			db.beginTransaction();
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				DownloadInfoBean bean = new DownloadInfoBean();
				bean.setId(c.getInt(c.getColumnIndex(DOWNLOAD_KEY_ID)));
				bean.setUsername(c.getString(c.getColumnIndex(DOWNLOAD_KEY_USERNAME)));
				bean.setLocalpath(c.getString(c.getColumnIndex(DOWNLOAD_KEY_LOCAL_PATH)));

				bean.setCompeleteSize(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_COMPLETE_SIZE)));
				bean.setSize(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_FILE_SIZE)));
				bean.setBegin_time(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_BEGIN_TIME)));
				bean.setEnd_time(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_END_TIME)));
				bean.setStatus(c.getInt(c.getColumnIndex(DOWNLOAD_KEY_STATUS)));
				bean.setIcon(c.getString(c.getColumnIndex(DOWNLOAD_KEY_ICON)));
				list.add(bean);
			}
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}

		return list;
	}

	/**
	 * @Title: getCompletedPath
	 * @Description: 获取传输结束的本地路径
	 * @author: leobert.lan
	 * @param dbIndex
	 * @return
	 */
	public synchronized String getCompletedPath(int dbIndex) {
		String path = null;
		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = '%s' order by %s desc", DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_ID, dbIndex, DOWNLOAD_KEY_END_TIME);

		db.beginTransaction();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			path = cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_LOCAL_PATH));
		}
		cursor.close();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();

		return path;
	}

	/**
	 * @Title: getCompletedJobs
	 * @Description: 获取完成的记录
	 * @author: leobert.lan
	 * @param username
	 * @return
	 */
	public synchronized ArrayList<DownloadInfoBean> getCompletedJobs(String username) {
		ArrayList<DownloadInfoBean> list = new ArrayList<DownloadInfoBean>();

		SQLiteDatabase db = getConnection();
		if (db == null)
			return new ArrayList<DownloadInfoBean>();

		String sql = String.format("select * from %s where %s = '%s' and %s >= %d order by %s desc",
				DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_USERNAME, username, DOWNLOAD_KEY_STATUS, VALUE_STATUS_COMPLETE,
				DOWNLOAD_KEY_END_TIME);
		try {
			db.beginTransaction();
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				DownloadInfoBean bean = new DownloadInfoBean();
				bean.setId(c.getInt(c.getColumnIndex(DOWNLOAD_KEY_ID)));
				bean.setUsername(c.getString(c.getColumnIndex(DOWNLOAD_KEY_USERNAME)));
				bean.setLocalpath(c.getString(c.getColumnIndex(DOWNLOAD_KEY_LOCAL_PATH)));
				bean.setRemotepath(c.getString(c.getColumnIndex(DOWNLOAD_KEY_REMOTE_PATH)));
				bean.setCompeleteSize(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_COMPLETE_SIZE)));
				bean.setSize(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_FILE_SIZE)));
				bean.setBegin_time(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_BEGIN_TIME)));
				bean.setEnd_time(c.getLong(c.getColumnIndex(DOWNLOAD_KEY_END_TIME)));
				bean.setStatus(c.getInt(c.getColumnIndex(DOWNLOAD_KEY_STATUS)));
				bean.setIcon(c.getString(c.getColumnIndex(DOWNLOAD_KEY_ICON)));

				list.add(bean);
			}
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}

		return list;
	}

	public synchronized ShareDownloadInfoBean getShareRowById(int dbIndex) {
		ShareDownloadInfoBean bean = new ShareDownloadInfoBean();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			database.beginTransaction();
			String sql = String.format("select * from %s where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID, dbIndex);
			cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {

				bean.setId(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_ID)));
				bean.setUsername(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_USERNAME)));
				bean.setLocalpath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_LOCAL_PATH)));
				bean.setRemotepath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_REMOTE_PATH)));
				bean.setCompeleteSize(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_COMPLETE_SIZE)));
				bean.setSize(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_FILE_SIZE)));
				bean.setBegin_time(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_BEGIN_TIME)));
				bean.setEnd_time(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_END_TIME)));
				bean.setStatus(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_STATUS)));
				bean.setIcon(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_ICON)));
				bean.setShareId("");
				bean.setOwner("");
				bean.setType("");
			}
			if (null != cursor) {
				cursor.close();
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
		return bean;
	}

	public synchronized DownloadInfoBean getRowById(int dbIndex) {
		DownloadInfoBean bean = new DownloadInfoBean();
		SQLiteDatabase database = getConnection();
		Cursor cursor = null;
		try {
			database.beginTransaction();
			String sql = String.format("select * from %s where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID, dbIndex);
			cursor = database.rawQuery(sql, null);
			while (cursor.moveToNext()) {

				bean.setId(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_ID)));
				bean.setUsername(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_USERNAME)));
				bean.setLocalpath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_LOCAL_PATH)));
				bean.setRemotepath(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_REMOTE_PATH)));
				bean.setCompeleteSize(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_COMPLETE_SIZE)));
				bean.setSize(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_FILE_SIZE)));
				bean.setBegin_time(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_BEGIN_TIME)));
				bean.setEnd_time(cursor.getLong(cursor.getColumnIndex(DOWNLOAD_KEY_END_TIME)));
				bean.setStatus(cursor.getInt(cursor.getColumnIndex(DOWNLOAD_KEY_STATUS)));
				bean.setIcon(cursor.getString(cursor.getColumnIndex(DOWNLOAD_KEY_ICON)));

			}
			if (null != cursor) {
				cursor.close();
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
		return bean;
	}

	public synchronized void updateStatus(int dbIndex, int status) {
		SQLiteDatabase database = getConnection();
		String sql = String.format("update %s set %s = %d where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_STATUS,
				status, DOWNLOAD_KEY_ID, dbIndex);
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public synchronized void updateSizeAndStatus(int dbIndex, long completeSize, int status) {
		SQLiteDatabase database = getConnection();
		String sql = String.format("update %s set %s = %d , %s = %d where %s = %d", DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_COMPLETE_SIZE, completeSize, DOWNLOAD_KEY_STATUS, status, DOWNLOAD_KEY_ID, dbIndex);
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public synchronized void updateCompleteSize(int dbIndex, long completeSize) {
		SQLiteDatabase database = getConnection();
		String sql = String.format("update %s set %s = %d where %s = %d", DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_COMPLETE_SIZE, completeSize, DOWNLOAD_KEY_ID, dbIndex);
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public synchronized void initStatus() {
		String sql = String.format("update %s set %s = %d where %s <%d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_STATUS,
				VALUE_STATUS_PAUSE, DOWNLOAD_KEY_STATUS, VALUE_STATUS_COMPLETE);
		SQLiteDatabase database = getConnection();
		if (database == null)
			return;
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	/**
	 * 更新数据库中的下载信息
	 */
	public synchronized void updataInfos(int id, long compeleteSize, String remotepath) {
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			String sql = updateSql();
			Object[] bindArgs = { id, compeleteSize, remotepath };
			database.execSQL(sql, bindArgs);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	/**
	 * 删除命令执行
	 */
	public synchronized void delete(int id) {
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			String sql = String.format("delete from %s where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID, id);
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	private String getInsertSql() {
		String insertSql = String.format("insert into %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) values (?,?,?,?,?,?,?,?,?)",
				DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_USERNAME, DOWNLOAD_KEY_LOCAL_PATH, DOWNLOAD_KEY_REMOTE_PATH,
				DOWNLOAD_KEY_COMPLETE_SIZE, DOWNLOAD_KEY_FILE_SIZE, DOWNLOAD_KEY_BEGIN_TIME, DOWNLOAD_KEY_END_TIME,
				DOWNLOAD_KEY_STATUS, DOWNLOAD_KEY_ICON);
		return insertSql;
	}

	// private String getSelectSql() {
	// String selectSql = String.format(
	// "select %s,%s,%s,%s,%s,%s,%s,%s from %s where %s = (?) ",
	// DOWNLOAD_KEY_USERNAME, DOWNLOAD_KEY_LOCAL_PATH,
	// DOWNLOAD_KEY_REMOTE_PATH, DOWNLOAD_KEY_COMPLETE_SIZE,
	// DOWNLOAD_KEY_FILE_SIZE, DOWNLOAD_KEY_BEGIN_TIME,
	// DOWNLOAD_KEY_END_TIME, DOWNLOAD_KEY_STATUS,
	// DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID);
	// return selectSql;
	// }

	private String updateSql() {
		String updateSql = String.format("update %s set %s = ? where %s = (?)", DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_COMPLETE_SIZE, DOWNLOAD_KEY_ID);
		return updateSql;
	}

	/**
	 * @Title: SelectIdSql
	 * @Description: 获取ID
	 * @author: zhangbin
	 * @return
	 */
	private String SelectIdSql() {
		return String.format("select %s from %s order by %s desc limit 1", DOWNLOAD_KEY_ID, DOWNLOAD_TABLE_NAME,
				DOWNLOAD_KEY_ID);

	}

	public synchronized void updateEndTime(int dbIndex, Long stamp) {
		SQLiteDatabase database = getConnection();
		String sql = String.format("update %s set %s = %d where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_END_TIME,
				stamp, DOWNLOAD_KEY_ID, dbIndex);
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	public void delete(ArrayList<Integer> temp) {
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			for (int i = 0; i < temp.size(); i++) {
				String sql = String.format("delete from %s where %s = %d", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID,
						temp.get(i));
				database.execSQL(sql);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}

	/**
	 * @Title: delete
	 * @Description: 清除用户下载记录
	 * @author: leobert.lan
	 * @param username
	 */
	public void delete(String username) {
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			String sql = String.format("delete from %s where %s = '%s'", DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_USERNAME,
					username);
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}
	}
}