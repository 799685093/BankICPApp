package com.bankicp.app.utils;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.TextView;

import com.bankicp.app.AppManager;
import com.bankicp.app.Constants;
import com.bankicp.app.MyApp;
import com.bankicp.app.model.AreaInfo;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.model.InspectionTaskModel;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.RoleInfo;
import com.bankicp.app.ui.LoginActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * * Copyright (C) 2010 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. --------------------------------------- PLEASE READ
 * --------------------------------------- CopyRight (c) 广东电信工程有限公司 信息技术研发中心
 * 项目名称：GDTEC_Spot 类名称：Util 类描述： 创建人：huangchunfei 修改人：Administrator
 * 创建时间：2013年10月23日 下午2:36:00 修改时间：2013年10月23日 下午2:36:00
 */
@SuppressLint({"DefaultLocale", "SimpleDateFormat"})
public class Util {

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @SuppressLint("SimpleDateFormat")
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @SuppressLint("SimpleDateFormat")
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 对空字符串处理
     *
     * @return
     */

    public static void boldText(TextView tv, boolean b) {
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(b);
    }

    public static ArrayList<String> collect(String divcollect) {
        ArrayList<String> collects = new ArrayList<String>();

        String[] collectArr = divcollect.split(",");
        for (int i = 0; i < collectArr.length; i++) {

            collects.add(collectArr[i]);

        }
        return collects;
    }

    public static ArrayList<FileInfo> collect(String ids, String names,
                                              String directions, String paths, String types, String remarks,
                                              String voices) {
        ArrayList<FileInfo> collects = new ArrayList<FileInfo>();
        if (!TextUtils.isEmpty(ids) && !TextUtils.isEmpty(paths)
                && !TextUtils.isEmpty(types) && !TextUtils.isEmpty(names)
                && !TextUtils.isEmpty(remarks) && !TextUtils.isEmpty(voices)) {

            String[] idArr = ids.split(Constants.SPLIT);
            String[] nameArr = names.split(Constants.SPLIT);
            String[] directionArr = directions.split(Constants.SPLIT);
            String[] pathArr = paths.split(Constants.SPLIT);
            String[] typeArr = types.split(Constants.SPLIT);
            String[] remarkArr = remarks.split(Constants.SPLIT);
            String[] voiceArr = voices.split(Constants.SPLIT);

            for (int i = 0; i < pathArr.length; i++) {
                FileInfo fileInfo = new FileInfo();
                if (i < idArr.length) {
                    fileInfo.setId(getValue(idArr[i]));
                }
                if (i < nameArr.length) {
                    fileInfo.setName(getValue(nameArr[i]));
                }
                if (i < directionArr.length) {
                    fileInfo.setDirection(getValue(directionArr[i]));
                }
                if (i < pathArr.length) {
                    fileInfo.setPath(getValue(pathArr[i]));
                }
                if (i < typeArr.length) {
                    fileInfo.setType(getStringToInt(typeArr[i]));
                }
                if (i < remarkArr.length) {
                    fileInfo.setRemarks(getValue(remarkArr[i]));
                }

                if (i < voiceArr.length) {
                    fileInfo.setVoicePath(getValue(voiceArr[i]));
                }
                collects.add(fileInfo);

            }
        }
        return collects;
    }

    @SuppressLint("SimpleDateFormat")
    public static String DatetoString(Date date) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        java.sql.Date sDate = new java.sql.Date(date.getTime());
        String strDate = df.format(sDate);
        return strDate;

    }

    public static String formatLongToTimeStr(int second) {
        int minute = 0;
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }

        String strtime = (minute > 9 ? minute : "0" + minute) + "："
                + (second > 9 ? second : "0" + second);
        return strtime;

    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    public static boolean getBoolean(String s) {
        try {
            if ("true".equals(s) || "1".equals(s)) {
                return true;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return false;
    }

    // 获取本地IP函数
    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> mEnumeration = NetworkInterface
                    .getNetworkInterfaces(); mEnumeration.hasMoreElements(); ) {
                NetworkInterface intf = mEnumeration.nextElement();
                for (Enumeration<InetAddress> enumIPAddr = intf
                        .getInetAddresses(); enumIPAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIPAddr.nextElement();
                    // 如果不是回环地址
                    if (!inetAddress.isLoopbackAddress()) {
                        // 直接返回本地IP地址
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            System.err.print("error");
        }
        return null;
    }

    /**
     * 对空字符串处理
     *
     * @param s
     * @return
     */
    public static String getNotNullString(String s) {
        return getNotNullString(s, "");
    }

    /**
     * 对空字符串处理
     *
     * @param s
     * @param def
     * @return
     */
    public static String getNotNullString(String s, String def) {
        // String str = "";
        try {
            if (s == null || "".equals(s) || "null".equals(s)) {
                return def;
            }
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    // 获取当前时间
    public static String getNowDateTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return year + "-" + (month < 10 ? ("0" + month) : month) + "-"
                + (day < 10 ? ("0" + day) : day) + " "
                + (hour < 10 ? ("0" + hour) : hour) + ":"
                + (min < 10 ? ("0" + min) : min);
    }

    // 获取当前时间包括年月日时分秒
    public static String getNowDateTimeWhole() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return year + "年" + (month + 1) + "月" + day + "日 "
                + (hour < 10 ? ("0" + hour) : hour) + ":"
                + (min < 10 ? ("0" + min) : min);
    }

    public static String getNowDay() {
        return getNowDay(0);
    }

    public static String getNowDay(int d) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        // calendar.add(Calendar.YEAR,y);
        // calendar.add(Calendar.MONTH,m);
        calendar.add(Calendar.DAY_OF_MONTH, d);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        return year + "-" + (month < 10 ? ("0" + month) : month) + "-"
                + (day < 10 ? ("0" + day) : day);
    }

    // 获取当前时间
    public static String getNowDayCN() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "年" + (month + 1) + "月" + day + "日";
    }

    // 当View达到最大字数长度后最后一个字符转为"……"

    /**
     * 描述：获取当前版本号 作者：陈培伟 时间：2013-1-28 下午03:55:40 返回值：int
     */
    // public static String getCurrentVersion(Context context) {
    // PackageInfo info;
    //
    // try {
    //
    // info = context.getPackageManager().getPackageInfo(
    // context.getPackageName(), 0);
    //
    // return info.versionName;
    //
    // } catch (NameNotFoundException e) {
    //
    // e.printStackTrace();
    //
    // }
    // return "1";
    // }

    // 获取文件后缀名
    // public static String getExtensionName(String filename) {
    // if ((filename != null) && (filename.length() > 0)) {
    // int dot = filename.lastIndexOf('.');
    // if ((dot > -1) && (dot < (filename.length() - 1))) {
    // return filename.substring(dot + 1);
    // }
    // }
    // return filename;
    // }

    // 获取随机数
    public static String getRandomStr() {
        String s = "";
        Random ran = new Random(System.currentTimeMillis());
        for (int i = 0; i < 8; i++) {
            s = s + ran.nextInt(10);
        }

        return s;

    }

    private static int getStringToInt(String v) {

        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;

    }

    private static String getValue(String v) {
        String value = "";
        try {
            if (!TextUtils.isEmpty(v) && !"null".equals(v)
                    && !Constants.DATA_NULL.equals(v)) {
                value = v;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;

    }

    @SuppressWarnings("unused")
    private static String intToIp(int i) {

        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
                + "." + (i >> 24 & 0xFF);
    }

    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(String str) {
        try {
            if (str == null || str.length() == 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     *
     * @param context
     * @return true 表示开启
     */
    public static final boolean isOPen(final Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        // boolean network =
        // locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    public static Result processResult(String result) {
        Result res = new Result();
        try {
            if (result != null) {
                JSONObject json = new JSONObject(result);
                res.setCode(json.optInt("Code", 0));
                res.setMsg(json.optString("Msg", ""));
                res.setData(json.optString("Data"));
                return res;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static List<RoleInfo> getGsonToList(String s) {
        try {

            Gson gson = new Gson();
            List<RoleInfo> retList = gson.fromJson(s,
                    new TypeToken<List<RoleInfo>>() {
                    }.getType());
            return retList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public static List<InspectionTaskModel> getGsonToITaskList(String s) {
        try {

            Gson gson = new Gson();
            List<InspectionTaskModel> retList = gson.fromJson(s,
                    new TypeToken<List<InspectionTaskModel>>() {
                    }.getType());
            return retList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public static List<AreaInfo> getGsonToList2(String s) {
        try {

            Gson gson = new Gson();
            List<AreaInfo> retList = gson.fromJson(s,
                    new TypeToken<List<AreaInfo>>() {
                    }.getType());

            return retList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    public static String listToGson(List<RoleInfo> roles) {
        try {
            Gson gson = new Gson();
            gson.toJson(roles);
            return gson.toJson(roles);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToGson2(List<AreaInfo> roles) {
        try {
            Gson gson = new Gson();
            gson.toJson(roles);
            return gson.toJson(roles);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    // MD5加密
    public static String MD5(String str) {

        try {
            if (str.equals(null) || str.equals("")) {
                return str;
            }

        } catch (Exception e) {

            return str;

        }
        StringBuffer hexString = null;

        byte[] defaultBytes = str.getBytes();

        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(defaultBytes);
            byte messageDigest[] = algorithm.digest();

            hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                if (Integer.toHexString(0xFF & messageDigest[i]).length() == 1) {
                    hexString.append(0);
                }
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            }
            messageDigest.toString();
            str = hexString + "";
        } catch (NoSuchAlgorithmException nsae) {
            return "";
        }

        return hexString.toString().toUpperCase();

    }

    /**
     * 提示用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                context.startActivity(intent);
            } catch (Exception e) {
            }
        }

    }

    /**
     * @param @param  str
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: replaceBlank
     * @Description: TODO(替换掉字符串中任何格式的空格)
     */
    public static String replaceBlank(String str) {

        String dest = "";

        if (str != null) {

            Pattern p = Pattern.compile("\\s*|\t|\r|\n");

            Matcher m = p.matcher(str);

            dest = m.replaceAll("");

        }

        return dest;

    }

    // 过滤特殊字符
    public static String StringFilter(String str) {

        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        try {
            str = str.replaceAll("\r", "").replaceAll("\n", "");
            String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？-]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(str);
            return m.replaceAll("").trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getWeek() {
        String Week = "星期";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");// 也可将此值当参数传进来
        Date curDate = new Date(System.currentTimeMillis());
        String pTime = format.format(curDate);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        switch (c.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                Week += "天";
                break;
            case 2:
                Week += "一";
                break;
            case 3:
                Week += "二";
                break;
            case 4:
                Week += "三";
                break;
            case 5:
                Week += "四";
                break;
            case 6:
                Week += "五";
                break;
            case 7:
                Week += "六";
                break;
            default:
                break;
        }
        return Week;
    }

    // 转为大写
    public static String switchToUpper(String str) {
        return str.toUpperCase();
    }

    @SuppressLint("SimpleDateFormat")
    public static String TimeLong2Str(String time) {

        long date = Long.parseLong(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String str = sdf.format(date);
        return str;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            String date = sdate.replace("T", " ");
            date = date.replace("Z", "");
            SimpleDateFormat formatter = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            // Date date = formatter.parse(time);
            return dateFormater.get().parse(date);
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * 获取指定偏移后的时间
     *
     * @param sdate yyyy-MM-dd hh:mm:ss
     * @param in    偏移量（毫秒）
     * @return
     */
    public static String getTime(String sdate, int in) {
        Date data = toDate(sdate);
        data.setTime(data.getTime() + in);
        return DatetoString(data);
    }

    public static long getTime(String sdate) {
        Date data = toDate(sdate);
        return data.getTime();
    }

    public static String toDateDay(String sdate) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(toDate(sdate));
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            return year + "年" + (month + 1) + "月" + day + "日  "
                    + (hour < 10 ? "0" + hour : hour) + ":"
                    + (min < 10 ? "0" + min : min);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("InlinedApi")
    public static void toLogin(Context mContext) {
        MyApp app = (MyApp) mContext.getApplicationContext();
        app.clearUserProps();
        AppManager.getInstance().finishAllActivity();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        app.setOnCancellation(true);
        mContext.startActivity(intent);
    }

    @SuppressLint("SimpleDateFormat")
    public static String uploadTime(Long publishtime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        Date date = new Date(publishtime * 1000);
        return formatter.format(date);
    }

    public static void ViewCharmajorization(Button view, int charnum,
                                            String chars) {
        if (view != null && charnum > 0) {
            int maxcount = Math.max(chars.length(), charnum);
            String tempstr = chars.substring(0, maxcount - 1);
            tempstr += "……";
            view.setText(tempstr);
        }
    }

    public static String logStringCache = "";

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {

        }
        return apiKey;
    }

    // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
    public static boolean hasBind(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        String flag = sp.getString("bind_flag", "");
        if ("ok".equalsIgnoreCase(flag)) {
            return true;
        }
        return false;
    }

    public static void setBind(Context context, boolean flag) {
        String flagStr = "not";
        if (flag) {
            flagStr = "ok";
        }
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("bind_flag", flagStr);
        editor.commit();
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }

    public static String getLogText(Context context) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString("log_text", "");
    }

    public static void setLogText(Context context, String text) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sp.edit();
        editor.putString("log_text", text);
        editor.commit();
    }

    public static SpannableString getColorText(Context context, int start,
                                               int end, String text, int colorId) {

        SpannableString ss = new SpannableString(text);
        try {
            // SpannableString ss = new SpannableString(text);
            ss.setSpan(
                    new ForegroundColorSpan(context.getResources().getColor(
                            colorId)), start, end,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            return ss;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ss;
    }

    public static String getCacheJson(Context mContext, String cacheFileName) {

        File cacheFile = new File(mContext.getCacheDir(),
                URLEncoder.encode(cacheFileName));// 缓存文件
        StringBuilder datasb = new StringBuilder();
        if (cacheFile.exists())// 判断是否有缓存
        {
            try {
                BufferedReader br = new BufferedReader(
                        new FileReader(cacheFile));
                String data = br.readLine();// 一锟轿讹拷锟斤拷一锟叫ｏ拷直锟斤拷锟斤拷锟斤拷null为锟侥硷拷锟斤拷锟斤拷
                while (data != null) {
                    datasb.append(data);
                    data = br.readLine(); // 锟斤拷锟脚讹拷锟斤拷一锟斤拷
                }

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return datasb.toString();
    }

    // 4. 写缓存文件

    public static void weiteCache(Context mContext, String cacheFileName,
                                  List list) {

        String txt = JsonUtil.objectToJson(list);

        try {

            File cacheFile = new File(mContext.getCacheDir(),
                    URLEncoder.encode(cacheFileName));// 缓存文件
            FileOutputStream fos = new FileOutputStream(cacheFile);
            fos.write(txt.toString().getBytes());// 指定格式 存储到本地
            fos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
