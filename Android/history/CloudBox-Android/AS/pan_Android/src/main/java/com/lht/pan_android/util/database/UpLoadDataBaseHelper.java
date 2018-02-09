package com.lht.pan_android.util.database;

import com.lht.pan_android.Interface.IKeyManager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName: UpLoadDataBaseHelper
 * @Description: 上传数据库helpler
 * @date 2015年12月3日 下午3:05:20
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UpLoadDataBaseHelper extends SQLiteOpenHelper implements IKeyManager.UploadDataBaseKey {
	public UpLoadDataBaseHelper(Context context) {
		super(context, UPLOAD_DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(getCreateSql());
	}

	private String getCreateSql() {
		String s = String.format(
				"create table %s(%s integer PRIMARY KEY AUTOINCREMENT,%s string,%s string,%s string,"
						+ "%s string,%s string,%s long,%s long,%s long,%s long,%s integer,%s string,%s string,%s long,%s integer,%s string)",
				UPLOAD_TABLE_NAME, KEY_ID, KEY_USERNAME, KEY_LOCAL_PATH, KEY_MD5, KEY_REMOTE_PATH, KEY_FINAL_NAME,
				KEY_BEGIN_TIME, KEY_END_TIME, KEY_FILE_SIZE, KEY_COMPLETE_SIZE, KEY_OVERWRITE, KEY_PROJECT_ID,
				KEY_TASK_ID, KEY_CHUNK_SIZE, KEY_STATUS, KEY_CONTENTTYPE);
		return s;

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}