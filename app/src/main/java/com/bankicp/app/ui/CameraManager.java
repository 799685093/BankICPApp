package com.bankicp.app.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.bankicp.app.utils.LogUtil;
import com.bankicp.app.utils.ToastUtils;

/**
 * Created by madmatrix on 2014/24/02.
 */
@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class CameraManager {
	private static final String TAG = CameraActivity.TAG;

	private Camera mCamera;

	/**
	 * 前置/后置摄像头
	 */
	private int facing;
	private CameraActivity cameraActivity;
	private CameraConfiguration configuration;
	private CameraPreview cameraPreview;
	private MyOrientationEventListener orientationEventListener;

	/**
	 * 记录手机的上一个方向
	 */
	private int lastAutofocusOrientation = 0;

	/**
	 * 按钮的上一次角度
	 */
	private int lastBtOrientation = 0;

	// private CameraButtonAnimation buttonAnimation;

	private SurfaceView surfaceView;

	public CameraManager(CameraActivity cameraActivity, SurfaceView surfaceView) {
		this.cameraActivity = cameraActivity;
		configuration = new CameraConfiguration(cameraActivity);
		facing = Camera.CameraInfo.CAMERA_FACING_BACK;
		this.surfaceView = surfaceView;
		cameraPreview = new CameraPreview();
		orientationEventListener = new MyOrientationEventListener(
				cameraActivity);
		// this.buttonAnimation = buttonAnimation;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public Camera openCamera() {
		releaseCamera();
		Log.d(TAG, "open the " + getFacingDesc(facing) + " camera");

		if (facing != Camera.CameraInfo.CAMERA_FACING_BACK
				&& facing != Camera.CameraInfo.CAMERA_FACING_FRONT) {
			Log.w(TAG, "invalid facing " + facing
					+ ", use default CAMERA_FACING_BACK");
			facing = Camera.CameraInfo.CAMERA_FACING_BACK;
		}

		int numCameras = Camera.getNumberOfCameras();
		if (numCameras == 0) {
			return null;
		}

		int index = 0;
		while (index < numCameras) {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(index, cameraInfo);
			if (cameraInfo.facing == facing) {
				break;
			}
			++index;
		}

		if (index < numCameras) {
			mCamera = Camera.open(index);
		} else {
			mCamera = Camera.open(0);
		}
		initFlashMode();
		configuration.config(mCamera);
		cameraPreview.startPreview();
		if (orientationEventListener != null) {
			orientationEventListener.enable();
		}

		return mCamera;
	}

	public void initFlashMode() {
		if (mCamera == null) {
			return;
		}
		try {

			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters == null) {
				return;
			}
			String flashMode = parameters.getFlashMode();
			// if(flashMode == null){
			// return;
			// }
			if (Camera.Parameters.FLASH_MODE_AUTO.equals(flashMode)) {
				flash_type = FLASH_AUTO;
			} else if (Camera.Parameters.FLASH_MODE_ON.equals(flashMode)) {
				flash_type = FLASH_ON;
			} else {
				flash_type = FLASH_OFF;
			}
			changeFlash();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// long s=0,e=0,c=0;
	/**
	 * 拍照
	 */
	public void takePicture() {
		// isPicSave = true;
		// s = System.currentTimeMillis();
		mCamera.takePicture(null, null, pictureCallback);
	}

	/**
	 * 切换前置/后置摄像头
	 */
	public void switchCamera() {

		if (Camera.CameraInfo.CAMERA_FACING_BACK == facing) {
			facing = Camera.CameraInfo.CAMERA_FACING_FRONT;
		} else {
			facing = Camera.CameraInfo.CAMERA_FACING_BACK;
		}

		releaseCamera();
		openCamera();
		cameraActivity.switchBtn(true);
		// cameraPreview.startPreview();
	}

	public void releaseCamera() {
		if (orientationEventListener != null) {
			orientationEventListener.disable();
		}
		flash_type = FLASH_AUTO;
		if (null != mCamera) {
			if (null != cameraPreview)
				cameraPreview.stopPreview();
			mCamera.release();
			mCamera = null;
		}

	}

	// private boolean isPicSave = false;

	private class PicSaveTask extends AsyncTask<byte[], Void, File> {
		@Override
		protected File doInBackground(byte[]... datas) {
			byte[] data = datas[0];
			File pictureFile = null;
			try {

				pictureFile = configuration.getPictureStorageFile();
			} catch (Exception e) {

			}
			try {
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				// Bitmap bitmap = BitmapFactory.decodeByteArray(data,
				// 0,data.length);
				// FileOutputStream fos;
				// System.gc();
				// fos = new FileOutputStream(pictureFile);
				// BufferedOutputStream bos = new BufferedOutputStream(fos);
				// bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				// bos.flush();
				// bos.close();
				// bitmap.recycle();
				Intent mediaScanIntent = new Intent(
						Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
				Uri contentUri = Uri.fromFile(pictureFile);
				mediaScanIntent.setData(contentUri);
				cameraActivity.sendBroadcast(mediaScanIntent);
			} catch (FileNotFoundException e) {
				LogUtil.saveTestTxt("File not found: " + e.getMessage(), true);
				Log.d(TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				LogUtil.saveTestTxt("File not found: " + e.getMessage(), true);
				Log.d(TAG, "Error accessing file: " + e.getMessage());
			}

			return pictureFile;
		}

		@Override
		protected void onPostExecute(File pictureFile) {
			super.onPostExecute(pictureFile);
			try {
				if (cameraActivity == null && cameraActivity.isFinishing()) {
					return;
				}
				cameraActivity.addPic(pictureFile.getAbsolutePath());
				// cameraActivity.switchBtn(true);
				// isPicSave = false;
				ToastUtils.showToast(cameraActivity, "图片保存图库成功！",
						Toast.LENGTH_SHORT);
				// Log.d(TAG,"the picture saved in "+
				// pictureFile.getAbsolutePath());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			if (null == data || data.length == 0) {
				ToastUtils.showToast(cameraActivity, "拍照失败，请重试！",
						Toast.LENGTH_SHORT);

				// Log.e(TAG, "No media data returned");
				cameraActivity.switchBtn(true);
				// isPicSave = false;
				return;
			}
			// System.out.println("currenttimemillis"+(System.currentTimeMillis()-s));
			mCamera.stopPreview();
			new PicSaveTask().execute(data);
			cameraActivity.switchBtn(true);
			mCamera.startPreview();

		}

	};

	private class MyOrientationEventListener extends OrientationEventListener {

		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
			try {
				if (orientation == ORIENTATION_UNKNOWN)
					return;

				// 设置camera旋转角度以使最终照片与预览界面方向一致
				Camera.CameraInfo info = new Camera.CameraInfo();
				Camera.getCameraInfo(facing, info);
				int fixedOrientation = (orientation + 45) / 90 * 90;

				int rotation = 0;
				if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					rotation = (info.orientation - fixedOrientation + 360) % 360;
				} else { // back-facing camera
					rotation = (info.orientation + fixedOrientation) % 360;
				}

				if (mCamera != null) {
					Camera.Parameters cameraParameters = mCamera
							.getParameters();
					cameraParameters.setRotation(rotation);
					mCamera.setParameters(cameraParameters);
				}

				// 手机旋转超过30°就重新对焦
				// 处理启动时orientation临界值情况
				if (orientation > 180 && lastAutofocusOrientation == 0) {
					lastAutofocusOrientation = 360;
				}
				if (Math.abs(orientation - lastAutofocusOrientation) > 30) {
					// Log.d(TAG, "orientation=" + orientation +
					// ", lastAutofocusOrientation=" +
					// lastAutofocusOrientation);

					// if(!isPicSave){
					// autoFocusManager.autoFocus();
					// if (camera != null) {
					// camera.autoFocus(null);// 自动对焦
					// }
					// }
					lastAutofocusOrientation = orientation;
				}

				// 使按钮随手机转动方向旋转
				// 按钮图片的旋转方向应当与手机的旋转方向相反，这样最终方向才能保持一致
				int phoneRotation = 0;
				if (orientation > 315 && orientation <= 45) {
					phoneRotation = 0;
				} else if (orientation > 45 && orientation <= 135) {
					phoneRotation = 90;
				} else if (orientation > 135 && orientation <= 225) {
					phoneRotation = 180;
				} else if (orientation > 225 && orientation <= 315) {
					phoneRotation = 270;
				}

				// 恢复自然方向时置零
				if (phoneRotation == 0 && lastBtOrientation == 360) {
					lastBtOrientation = 0;
				}

				// "就近处理"：为了让按钮旋转走"捷径"，如果起始角度与结束角度差超过180，则将为0的那个值换为360
				if ((phoneRotation == 0 || lastBtOrientation == 0)
						&& (Math.abs(phoneRotation - lastBtOrientation) > 180)) {
					phoneRotation = phoneRotation == 0 ? 360 : phoneRotation;
					lastBtOrientation = lastBtOrientation == 0 ? 360
							: lastBtOrientation;
				}

				if (phoneRotation != lastBtOrientation) {
					int fromDegress = 360 - lastBtOrientation;
					int toDegrees = 360 - phoneRotation;

					Log.i(TAG, "fromDegress=" + fromDegress + ", toDegrees="
							+ toDegrees);

					RotateAnimation animation = new RotateAnimation(
							fromDegress, toDegrees, Animation.RELATIVE_TO_SELF,
							0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
					animation.setDuration(1000);
					animation.setFillAfter(true);
					cameraActivity.executeButtonAnimation(animation);
					lastBtOrientation = phoneRotation;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static String getFacingDesc(int facing) {
		if (facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			return "front";
		} else {
			return "back";
		}
	}

	/**
	 * 固定portrait模式下，无需调用此函数
	 */
	@SuppressWarnings("unused")
	private void setCameraDisplayOrientation() {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(facing, info);
		int rotation = cameraActivity.getWindowManager().getDefaultDisplay()
				.getRotation();

		Log.d(TAG, "[1492]rotation=" + rotation);

		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		Log.d(TAG, "[1492]info.orientation=" + info.orientation + ", degrees="
				+ degrees);

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		Log.d(TAG, "[1492]setCameraDisplayOrientation, result=" + result);

		mCamera.setDisplayOrientation(result);
	}

	private class CameraPreview implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;

		public CameraPreview() {
			mHolder = surfaceView.getHolder();
			mHolder.addCallback(this);
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void startPreview() {
			try {

				// --
				Camera.Parameters cameraParameters = mCamera.getParameters();
				// cameraParameters.setPreviewFrameRate(3);// 每秒3帧

				Size largestSize = getBestSupportedSize(cameraParameters
						.getSupportedPreviewSizes());
				cameraParameters.setPreviewSize(largestSize.width,
						largestSize.height);// 设置预览图片尺寸
				largestSize = getBestSupportedSize(cameraParameters
						.getSupportedPictureSizes());// 设置捕捉图片尺寸
				cameraParameters.setPictureSize(largestSize.width,
						largestSize.height);
				mCamera.setParameters(cameraParameters);
				// --
				mCamera.setPreviewDisplay(mHolder);
				mCamera.setDisplayOrientation(90);

				mCamera.startPreview();
				mCamera.setAutoFocusMoveCallback(null);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		}

		public void stopPreview() {
			try {
				mCamera.stopPreview();
			} catch (Exception e) {

			}
		}

		/**
		 * 取能适用的最大的SIZE
		 * 
		 * @param sizes
		 * @return
		 */
		private Size getBestSupportedSize(List<Size> sizes) {
			// 取能适用的最大的SIZE
			Size largestSize = sizes.get(0);
			int largestArea = sizes.get(0).height * sizes.get(0).width;
			for (Size s : sizes) {
				int area = s.width * s.height;
				if (area > largestArea) {
					largestArea = area;
					largestSize = s;
				}
			}
			return largestSize;
		}

		@Override
		public void surfaceCreated(SurfaceHolder surfaceHolder) {
			// System.out.println("surfaceCreated++++++++++++>");
			// startPreview();
		}

		@Override
		public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
				int w, int h) {
			if (surfaceHolder.getSurface() == null) {
				return;
			}

			stopPreview();
			startPreview();

		}

		@Override
		public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

			System.out.println("surfaceDestroyed++++++++++++>");
			// releaseCamera();

		}
	}

	private int flash_type = FLASH_AUTO; // 0 close , 1 open , 2 auto

	/**
	 * change camera flash mode
	 */
	/**
	 * change camera flash mode
	 */
	public final void changeFlash() {
		if (mCamera == null) {
			return;
		}
		Camera.Parameters parameters = mCamera.getParameters();
		List<String> FlashModes = parameters.getSupportedFlashModes();
		if (FlashModes == null) {
			return;
		}

		if (cameraActivity != null) {
			cameraActivity.onChangeFlashMode(flash_type % 3);
		}

		Log.i(TAG, "camera-flash-type:" + flash_type);
		switch (flash_type % 3) {
		case FLASH_ON:
			if (FlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
				flash_type++;
				mCamera.setParameters(parameters);
			}
			break;
		case FLASH_OFF:
			if (FlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				flash_type++;
				mCamera.setParameters(parameters);
			}
			break;
		case FLASH_AUTO:
			if (FlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				flash_type++;
				mCamera.setParameters(parameters);
			}
			break;
		default:
			// if (FlashModes.contains(Camera.Parameters.FLASH_MODE_OFF)) {
			// parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			// flash_type++;
			// camera.setParameters(parameters);
			// }
			break;
		}
	}

	/**
	 * camera flash mode
	 */
	public final static int FLASH_AUTO = 2;
	public final static int FLASH_OFF = 0;
	public final static int FLASH_ON = 1;
}
