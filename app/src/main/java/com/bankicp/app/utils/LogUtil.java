/**
 * @Title: LogUtil.java
 * @Package cn.com.gdtec.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-23 下午3:43:04
 * @version V1.0
 */
package com.bankicp.app.utils;

import android.content.Context;
import android.util.Log;

import com.bankicp.app.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @ClassName: LogUtil
 * @Description: TODO(记录日志并生成日志文件)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-23 下午3:43:04
 *
 */
public class LogUtil {

    private static boolean isShow = true; //

    public static void setLogStatus(boolean flag) {
        isShow = flag;
    }

    public static void d(String tag, String msg) {
        if (isShow)
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isShow)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isShow)
            Log.v(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isShow)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isShow)
            Log.w(tag, msg);
    }

    public static void wtf(String tag, String msg) {
        if (isShow)
            Log.wtf(tag, msg);
    }

    /**
     * @Title: recordExceptionLog
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param @param mContext 当前上下文
     * @param @param e Exception对象
     * @param saveTypeStr
     *            保存类型，fals为覆盖保存，true为在原来文件后添加保存
     * @param savePathStr
     *            保存日志路径
     * @param saveFileNameS
     *            保存日志文件名
     * @param extraDate
     *            追加信息
     * @param saveDataStr
     *            保存日志数据
     * @return void 返回类型
     * @throws
     */
    public static void recordExceptionLog(Context mContext,
                                          Exception exception, String FileName, boolean saveTypeStr) {
        if (!isShow) return;
        try {
            DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss",
                    Locale.CHINA);
            String strDateTime = df.format(new Date());
            String savePath = Constants.DEFAULT_SAVE_LOG_PATH;
            String saveFileName = (FileName == null || FileName.equals("") ? strDateTime
                    + ".txt"
                    : FileName + ".txt");
            final Writer result = new StringWriter();
            final PrintWriter printWriter = new PrintWriter(result);
            printWriter.append(exception.getMessage());
            exception.printStackTrace(printWriter);
            printWriter.close();
            String saveData = result.toString();
            boolean saveType = saveTypeStr;

            // 准备需要保存的文件
            File saveFilePath = new File(savePath);
            if (!saveFilePath.exists()) {
                saveFilePath.mkdirs();
            }
            File saveFile = new File(saveFilePath, saveFileName);
            if (!saveType && saveFile.exists()) {
                saveFile.delete();
                saveFile.createNewFile();
                // 保存结果到文件
                FileOutputStream fos = new FileOutputStream(saveFile, saveType);
                fos.write(saveData.getBytes());
                fos.close();
            } else if (saveType && saveFile.exists()) {
                // saveFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(saveFile, saveType);
                fos.write(saveData.getBytes());
                fos.close();
            } else if (saveType && !saveFile.exists()) {
                saveFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(saveFile, saveType);
                fos.write(saveData.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.i("LogException", e + "");
            e.printStackTrace();
        }

    }

    public static void saveTestTxt(String txt, boolean append) {
        if (!isShow) return;
        try {
            String savePath = Constants.DEFAULT_SAVE_LOG_PATH;
            String saveFileName = "000000.txt";

            // 准备需要保存的文件
            File saveFilePath = new File(savePath);
            if (!saveFilePath.exists()) {
                saveFilePath.mkdirs();
            }
            File saveFile = new File(savePath + "/" + saveFileName);
            if (!append) {
                saveFile.delete();
                saveFile.createNewFile();
            }
            txt += "\r\n";
            // 保存结果到文件
            FileOutputStream fos = new FileOutputStream(saveFile, append);
            fos.write(txt.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
