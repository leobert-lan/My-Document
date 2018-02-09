package com.lht.pan_android.util.database;

import com.lht.pan_android.Interface.IKeyManager;

/**
 * Created by zhangbin on 2015/11/30.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 建立一个数据库帮助类
 */
public class DownLoadDataBaseHelper extends SQLiteOpenHelper implements IKeyManager.DownLoadDataBaseKey {

	public DownLoadDataBaseHelper(Context context) {
		super(context, DOWNLOAD_DATABASE_NAME, null, DOWNLOAD_VERSION);
	}

	/**
	 * 在download.db数据库下创建一个download_info表存储下载信息
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreateSql());
	}

	private String getCreateSql() {
		String createSql = String.format(
				"create table %s(%s integer PRIMARY KEY AUTOINCREMENT,%s string, %s string, "
						+ "%s string, %s string, %s long, %s long, %s long, %s integer ,%s string)",
				DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_ID, DOWNLOAD_KEY_USERNAME, DOWNLOAD_KEY_LOCAL_PATH,
				DOWNLOAD_KEY_REMOTE_PATH, DOWNLOAD_KEY_BEGIN_TIME, DOWNLOAD_KEY_END_TIME, DOWNLOAD_KEY_COMPLETE_SIZE,
				DOWNLOAD_KEY_FILE_SIZE, DOWNLOAD_KEY_STATUS, DOWNLOAD_KEY_ICON);
		return createSql;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	// private void updateDb2Version2() {
	// // ALTER TABLE table_name ADD column_name datatype
	//
	// SQLiteDatabase db = getWritableDatabase();
	//
	// String upgradeSql1 = String.format("ALTER TABLE %s ADD %s string",
	// DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_SHAREID);
	// String upgradeSql2 = String.format("ALTER TABLE %s ADD %s string",
	// DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_OWNER);
	// String upgradeSql3 = String.format("ALTER TABLE %s ADD %s string",
	// DOWNLOAD_TABLE_NAME, DOWNLOAD_KEY_TYPE);
	// try {
	// db.beginTransaction();
	// // db.execSQL(upgradeSql1);
	// // db.execSQL(upgradeSql2);
	// // db.execSQL(upgradeSql3);
	// db.setTransactionSuccessful();
	// } catch (Exception e) {
	// } finally {
	// db.endTransaction();
	// db.close();
	// }
	// }
}