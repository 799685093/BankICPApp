package com.bankicp.app.ui;

import java.util.ArrayList;
import java.util.HashSet;

import com.bankicp.app.R;
import com.bankicp.app.adapter.ImageFolderAdapter;
import com.bankicp.app.model.FileFolder;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.utils.AlertUtils;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

/**
 * @ClassName: LoadFolderOfPhotoActivity
 * @Description: TODO(显示相册时第一层目录即包含图片的文件夹界面)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-24 下午3:55:41
 * 
 */
public class AblumActivity extends BaseFragmentActivity {

	public ArrayList<String> fileInfo;
	private ArrayList<FileInfo> images;
	private static ArrayList<FileFolder> folders;
	public ArrayList<String> imgID = new ArrayList<String>();
	private GridView fGridView;
	private Button check_ablum_folder_cancel_btn;
	/**
	 * @Fields loadingdialog : TODO(加载画面对话框)
	 */
	public Dialog loadingdialog;
	private ImageFolderAdapter adapter;

	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (loadingdialog != null) {
					loadingdialog.dismiss();
					loadingdialog = null;
				}
				if (adapter != null)
					adapter.notifyDataSetChanged();
				break;

			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_loadfolder);
		initViews();
		try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("finish");
			registerReceiver(broadcastReceiver, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initData();
	}

	private void initViews() {
		try {

			fGridView = $(R.id.upload_ablum_folder_check_gridview);
			check_ablum_folder_cancel_btn = $(R.id.check_ablum_folder_cancel_btn);
			check_ablum_folder_cancel_btn
					.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							finish();
						}
					});
			folders = new ArrayList<FileFolder>();
			adapter = new ImageFolderAdapter(mContext, folders);
			fGridView.setAdapter(adapter);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void initData() {
		loadingdialog = AlertUtils.showLoading(mContext);
		if (loadingdialog != null) {
			loadingdialog.show();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				initImage();
			}
		}).start();
	}

	private void initImage() {

		images = getAllImages();
		fileInfo = getFoldersName();
		folders.addAll(getImageFolder(images));
		// 获取对应图片文件夹默认显示的图片
		handler.sendEmptyMessage(1);
	}

	/**
	 * 获取所有的图片
	 * 
	 * @return
	 */
	private ArrayList<FileInfo> getAllImages() {
		ArrayList<FileInfo> images = new ArrayList<FileInfo>();
		ContentResolver cr = mContext.getContentResolver();
		String[] projection = { BaseColumns._ID, MediaColumns.DATA };
		Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection, null, null,
				MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
		// Cursor cursor =
		// cr.query(MediaStore.Images.Media.INTERNAL_CONTENT_URI, projection,
		// null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

		// if
		// (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		// {
		// uri = c.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		// } else {
		// uri = c.insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
		// }
		// 要做没有SD卡时的容错
		// 获取所有图片的路径，格式如：{id=44,
		// path=/storage/sdcard0/Tencent/QzonePic/-185689355.jpeg}
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				int _id;
				String image_path;
				int _idColumn = cursor.getColumnIndex(BaseColumns._ID);
				int dataColumn = cursor.getColumnIndex(MediaColumns.DATA);
				// int date =
				// cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN);
				do {
					_id = cursor.getInt(_idColumn);
					image_path = cursor.getString(dataColumn);
					FileInfo imageInfo = new FileInfo();
					imageInfo.setPath(image_path);
					imageInfo.setId(String.valueOf(_id));
					images.add(imageInfo);
					// System.out.println("cursor.getString(date)====>"+cursor.getString(date));
				} while (cursor.moveToNext());
				cursor.close();
			}
		}
		return images;
	}

	/**
	 * 将图片的路径分割成文件夹
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private ArrayList<String> getFoldersName() {
		ArrayList<String> fileNameList = new ArrayList<String>();
		// 获取各张图片所在的文件夹
		for (int n = 0; n < images.size(); n++) {
			FileInfo fileInfo = images.get(n);

			String[] file = fileInfo.getPath().split("/");
			// for (String string : file) {
			// System.out.println(string);
			// }
			if (file.length > 2) {
				String fileName = file[file.length - 2];
				if (fileInfo.getPath().contains("gpdi")
						|| fileInfo.getPath().contains("GPDI")) {
					if (!fileName.toLowerCase().contains("gpdi")) {
						continue;
					}
				}
				fileNameList.add(fileName);
			}
		}
		HashSet<String> fileInfoSet = new HashSet<String>(fileNameList);
		// 去掉重复项目
		return new ArrayList<String>(fileInfoSet);
		// return fileNameList;
	}

	/***
	 * 获得图片文件夹列表
	 * 
	 * @param images
	 * @return
	 */
	private ArrayList<FileFolder> getImageFolder(ArrayList<FileInfo> images) {
		ArrayList<FileFolder> folders = new ArrayList<FileFolder>();
		for (int i = 0; i < fileInfo.size(); i++) {
			System.out.println("fileName===>" + fileInfo.get(i));
			FileFolder folder = new FileFolder();
			ArrayList<FileInfo> folderImages = new ArrayList<FileInfo>();
			for (FileInfo image : images) {
				if (image.isUse()) {
					continue;
				}
				// String filePath = image.getPath();
				String[] file = image.getPath().split("/");
				String fileName = "";
				if (file.length > 2) {
					fileName = file[file.length - 2];

				}

				if (fileName.equals(fileInfo.get(i))) {
					// FileInfo info = new FileInfo();
					// info.setId(image.getId());
					// info.setPath(image.getPath());
					folderImages.add(image);
					// System.out.println("image=====>"+image.getPath());
					image.setUse(true);
				}

			}
			System.out.println("----------------------------");
			folder.setName(fileInfo.get(i));
			folder.setFileInfos(folderImages);
			folders.add(folder);
		}
		return folders;
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		super.onDestroy();
	}

}
