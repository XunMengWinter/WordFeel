package top.wefor.wordfeel.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import top.wefor.wordfeel.App;

/**
 * Created on 2016/12/4.
 * <p>
 * 文件相关的帮助类
 *
 * @author ice
 * @GitHub https://github.com/XunMengWinter
 */

public class FileUtil {

    /**
     * 创建文件夹
     * 递归实现
     */
    public static void makeDirs(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists())
            if (!dir.mkdir()) {
                String[] dirArray = dirPath.split(File.separator);
                int lastDirLength = dirArray[dirArray.length - 1].length();
                makeDirs(dirPath.substring(0, dirPath.length() - lastDirLength - 1));
                dir.mkdir();
            }
    }

    /**
     * 获取某个目录下的所有文件
     *
     * @param dirPath 文件夹绝对路径
     * @return 返回文件列表
     */
    public static List<File> getFileList(String dirPath) {
        List<File> allDir = new ArrayList<>();
        try {
            SecurityManager checker = new SecurityManager();
            File dirFile = new File(dirPath);
            checker.checkRead(dirPath);
            File[] files = dirFile.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    allDir.add(file);
                } else {
                    getFileList(file.getAbsolutePath());
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return allDir;
    }

    /**
     * 保存图片，绝对路径
     */
    public static File saveBitmap(Bitmap bitmap, String filename, String dirPath) {
        OutputStream outStream = null;

        File file = new File(dirPath, filename);
        if (file.exists()) {
            file.delete();
            file = new File(dirPath, filename);
            Logger.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            outStream = new FileOutputStream(file);
            if (filename.contains("png"))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            else
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            // 更新图库
            Uri uri = Uri.fromFile(file);
            Intent scannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            App.get().sendBroadcast(scannerIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Logger.i("new file: " + file);
        return file;
    }

    /*打开文件夹，绝对路径*/
    public static void openFolder(Context context, String dirPath) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(dirPath);
        intent.setDataAndType(uri, "text/csv");
        context.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    /*删除文件夹，绝对路径*/
    public static void deleteFolder(String dirPath) {
        File dirFile = new File(dirPath);
        if (!dirFile.exists()) return;

        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                file.delete();
            } else {
                getFileList(file.getAbsolutePath());
            }
        }
        dirFile.delete();
    }

}
