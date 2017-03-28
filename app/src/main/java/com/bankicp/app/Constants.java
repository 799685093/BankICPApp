/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.bankicp.app;

import android.os.Environment;

import java.io.File;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public final class Constants {
    private Constants() {
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

    /***
     * 百度地图
     */
    public static final String mStrKey = "qd5M0n65RqoaUxm1YIIHddsv";
    /***
     * 百度推送
     */

    /**
     * API_KEY
     */
    public final static String API_KEY = "acajmipA7fgEfHvy20902VyY";
    /**
     * SECRET_KEY
     */
    public final static String SECRIT_KEY = "qGIx3Bi4mtyR9dY8Su9YZ6RlfdOmhR6Z";

    public static class Extra {
        public static final String FRAGMENT_NAME = "com.bankicp.app.FRAGMENT_NAME";
        public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    }

    public static final String SPLIT = ",";
    public static final String DATA_NULL = "#";

    public static int DATA_TYPE_TEXT_1 = 1;
    public static int DATA_TYPE_IMAGE_2 = 2;
    public static int DATA_TYPE_DATE_3 = 3;
    public static int DATA_TYPE_INT_4 = 4;
    public static int DATA_TYPE_GPS_5 = 5;
    public static int DATA_TYPE_FILE_6 = 6;
    public static int REQUEST_CODE_TYPE = 6;// 区域
    public static final int REQUEST_CODE_SCAN = 101;
    public static final int FILE_REQUEST_CODE = 6384;// 文件
    public static final int PIC_REQUEST_CODE = 1; // 拍照
    public static final int IMAGE_REQUEST_CODE = 2;// 相册
    public static final int REMARK_REQUEST_CODE = 3;// 添加备注
    public static final int CAM_REQUEST_CODE = 4;// 系统拍照
    public static final int PIC_REMARK_REQUEST_CODE = 5;// 单张拍照备注
    // 每次请求数目
    public static int PAGESIZE = 10;
    // 请求第几页数据
    public static int PAGEINDEX = 1;
    // 搜索项目的接口方法
    public static String PROJECT_SERCH_METHOD = "gp";
    // 搜索专项的接口方法
    public static String SM_SERCH_METHOD = "sm";
    public final static String BASE_PATH = Environment
            .getExternalStorageDirectory() + File.separator;
    public final static String DEFAULT_SAVE_IMAGE_PATH = BASE_PATH + "GPDI"
            + File.separator;
    public final static String DEFAULT_SAVE_LOG_PATH = BASE_PATH + "gpdi"
            + File.separator + "Log";
}
