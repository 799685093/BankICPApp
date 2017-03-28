package com.bankicp.app.db;

/** 
 * @ClassName: DataHelper 
 * @Description: TODO(本地数据库Sqlite操作管理类) 
 * @date 2014-4-23 下午3:34:15 
 *  
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.bankicp.app.MyApp;
import com.bankicp.app.model.RoomInfo;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

public class DataHelper {
	private static String DB_NAME = "bankicp.db";
	private static int DB_VERSION = 2;
	private SQLiteDatabase db;
	private SqliteHelper dbHelper;
	Context mContext;

	public DataHelper(Context context) {

		try {
			// 创建管理数据库创建和版本的辅助类
			dbHelper = new SqliteHelper(context, DB_NAME, null, DB_VERSION);
			// 创建数据管理类
			db = dbHelper.getWritableDatabase();

			mContext = context;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @Title:
	 * @Description: TODO(关闭数据库)
	 * @return void 返回类型
	 * @throws
	 */
	public void Close() {
		try {
			if(db!=null){
				db.close();
				dbHelper.close();
			}
		} catch (Exception e) {

		}

	}

	// ============任务 =======================
	/***
	 * 保存任务列表<br>
	 * 已存在的任务更加创建时间判断是否更新
	 * 
	 * @param taskList
	 * @return
	 */
	public int saveTaskList(List<TaskInfo> taskList) {
		int row = 0;
		db.beginTransaction(); // 手动设置开始事务
		try {
			for (int i = 0; i < taskList.size(); i++) {
				TaskInfo info = taskList.get(i);
				if(saveTask(info)==-100){
					throw new Exception();
				};
				row++;
			}
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		} catch (Exception e) {
			
			e.printStackTrace();
		} finally {
			db.endTransaction(); // 处理完成
		}
		return row;

	}

	public long saveTask(TaskInfo info) {
		long i = 0;
		try {
			UserInfo userInfo = MyApp.getInstance().getUserInfo();
			ContentValues values = new ContentValues();
			values.put(TaskInfo.TASK_ID, info.getId());
			values.put(TaskInfo.JSON_DATA, JsonUtil.objectToJson(info));
			values.put(TaskInfo.CREATE_TIME, info.getTaskTime());
			values.put(TaskInfo.TASK_TYPE, info.getTaskType());
			values.put(TaskInfo.ROOM_ID, info.getCabinet().getRoomId());
			values.put(TaskInfo.USER_ID, userInfo.getUserId());
			//
			TaskInfo taskInfo = getTask(String.valueOf(info.getId()));
			if (taskInfo != null) {
				// 更新
				if (info.getTaskState() >= taskInfo.getTaskState()) {
					i = db.update(SqliteHelper.TB_TASK,
							values,
							TaskInfo.TASK_ID + "=" + values.getAsString(TaskInfo.TASK_ID),
							null);
				}
				
			} else {
				// 新增
				i = db.insert(SqliteHelper.TB_TASK, TaskInfo._ID, values);
			}

		} catch (Exception e) {
			i=-100;
			e.printStackTrace();
		}
		return i;
	}

	/**
	 * 查看任务是否存在
	 * 
	 * @param userId
	 * @return
	 */
	private boolean isHaveTask(String taskId, String userId) {
		boolean b = false;
		try {
			Cursor cursor = db.query(SqliteHelper.TB_TASK, null,
					TaskInfo.USER_ID + "=? and " + TaskInfo.TASK_ID + "=?",
					new String[] { userId, taskId }, null, null, null);
			b = cursor.moveToFirst();
			cursor.close();
		} catch (Exception e) {

		}

		return b;
	}
	public int deleteByRoomId(String roomId) {
		int b = -1;
		try {	
			UserInfo userInfo = MyApp.getInstance().getUserInfo();
			b = db.delete(SqliteHelper.TB_TASK, TaskInfo.ROOM_ID+ "=? and "+TaskInfo.USER_ID +"=?", new String[]
					 {roomId,userInfo.getUserId()});
		} catch (Exception e) {

		}

		return b;
	}
	//
	private boolean equalsTask(String taskId, String userId, String createTime) {
		boolean b = false;
		try {
			Cursor cursor = db.query(SqliteHelper.TB_TASK, null,
					TaskInfo.USER_ID + "=? and " + TaskInfo.TASK_ID + "=? and "
							+ TaskInfo.CREATE_TIME + "=?", new String[] {
							userId, taskId, createTime }, null, null, null);
			b = cursor.moveToFirst();
			cursor.close();
		} catch (Exception e) {

		}

		return b;
	}

	public int delAllTask() {
		// db.delete(SqliteHelper.TB_TASK, TaskInfo.TASK_ID+ "=?", new String[]
		// {values.getAsString(TaskInfo.TASK_ID)});
		return db.delete(SqliteHelper.TB_TASK, null, null);
	}

	public int delTask(String id) {
		try {
			UserInfo userInfo = MyApp.getInstance().getUserInfo();
			return db.delete(SqliteHelper.TB_TASK, TaskInfo.TASK_ID + "=? and "
					+ TaskInfo.USER_ID + "=?",
					new String[] { id, userInfo.getUserId() });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	};

	/****
	 * 获取任务列表
	 * 
	 * @return
	 */
	public ArrayList<TaskInfo> getAllTask(String roomId, String type) {
		UserInfo userInfo = MyApp.getInstance().getUserInfo();
		String userId = userInfo.getUserId();
		String sql = "";
		if (!TextUtils.isEmpty(roomId)) {
			sql = " and " + TaskInfo.ROOM_ID + "=" + roomId;
		}
		if (!TextUtils.isEmpty(type)) {
			sql += " and " + TaskInfo.TASK_TYPE + "='" + type + "'";
		}
		ArrayList<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		Cursor cursor = db.query(SqliteHelper.TB_TASK, null, TaskInfo.USER_ID
				+ "=?" + sql, new String[] { userId }, null, null, null);

		while (cursor.moveToNext()) {
			taskInfos.add(initTaskInfo(cursor));
		}
		if(cursor!=null){
			cursor.close();
		}
		return taskInfos;
	}

	public TaskInfo getTask(String taskId) {
		UserInfo userInfo = MyApp.getInstance().getUserInfo();
		String userId = userInfo.getUserId();
		Cursor cursor = db.query(SqliteHelper.TB_TASK, null, TaskInfo.USER_ID
				+ "=? and " + TaskInfo.TASK_ID + "=?", new String[] { userId,
				taskId }, null, null, null);
		while (cursor.moveToNext()) {
			TaskInfo i= initTaskInfo(cursor);
			if(cursor!=null){
				cursor.close();
			}
			return i;
		}
		return null;
	}

	private TaskInfo initTaskInfo(Cursor cursor) {
		TaskInfo taskInfo = new TaskInfo();
		String jsonStr = cursor.getString(cursor
				.getColumnIndex(TaskInfo.JSON_DATA));
		try {
			taskInfo = (TaskInfo) JsonUtil.jsonToBean(jsonStr, TaskInfo.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return taskInfo;
	}

	// ============任务 end=======================

	public int saveAreaList(List<RoomInfo> list) {
		int row = 0;
		db.beginTransaction(); // 手动设置开始事务
		try {
			for (int i = 0; i < list.size(); i++) {
				RoomInfo info = list.get(i);
				saveMachineAreaInfo(info);
				row++;
			}
			db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction(); // 处理完成
		}
		return row;

	}

	private long saveMachineAreaInfo(RoomInfo info) {
		long i = 0;
		try {
			UserInfo userInfo = MyApp.getInstance().getUserInfo();
			ContentValues values = new ContentValues();
			values.put(RoomInfo.JSON_DATA, JsonUtil.objectToJson(info));
			values.put(RoomInfo.CREATE_TIME, info.getCreateDate());
			values.put(RoomInfo.USER_ID, userInfo.getUserId());

			if (isHaveTask(String.valueOf(info.getId()), userInfo.getUserId())) {
				i = db.update(SqliteHelper.TB_TASK, values, TaskInfo.TASK_ID
						+ "=" + values.getAsString(TaskInfo.TASK_ID), null);
			} else {
				i = db.insert(SqliteHelper.TB_TASK, TaskInfo._ID, values);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
		return i;
	}

}