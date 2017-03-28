package com.bankicp.app.utils;

import java.io.File;

import com.bankicp.app.model.FileInfo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

public class FileUtils {
    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            } finally {
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    public static FileInfo getImageInfo(Context context, Uri uri) {
        FileInfo info = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {BaseColumns._ID, MediaColumns.DATA};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int _id;
                        String image_path;
                        int _idColumn = cursor.getColumnIndex(BaseColumns._ID);
                        int dataColumn = cursor
                                .getColumnIndex(MediaColumns.DATA);
                        _id = cursor.getInt(_idColumn);
                        image_path = cursor.getString(dataColumn);
                        info = new FileInfo();
                        info.setId(String.valueOf(_id));
                        info.setPath(image_path);

                        return info;
                    }
                }
            } catch (Exception e) {
                // Eat it
            } finally {
                cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            info = new FileInfo();
            info.setPath(uri.getPath());

            return info;
        }

        return null;
    }

    public static long getDirSize(File dir) {

        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    public static String formatFileSize(long fileS) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static String getNameLowerCase(String filePath) {
        if (filePath == null || filePath.equals(""))
            return "";
        String fName = FileUtils.getFileName(filePath);

        String startName = fName.substring(0, fName.lastIndexOf("."));

        return startName;
    }

    public static String getFileNameRemark(String filePath) {
        if (filePath == null || filePath.equals(""))
            return "";
        return filePath.substring(filePath.lastIndexOf("_") + 1, filePath.lastIndexOf("."));
    }

}
