package com.bankicp.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.view.CompassView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 *
 */
public class CameraActivity extends Activity {
    private static DisplayImageOptions displayOptions;
    /**
     * camera flash mode
     */
    public final static int FLASH_AUTO = 2;
    public final static int FLASH_OFF = 0;
    public final static int FLASH_ON = 1;

    public static final String TAG = "mycamera";

    static {
        displayOptions = DisplayImageOptions.createSimple();
    }

    private MyApp app;
    private CameraManager cameraManager;
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mc_shutter:
                    if (isEnabled) {
                        mDirectionDegree = direction2 + "度";
                        switchBtn(false);
                        cameraManager.takePicture();
                    }
                    break;
                case R.id.ib_camera_change:
                    if (isEnabled) {
                        switchBtn(false);
                        cameraManager.switchCamera();
                    }
                    break;

                case R.id.ib_camera_flash:
                    cameraManager.changeFlash();

                    break;
                case R.id.mc_albums_image:

                    if (app.getChooseImages() != null
                            && app.getChooseImages().size() > 0) {

                        Intent intent = new Intent(mContext,
                                GalleryFileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("imageInfos",
                                app.getChooseImages());
                        bundle.putInt("position", 0);
                        intent.putExtras(bundle);
                        ((Activity) mContext).startActivity(intent);
                    }
                    break;
                case R.id.ib_finish:
                    finish();
                    break;
            }
        }
    };
    private int direction2;// 角度
    private ImageButton ibCameraFlash;

    private ImageButton ibCameraSwitcher;
    private Button ibFinish;// 完成
    private boolean isDirection;// 是否开启指南针

    private boolean isEnabled = true;

    private ImageView ivShutter;
    // 方向
    private final float MAX_ROATE_DEGREE = 1.0f;
    private ImageView mc_albums_image;
    private TextView mc_title;
    // private boolean mChinease;
    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE
                                : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator
                            .getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f
                                    : 0.3f)));
                    mPointer.updateDirection(mDirection);
                }

                updateDirection();

                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };
    private Context mContext;
    private float mDirection;
    private String mDirectionDegree;// 方向
    protected final Handler mHandler = new Handler();
    private AccelerateInterpolator mInterpolator;
    private Sensor mOrientationSensor;

    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);
        }
    };
    private CompassView mPointer;
    private SensorManager mSensorManager;
    private boolean mStopDrawing;
    private float mTargetDirection;

    private SurfaceView previewLayout;

    private String title;

    public void addPic(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        FileInfo imageInfo = new FileInfo();
        imageInfo.setPath(path);
        if (isDirection) {
            imageInfo.setDirection(mDirectionDegree);
        }
        app.getChooseImages().add(imageInfo);
        ImageSize minImageSize = new ImageSize(70, 70); // 70 - approximate size
        // of ImageView in
        // widget
        ImageLoader.getInstance().loadImage("file://" + path, minImageSize,
                displayOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view,
                                                  Bitmap loadedImage) {
                        mc_albums_image.setVisibility(View.VISIBLE);
                        mc_albums_image.setImageBitmap(loadedImage);
                        mc_albums_image.setAdjustViewBounds(true);
                        mc_albums_image.setScaleType(ScaleType.CENTER_CROP);
                    }
                });
    }

    // private CameraButtonAnimation btAnimation = new CameraButtonAnimation() {
    // @Override
    // public void executeAnimation(Animation animation) {
    // ivShutter.startAnimation(animation);
    // ivCameraSwitcher.startAnimation(animation);
    // }
    // };
    public void executeButtonAnimation(Animation animation) {
        ivShutter.startAnimation(animation);
    }

    // RelativeLayout top_lly;
    // RelativeLayout shutter_lly;
    // RelativeLayout imag_lly;
    private void initResources() {

        ivShutter = (ImageView) findViewById(R.id.mc_shutter);
        ibCameraSwitcher = (ImageButton) findViewById(R.id.ib_camera_change);
        ibCameraFlash = (ImageButton) findViewById(R.id.ib_camera_flash);
        previewLayout = (SurfaceView) findViewById(R.id.mc_preview);
        mc_albums_image = (ImageView) findViewById(R.id.mc_albums_image);
        mc_title = (TextView) findViewById(R.id.mc_title);
        ibFinish = (Button) findViewById(R.id.ib_finish);

        mPointer = (CompassView) findViewById(R.id.compass_pointer);
        mc_title = (TextView) findViewById(R.id.mc_title);

        mc_title.setText(title);

        ivShutter.setOnClickListener(clickListener);
        ibCameraSwitcher.setOnClickListener(clickListener);
        ibCameraFlash.setOnClickListener(clickListener);
        ibFinish.setOnClickListener(clickListener);
        mc_albums_image.setOnClickListener(clickListener);
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;

        if (isDirection) {

            mPointer.setVisibility(View.VISIBLE);
            mc_title.setVisibility(View.VISIBLE);
            mc_title.setText(title);
        }
    }

    @SuppressWarnings("deprecation")
    private void initServices() {
        cameraManager = new CameraManager(this, previewLayout);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

    public void onChangeFlashMode(int flashMode) {
        switch (flashMode) {
            case FLASH_AUTO:
                ibCameraFlash.setBackgroundResource(R.drawable.camera_flash_auto);
                break;
            case FLASH_OFF:
                ibCameraFlash.setBackgroundResource(R.mipmap.camera_flash_off);
                break;
            case FLASH_ON:
                ibCameraFlash.setBackgroundResource(R.mipmap.camera_flash_on);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        mContext = this;
        app = (MyApp) getApplication();
        app.getChooseImages().clear();
        title = getIntent().getStringExtra("title");
        isDirection = getIntent().getBooleanExtra("isDirection", false);

        // mc_albums_image.setOnClickListener(this);

        // cameraManager = new CameraManager(this, previewLayout, btAnimation);

        initResources();
        initServices();
    }

    @Override
    protected void onDestroy() {
        cameraManager.releaseCamera();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isDirection) {

            mStopDrawing = true;
            if (mOrientationSensor != null) {
                mSensorManager
                        .unregisterListener(mOrientationSensorEventListener);
            }
        }
        cameraManager.releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.openCamera();
        if (isDirection) {
            if (mOrientationSensor != null) {
                mSensorManager.registerListener(
                        mOrientationSensorEventListener, mOrientationSensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
            mStopDrawing = false;
            mHandler.postDelayed(mCompassViewUpdater, 20);
        }
    }

    public void switchBtn(boolean b) {
        try {
            isEnabled = b;
            ivShutter.setEnabled(b);
            ibCameraSwitcher.setEnabled(b);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    private void updateDirection() {
        // LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
        // LayoutParams.WRAP_CONTENT);

        // mDirectionLayout.removeAllViews();
        // mAngleLayout.removeAllViews();

        // ImageView east = null;
        // ImageView west = null;
        // ImageView south = null;
        // ImageView north = null;
        String mDirection = "";
        float direction = normalizeDegree(mTargetDirection * -1.0f);
        if (direction > 22.5f && direction < 157.5f) {
            // east
            mDirection += "东";
        } else if (direction > 202.5f && direction < 337.5f) {
            // west
            mDirection += "西";
        }

        if (direction > 112.5f && direction < 247.5f) {
            // south
            mDirection += "南";
        } else if (direction < 67.5 || direction > 292.5f) {
            // north
            mDirection += "北";
        }

        direction2 = (int) direction;
        Log.i(TAG, direction2 + "");
        mc_title.setText(title + ":" + direction2 + "度");

        // boolean show = false;
        // if (direction2 >= 100) {
        // // mAngleLayout.addView(getNumberImage(direction2 / 100));
        // direction2 %= 100;
        // show = true;
        // }
        // if (direction2 >= 10 || show) {
        // // mAngleLayout.addView(getNumberImage(direction2 / 10));
        // direction2 %= 10;
        // }
        // mAngleLayout.addView(getNumberImage(direction2));

        // ImageView degreeImageView = new ImageView(this);
        // degreeImageView.setImageResource(R.drawable.degree);
        // degreeImageView.setLayoutParams(lp);
        // mAngleLayout.addView(degreeImageView);
    }

}
