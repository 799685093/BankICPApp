/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.bankicp.app.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.ContrastModel;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.AlertUtils.OnEditListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.FilePagerAdapter;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;

/**
 * 用于查看本地选择的图片 在该界面可添加录音及对相片备注
 */

public class GalleryFileActivity extends Activity {

    private static final int START_RECORD = 10;// 开始录音
    private static final int STOP_RECORD = 10 + 1;// 停止录音

    private static final int START_PLAY = 20;// 开始播放
    protected static final int DELETE_VOICE_FILE = 10 + 2;

    private int RECORDER_TIME_MAXTIME = 60 * 5;
    private GalleryViewPager mViewPager;
    private List<String> items;
    private List<FileInfo> imageInfos;
    private int position = 0;
    // private String title;
    // private TextView textView;
    private Button closeBtn;
    // private EditText editText;
    // private ViewSwitcher mViewSwitcher;
    // private InputMethodManager imm;
    // private String input = "";
    private String imgpath;
    private String name = "";
    private String direction;
    private String remark = "";
    private String voicePath = "";
    private MyApp app;
    // private AudioRecordUtils recordUtils;// 录音工具类
    private MediaPlayer mMediaPlayer = null;
    private Button recordBtn;
    private Button voice_file;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    remark_lly.setVisibility(View.GONE);// 备注
                    voice_del_lly.setVisibility(View.GONE);//
                    voice_del.setVisibility(View.GONE);//
                    recordBtn.setVisibility(View.GONE);

                    // 图片
                    if (!TextUtils.isEmpty(imgpath)) {

                        if (!imgpath.startsWith("http")) {
                            remark_lly.setVisibility(View.VISIBLE);
                            recordBtn.setVisibility(View.VISIBLE);
                            recordBtn.setText("点击录音");
                        }
                    }
                    break;
                case 1:
                    voice_file.setText("语音");
                    recordBtn.setText("点击播放");
                    voice_del_lly.setVisibility(View.VISIBLE);
                    recordBtn.setVisibility(View.VISIBLE);

                    if (voicePath.contains("http://")) {
                        // //只能播放
                        voice_del.setVisibility(View.GONE);
                    } else {
                        // 隐藏
                        voice_del.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    // if(updateThread != null)
                    // mHandler.removeCallbacks(updateThread);
                    // recordBtn.setText("点击播放");
                    break;
                case START_RECORD:
                    // startRecorder();
                    break;
                case STOP_RECORD:
                    // stopRecorder();
                    break;
                case START_PLAY:
                    // doPlay();
                    break;
                case DELETE_VOICE_FILE:
                    // deleteVoiceFile();
                    break;

                default:
                    break;
            }

            super.handleMessage(msg);

        }

    };
    private TextView btm_file_name;
    private Button btm_remark_btn;
    private Button voice_del;
    private LinearLayout voice_del_lly;
    private LinearLayout remark_lly;
    private int from;
    private boolean isEdit;

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_file_activity);
        try {
            app = (MyApp) getApplicationContext();
            items = new ArrayList<String>();
            // 获取传入的数据
            Bundle bundle = getIntent().getExtras();
            from = bundle.getInt("from", -1);
            if (from == HomeFragment.INDEX) {
                ArrayList<ContrastModel> contrastModelList = (ArrayList<ContrastModel>) bundle
                        .getSerializable("imageInfos");
                if (contrastModelList != null) {
                    int choosePosition = app.getChoosePosition();
                    if (contrastModelList.size() > choosePosition) {
                        ContrastModel contrastModel = contrastModelList
                                .get(choosePosition);
                        imageInfos = new ArrayList<FileInfo>();
                        imageInfos.add(contrastModel.getBeforeFileInfo());
                        imageInfos.add(contrastModel.getAfterFileInfo());
                        position = bundle.getInt("position", 0);
                        isEdit = bundle.getBoolean("isEdit", false);
                    }

                }
            } else {
                imageInfos = (List<FileInfo>) bundle
                        .getSerializable("imageInfos");// 文件列表
                position = bundle.getInt("position", 0);
                isEdit = bundle.getBoolean("isEdit", false);
            }
            // imageInfos = (ArrayList<FileInfo>)
            // bundle.getSerializable("imageInfos");//文件列表
            // position = bundle.getInt("position", 0);
            if (imageInfos != null) {
                for (FileInfo info : imageInfos) {
                    if (info.getType() != -1) {
                        items.add(info.getPath());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        // recordUtils = new AudioRecordUtils();

        remark_lly = (LinearLayout) findViewById(R.id.remark_lly);
        voice_del_lly = (LinearLayout) findViewById(R.id.voice_del_lly);
        closeBtn = (Button) findViewById(R.id.header_close);
        recordBtn = (Button) findViewById(R.id.record_btn);
        voice_file = (Button) findViewById(R.id.voice_file);
        btm_file_name = (TextView) findViewById(R.id.btm_file_name);
        btm_remark_btn = (Button) findViewById(R.id.btm_remark_btn);
        voice_del = (Button) findViewById(R.id.voice_del);

        // name =
        // TextUtils.isEmpty(imageInfos.get(position).getName())?"":imageInfos.get(position).getName();
        // remark =
        // TextUtils.isEmpty(imageInfos.get(position).getRemarks())?"":imageInfos.get(position).getRemarks();
        //
        // btm_file_name.setText("文件名："+name+remark);

        closeBtn.setOnClickListener(onClickListener);
        recordBtn.setOnClickListener(onClickListener);
        voice_del.setOnClickListener(onClickListener);

        btm_remark_btn.setOnClickListener(onClickListener);
        btm_file_name.setOnClickListener(onClickListener);

        FilePagerAdapter pagerAdapter = new FilePagerAdapter(this, items);
        pagerAdapter.setOnItemChangeListener(new BasePagerAdapter.OnItemChangeListener() {

            @Override
            public void onItemChange(int currentPosition) {
                if (isRecording) {
                    // stopRecorder();
                }
                position = currentPosition;
                voicePath = imageInfos.get(position).getVoicePath();
                imgpath = imageInfos.get(position).getPath();

                setFileName();

                // mHandler.sendEmptyMessage(0);

                // 音频
                if (!TextUtils.isEmpty(voicePath)
                        && (voicePath.startsWith("http") || new File(voicePath).exists())) {
                    // mHandler.sendEmptyMessage(1);
                }

            }

        });
        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);
    }

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.top_title:
                    break;
                case R.id.header_close:// 完成
                    // release();
                    addImage();
                    finish();
                    break;
                case R.id.record_btn:// 开始
                    if (!isRecording) {
                        if (voice_del_lly.getVisibility() == View.VISIBLE) {
                            mHandler.sendEmptyMessage(START_PLAY);
                        } else {
                            mHandler.sendEmptyMessage(START_RECORD);
                        }
                    } else {
                        mHandler.sendEmptyMessage(STOP_RECORD);
                    }
                    break;
                case R.id.voice_del:
                    mHandler.sendEmptyMessage(DELETE_VOICE_FILE);

                    break;
                case R.id.btm_file_name:

                case R.id.btm_remark_btn:
                    if (from == 5) {
                        return;
                    }
                    AlertUtils.showEdit(GalleryFileActivity.this,
                            imageInfos.get(position).getRemarks(),
                            new OnEditListener() {

                                @Override
                                public void onTextChanged(String str) {
                                    // System.out.println(str);
                                    remark = str;
                                    // if(!TextUtils.isEmpty(str)){
                                    // imageInfos.get(position).setRemarks(str);
                                    // }
                                    // btm_file_name.setText("文件名："+name+position+"_"+remark);
                                }

                                @Override
                                public void onEditFinish(String str) {
                                    // System.out.println(str);
                                    if (!TextUtils.isEmpty(str)) {
                                        imageInfos.get(position).setRemarks(str);
                                        setFileName();
                                    }
                                }
                            });
                    break;
                default:
                    break;
            }

        }

    };

    private String savePath;
    private String fileName;
    private boolean isRecording = false;
    // private boolean isPlaying = false;
    private long startVoiceT = 0;// 开始时间
    private long endVoiceT = 0;// 结束世间
    private int voiceLen = 0;

    // 开始录制语音
    // private void startRecorder() {
    //
    // savePath = getRecorderPath();
    // // 没有挂载SD卡，无法保存文件
    // if (TextUtils.isEmpty(savePath)) {
    // Toast.makeText(this, "请检查SD卡是否挂载", Toast.LENGTH_SHORT ).show();
    // return;
    // }
    // fileName = System.currentTimeMillis()+ ".mp3";// 语音命名
    //
    //
    // isRecording = true;
    // voiceLen = 0;
    // startVoiceT = System.currentTimeMillis();
    //
    // recordBtn.setText("停止录音");
    // voice_del_lly.setVisibility(View.VISIBLE);
    // voice_file.setText(Util.formatLongToTimeStr(voiceLen));
    //
    // recordUtils.start(savePath, fileName);
    // mHandler.postDelayed(mTimerTask, 1000);
    // }

    // 停止录音操作
    // private void stopRecorder() {
    // endVoiceT = System.currentTimeMillis();
    // long voiceT = endVoiceT - startVoiceT;
    // isRecording = false;
    // mHandler.removeCallbacks(mTimerTask);
    // recordUtils.stop();
    //
    // voiceLen = (int) (voiceT/1000);
    //
    // voicePath = savePath + fileName;
    // if (!TextUtils.isEmpty(voicePath) && new File(voicePath).exists()) {
    // imageInfos.get(position).setVoicePath(voicePath);
    // voice_del_lly.setVisibility(View.VISIBLE);
    // voice_file.setText(Util.formatLongToTimeStr(voiceLen));
    // }
    // recordBtn.setText("点击播放");
    // }
    // // 删除录音文件
    // private void deleteVoiceFile() {
    // release();
    // voice_file.setText("00:00");
    // voice_del_lly.setVisibility(View.GONE);
    // try {
    // File newPath = new File(voicePath);
    // if(newPath.exists()){
    // newPath.delete();
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // imageInfos.get(position).setVoicePath(null);
    // recordBtn.setText("点击录音");
    // // boolean res = newPath.delete();
    // //
    // }

    /***
     * 释放录音及播放器
     */
    // private void release() {
    // if(isRecording){
    // stopRecorder();
    // }
    // if (mMediaPlayer != null) {
    // if (mMediaPlayer.isPlaying()) {
    // mMediaPlayer.stop();
    // mMediaPlayer.reset();
    // }
    // if(updateThread != null)
    // mHandler.removeCallbacks(updateThread);
    // mMediaPlayer.release();
    // mMediaPlayer=null;
    // }
    // }
    // Runnable updateThread = new Runnable(){
    // public void run() {
    // //获得歌曲现在播放位置并设置成播放进度条的值
    // if (mMediaPlayer != null) {
    // int time =mMediaPlayer.getCurrentPosition()/ 1000;
    // voice_file.setText(Util.formatLongToTimeStr(time));
    // }
    // //每次延迟100毫秒再启动线程
    // mHandler.postDelayed(updateThread, 100);
    // }
    // };
    // 播放音频
    // private void doPlay() {
    // recordBtn.setText("点击停止");
    // if (!TextUtils.isEmpty(voicePath)) {
    // try {
    // if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
    // mMediaPlayer.stop();
    // mMediaPlayer.reset();
    // mHandler.sendEmptyMessage(2);
    // return ;
    // }
    // if (mMediaPlayer == null) {
    // mMediaPlayer = new MediaPlayer();
    // }
    //
    //
    // mMediaPlayer.reset();
    // mMediaPlayer.setDataSource(voicePath);
    // mMediaPlayer.prepare();
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // mMediaPlayer.start();
    // mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
    //
    // @Override
    // public void onPrepared(MediaPlayer mp) {
    // // TODO Auto-generated method stub
    // int time = mp.getDuration() / 1000;
    // voice_file.setText(Util.formatLongToTimeStr(time));
    // mHandler.post(updateThread);
    // }
    // });
    // mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
    //
    // @Override
    // public void onCompletion(MediaPlayer mp) {
    // mp.stop();
    // mp.reset();
    // mHandler.sendEmptyMessage(2);
    //
    // }
    // });
    //
    // mMediaPlayer.setOnErrorListener(new OnErrorListener() {
    //
    // @Override
    // public boolean onError(MediaPlayer mp, int arg1, int arg2) {
    // mp.stop();
    // mp.reset();
    // mHandler.sendEmptyMessage(2);
    // return false;
    // }
    // });
    // }
    //
    // }
    // 录音计时
    // private Runnable mTimerTask = new Runnable() {
    // // int i = 0;
    // @Override
    // public void run() {
    // if (!isRecording) return;
    // voiceLen++;
    // if (voiceLen == RECORDER_TIME_MAXTIME) {
    // Toast.makeText(GalleryFileActivity.this, "录音超时", Toast.LENGTH_SHORT
    // ).show();
    // return;
    // }
    // voice_file.setText(Util.formatLongToTimeStr(voiceLen));
    // mHandler.postDelayed(mTimerTask, 1000);
    // }
    //
    // };
    private String getRecorderPath() {
        String savePath = "";
        try {
            // 判断是否挂载了SD卡
            String storageState = Environment.getExternalStorageState();
            if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                savePath = Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/GPDI/";// 存放照片的文件夹

                File savedir = new File(savePath);
                if (!savedir.exists()) {
                    savedir.mkdirs();
                }
            }
        } catch (Exception e) {
        }
        return savePath;
    }

    // 隐藏输入状态
    // private void hideEditor(View v) {
    // imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    // if(mViewSwitcher.getDisplayedChild()==1){
    // mViewSwitcher.setDisplayedChild(0);
    // editText.clearFocus();
    // editText.setVisibility(View.GONE);
    //
    // }
    // name =
    // TextUtils.isEmpty(imageInfos.get(position).getName())?"":imageInfos.get(position).getName();
    // remark =
    // TextUtils.isEmpty(imageInfos.get(position).getRemarks())?"":imageInfos.get(position).getRemarks();
    //
    // textView.setText(name + remark);
    // }
    @Override
    protected void onPause() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        // release();
        super.onDestroy();
    }

    // @Override
    // public void onBackPressed() {
    // if (null != this.player && this.player.isPlaying()) {
    // this.player.stop();
    // this.player.release();
    // }
    // super.onBackPressed();
    // }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            addImage();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void addImage() {
        if (imageInfos != null && imageInfos.size() > 0) {
            app.getChooseImages().clear();
            app.getChooseImages().addAll(imageInfos);
        }
    }

    public void setFileName() {
        name = TextUtils.isEmpty(imageInfos.get(position).getName()) ? ""
                : imageInfos.get(position).getName();
        remark = TextUtils.isEmpty(imageInfos.get(position).getRemarks()) ? ""
                : imageInfos.get(position).getRemarks();
        direction = (TextUtils.isEmpty(imageInfos.get(position).getDirection()) ? ""
                : "_" + imageInfos.get(position).getDirection());
        if (from == HomeFragment.INDEX) {
            btm_file_name.setText("文件名：" + name + "_"
                    + (app.getChoosePosition() + 1) + direction + "_" + remark);
        } else {
            btm_file_name.setText("文件名：" + name + "_" + (position + 1)
                    + direction + "_" + remark);
        }

    }
}