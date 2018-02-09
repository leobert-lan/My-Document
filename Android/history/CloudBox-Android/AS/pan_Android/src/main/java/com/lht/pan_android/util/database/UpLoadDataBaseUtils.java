package com.lht.pan_android.util.database;

import java.util.ArrayList;

import com.lht.pan_android.Interface.IKeyManager;
import com.lht.pan_android.bean.UpLoadDbBean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @ClassName: DataBaseUtils
 * @Description: 数据库业务封装
 * @date 2015年12月3日 下午2:36:56
 * 
 * @author leobert.lan
 * @version 1.0
 */
@SuppressLint("DefaultLocale")
public class UpLoadDataBaseUtils implements IKeyManager.UploadDataBaseKey {
	private static UpLoadDataBaseUtils dataBaseUtils = null;
	private Context context;

	private String tag = "UploadDatabaseUtil";

	private UpLoadDataBaseUtils(Context context) {
		this.context = context;
	}

	public static UpLoadDataBaseUtils getInstance(Context context) {
		if (dataBaseUtils == null) {
			dataBaseUtils = new UpLoadDataBaseUtils(context);
		}
		return dataBaseUtils;
	}

	public synchronized SQLiteDatabase getConnection() {
		SQLiteDatabase sqliteDatabase = null;
		try {

			sqliteDatabase = new UpLoadDataBaseHelper(context).getReadableDatabase();

		} catch (Exception e) {
		}
		return sqliteDatabase;
	}

	// ***********************上传*********************************
	/**
	 * @Title: insertJobs
	 * @Description: 插入上传任务，多条，暂且不使用事物
	 * @author: leobert.lan
	 */
	public synchronized ArrayList<Integer> insertJobs(ArrayList<UpLoadDbBean> jobs) {

		ArrayList<Integer> Ids = new ArrayList<Integer>();

		SQLiteDatabase database = getConnection();

		try {
			database.beginTransaction();
			for (UpLoadDbBean bean : jobs) {
				String sql = getInsertJobSql();
				int VALUE_OVERWRITE;
				if (bean.isOverwrite())
					VALUE_OVERWRITE = 1;
				else
					VALUE_OVERWRITE = 0;
				Object[] bindArgs = { bean.getUsername(), bean.getLocal_path(), bean.getMd5(), bean.getRemote_path(),
						bean.getFinalName(), bean.getBegin_time(), bean.getEnd_time(), bean.getFile_size(),
						bean.getComplete_size(), VALUE_OVERWRITE, bean.getProject_id(), bean.getTask_id(),
						bean.getChunk_size(), bean.getStatus(), bean.getContentType() };
				database.execSQL(sql, bindArgs);
				Cursor c = database.rawQuery(getRowIdSql(), null);
				c.moveToNext();
				Ids.add(c.getInt(c.getColumnIndex(KEY_ID)));
				c.close();
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

		return Ids;

	}

	@SuppressLint("DefaultLocale")
	public synchronized UpLoadDbBean getRowById(int id) {

		UpLoadDbBean bean = null;
		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = %d", UPLOAD_TABLE_NAME, KEY_ID, id);

		try {
			bean = new UpLoadDbBean();
			db.beginTransaction();
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				bean.setId(id);
				bean.setBegin_time(c.getLong(c.getColumnIndex(KEY_BEGIN_TIME)));
				bean.setChunk_size(c.getLong(c.getColumnIndex(KEY_CHUNK_SIZE)));
				bean.setComplete_size(c.getLong(c.getColumnIndex(KEY_COMPLETE_SIZE)));
				bean.setEnd_time(c.getLong(c.getColumnIndex(KEY_END_TIME)));
				bean.setFile_size(c.getLong(c.getColumnIndex(KEY_FILE_SIZE)));
				bean.setFinalName(c.getString(c.getColumnIndex(KEY_FINAL_NAME)));

				bean.setLocal_path(c.getString(c.getColumnIndex(KEY_LOCAL_PATH)));
				bean.setMd5(c.getString(c.getColumnIndex(KEY_MD5)));
				if (c.getInt(c.getColumnIndex(KEY_OVERWRITE)) == 1)
					bean.setOverwrite(true);
				else
					bean.setOverwrite(false);
				bean.setProject_id(c.getString(c.getColumnIndex(KEY_PROJECT_ID)));
				bean.setRemote_path(c.getString(c.getColumnIndex(KEY_REMOTE_PATH)));
				bean.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
				bean.setTask_id(c.getString(c.getColumnIndex(KEY_TASK_ID)));
				bean.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
				bean.setContentType(c.getString(c.getColumnIndex(KEY_CONTENTTYPE)));
			}
			c.close();
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
			db.close();
		}

		return bean;

	}

	/**
	 * @Title: getUnCompletedJobs
	 * @Description: 获取未完成的任务
	 * @author: leobert.lan
	 * @param username
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public synchronized ArrayList<UpLoadDbBean> getUnCompletedJobs(String username) {

		ArrayList<UpLoadDbBean> list = new ArrayList<UpLoadDbBean>();

		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = '%s' and %s < %d order by %s desc", UPLOAD_TABLE_NAME,
				KEY_USERNAME, username, KEY_STATUS, VALUE_STATUS_COMPLETE, KEY_ID);
		try {
			db.beginTransaction();
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				UpLoadDbBean bean = new UpLoadDbBean();
				bean.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				bean.setBegin_time(c.getLong(c.getColumnIndex(KEY_BEGIN_TIME)));
				bean.setChunk_size(c.getLong(c.getColumnIndex(KEY_CHUNK_SIZE)));
				bean.setComplete_size(c.getLong(c.getColumnIndex(KEY_COMPLETE_SIZE)));
				bean.setEnd_time(c.getLong(c.getColumnIndex(KEY_END_TIME)));
				bean.setFile_size(c.getLong(c.getColumnIndex(KEY_FILE_SIZE)));
				bean.setFinalName(c.getString(c.getColumnIndex(KEY_FINAL_NAME)));
				bean.setLocal_path(c.getString(c.getColumnIndex(KEY_LOCAL_PATH)));
				bean.setMd5(c.getString(c.getColumnIndex(KEY_MD5)));
				if (c.getInt(c.getColumnIndex(KEY_OVERWRITE)) == 1)
					bean.setOverwrite(true);
				else
					bean.setOverwrite(false);
				bean.setProject_id(c.getString(c.getColumnIndex(KEY_PROJECT_ID)));
				bean.setRemote_path(c.getString(c.getColumnIndex(KEY_REMOTE_PATH)));
				bean.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
				bean.setTask_id(c.getString(c.getColumnIndex(KEY_TASK_ID)));
				bean.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
				bean.setContentType(c.getString(c.getColumnIndex(KEY_CONTENTTYPE)));
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

	public synchronized String getCompletedPath(int dbIndex) {

		String path = null;
		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = '%s' ", UPLOAD_TABLE_NAME, KEY_ID, dbIndex);

		db.beginTransaction();
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			path = cursor.getString(cursor.getColumnIndex(KEY_LOCAL_PATH));
		}
		cursor.close();
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();

		return path;

	}

	/**
	 * @Title: getCompletedJobs
	 * @Description: 获取完成的任务 changelog:修改sql，将失败的也包含
	 * @author: leobert.lan
	 * @param username
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public synchronized ArrayList<UpLoadDbBean> getCompletedJobs(String username) {

		Log.i("upserver", "i am work");
		ArrayList<UpLoadDbBean> list = new ArrayList<UpLoadDbBean>();

		SQLiteDatabase db = getConnection();
		String sql = String.format("select * from %s where %s = '%s' and %s >= %d order by %s", UPLOAD_TABLE_NAME,
				KEY_USERNAME, username, KEY_STATUS, VALUE_STATUS_COMPLETE, KEY_END_TIME);

		try {
			db.beginTransaction();
			Cursor c = db.rawQuery(sql, null);
			while (c.moveToNext()) {
				Log.d("transportM", "db name:" + c.getString(c.getColumnIndex(KEY_FINAL_NAME)));
				UpLoadDbBean bean = new UpLoadDbBean();
				bean.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				bean.setBegin_time(c.getLong(c.getColumnIndex(KEY_BEGIN_TIME)));
				bean.setChunk_size(c.getLong(c.getColumnIndex(KEY_CHUNK_SIZE)));
				bean.setComplete_size(c.getLong(c.getColumnIndex(KEY_COMPLETE_SIZE)));
				bean.setEnd_time(c.getLong(c.getColumnIndex(KEY_END_TIME)));
				bean.setFile_size(c.getLong(c.getColumnIndex(KEY_FILE_SIZE)));
				bean.setFinalName(c.getString(c.getColumnIndex(KEY_FINAL_NAME)));
				bean.setLocal_path(c.getString(c.getColumnIndex(KEY_LOCAL_PATH)));
				bean.setMd5(c.getString(c.getColumnIndex(KEY_MD5)));
				if (c.getInt(c.getColumnIndex(KEY_OVERWRITE)) == 1)
					bean.setOverwrite(true);
				else
					bean.setOverwrite(false);
				bean.setProject_id(c.getString(c.getColumnIndex(KEY_PROJECT_ID)));
				bean.setRemote_path(c.getString(c.getColumnIndex(KEY_REMOTE_PATH)));
				bean.setStatus(c.getInt(c.getColumnIndex(KEY_STATUS)));
				bean.setTask_id(c.getString(c.getColumnIndex(KEY_TASK_ID)));
				bean.setUsername(c.getString(c.getColumnIndex(KEY_USERNAME)));
				bean.setContentType(c.getString(c.getColumnIndex(KEY_CONTENTTYPE)));
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
	 * @Title: getRowIdSql
	 * @Description: 获取查询最后一条的id的sql 不能另起一个连接，会有问题的
	 * @author: leobert.lan
	 * @return
	 */
	public String getRowIdSql() {
		return String.format("select %s from %s order by %s desc limit 1", KEY_ID, UPLOAD_TABLE_NAME, KEY_ID);
	}

	/**
	 * @Title: updataStatus
	 * @Description: 更新状态
	 * @author: leobert.lan
	 * @param id
	 * @param status
	 */
	@SuppressLint("DefaultLocale")
	public synchronized void updateStatus(int id, int status) {

		Log.d(tag, "function has been called,change status to :" + status);
		String sql = String.format("update %s set status = %d where _id =%d", UPLOAD_TABLE_NAME, status, id);
		// update table_pan_upload set status = 1 where _id ='1';
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
			Log.wtf(tag, "that is ok");
		} finally {
			database.endTransaction();
			database.close();
		}

	}

	public synchronized void updateMd5(int id, String md5) {

		String sql = String.format("update %s set %s = '%s' where _id =%d", UPLOAD_TABLE_NAME, KEY_MD5, md5, id);
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

	}

	public synchronized void updateFileSize(int id, long fileSize) {

		String sql = String.format("update %s set %s = %d where _id =%d", UPLOAD_TABLE_NAME, KEY_FILE_SIZE, fileSize,
				id);
		SQLiteDatabase database = getConnection();
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
	 * @Title: updateEndTime
	 * @Description: 更新结束时间
	 * @author: leobert.lan
	 * @param id
	 * @param endTime
	 */
	@SuppressLint("DefaultLocale")
	public synchronized void updateEndTime(int id, long endTime) {

		String sql = String.format("update %s set %s = %d where _id =%d", UPLOAD_TABLE_NAME, KEY_END_TIME, endTime, id);
		SQLiteDatabase database = getConnection();
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
	 * @Title: getInsertJobSql
	 * @Description: 插入一条的sql
	 * @author: leobert.lan
	 * @return
	 */
	private String getInsertJobSql() {
		String s = String.format(
				"insert into %s(%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
				UPLOAD_TABLE_NAME, KEY_USERNAME, KEY_LOCAL_PATH, KEY_MD5, KEY_REMOTE_PATH, KEY_FINAL_NAME,
				KEY_BEGIN_TIME, KEY_END_TIME, KEY_FILE_SIZE, KEY_COMPLETE_SIZE, KEY_OVERWRITE, KEY_PROJECT_ID,
				KEY_TASK_ID, KEY_CHUNK_SIZE, KEY_STATUS, KEY_CONTENTTYPE);
		return s;
	}

	/**
	 * @Title: update
	 * @Description: combine时更新字段
	 * @author: leobert.lan
	 * @param dbIndex
	 * @param md5
	 * @param mFinalName
	 * @param size
	 * @param valueStatusUploading
	 */
	@SuppressLint("DefaultLocale")
	public synchronized void update(int dbIndex, String md5, String mFinalName, Long size, int valueStatusUploading) {

		String sql = String.format("update %s set %s = '%s',%s = '%s',%s = %d,%s = %d where %s = %d;",
				UPLOAD_TABLE_NAME, KEY_MD5, md5, KEY_FINAL_NAME, mFinalName, KEY_STATUS, valueStatusUploading,
				KEY_FILE_SIZE, size, KEY_ID, dbIndex);
		SQLiteDatabase database = getConnection();
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
	 * @Title: updateProgress
	 * @Description: 更新完成进度
	 * @author: leobert.lan
	 * @param id
	 * @param compliteSize
	 */
	public synchronized void updateProgress(int id, long completeSize) {

		String sql = String.format("update %s set %s = %d where %s = %d;", UPLOAD_TABLE_NAME, KEY_COMPLETE_SIZE,
				completeSize, KEY_ID, id);
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			database.execSQL(sql);
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

	}

	// ***********************上传部分结束****************************

	public synchronized void initStatus() {

		String sql1 = "update table_pan_upload set status = 2 where status <3";
		// 本条专治疑难杂症，理论上出现这条sql真正起到效果会是以下情况：程序bug，服务端bug
		String sql2 = String.format("update table_pan_upload set status = 4 where %s > %s", KEY_COMPLETE_SIZE,
				KEY_FILE_SIZE);
		SQLiteDatabase database = getConnection();
		try {
			database.beginTransaction();
			database.execSQL(sql1);
			database.execSQL(sql2);
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
			String sql = String.format("delete from %s where %s = %d", UPLOAD_TABLE_NAME, KEY_ID, id);
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
				String sql = String.format("delete from %s where %s = %d", UPLOAD_TABLE_NAME, KEY_ID, temp.get(i));
				database.execSQL(sql);
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.close();
		}

	}

}