package com.bankicp.app.db;

/** 
 * @ClassName: SqliteHelper 
 * @Description: TODO(管理数据库的创建和版本辅助类) 
 * @author 陈培伟 
 * @date 2014-4-23 下午3:36:42 
 *  
 */

import com.bankicp.app.model.RoomInfo;
import com.bankicp.app.model.TaskInfo;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {

	/**
	 * @Fields mContext : TODO(上下文引用)
	 */
	private Context mContext;
	/**
	 * @Fields TB_NAME : TODO(用来保存UserID、密码的表名)
	 */

	public static final String TB_TASK = "tb_task";
	public static final String TB_ROOM = "tb_room";

	private final static String CREADTB_TASK = "CREATE TABLE IF NOT EXISTS "
			+ TB_TASK + "(" + TaskInfo._ID
			+ " integer primary key autoincrement," + TaskInfo.TASK_ID
			+ " varchar," + TaskInfo.JSON_DATA + " varchar,"
			+ TaskInfo.TASK_TYPE + " varchar," + TaskInfo.ROOM_ID + " varchar,"
			+ TaskInfo.CREATE_TIME + " varchar," + TaskInfo.USER_ID
			+ " varchar" + ")";
	private final static String CREADTB_ROOM = "CREATE TABLE IF NOT EXISTS "
			+ TB_ROOM + "(" + RoomInfo._ID
			+ " integer primary key autoincrement," + RoomInfo.JSON_DATA
			+ " varchar," + RoomInfo.CREATE_TIME + " varchar,"
			+ RoomInfo.USER_ID + " varchar" + ")";

	public SqliteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		mContext = context;
	}

	// 创建表
	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
			// 创建现场信息表
			db.execSQL(CREADTB_TASK);
			db.execSQL(CREADTB_ROOM);
		} catch (Exception e) {
			// LogUtil.recordExceptionLog(mContext, e, null, true);
		}

	}

	// 更新表
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		try {
			db.execSQL("DROP TABLE IF EXISTS " + TB_TASK);
			db.execSQL("DROP TABLE IF EXISTS " + TB_ROOM);
			onCreate(db);
		} catch (Exception e) {
			// LogUtil.recordExceptionLog(mContext, e, null, true);
		}

	}
}