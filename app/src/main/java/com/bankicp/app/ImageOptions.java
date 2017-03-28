package com.bankicp.app;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 图片缓存设置
 * Created by liuyongfeng on 2016/1/18.
 */
public class ImageOptions {

    /**
     * 头像缓存图片设置常用的设置项
     */
    public static DisplayImageOptions getHeadOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.logo) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.logo)// 设置图片Uri为空或是错误的时候显示的图片
                .cacheInMemory(true)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
                .build();// 构建完成
        return options;
    }

    /**
     * 缓存图片设置常用的设置项
     */
    public static DisplayImageOptions getImageOptions() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.wait)
                .showImageForEmptyUri(R.mipmap.wait)
                .showImageOnFail(R.mipmap.wait)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        return options;
    }
}
