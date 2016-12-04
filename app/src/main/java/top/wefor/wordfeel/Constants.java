package top.wefor.wordfeel;

import android.os.Environment;

import java.io.File;

/**
 * Created on 2016/12/4.
 *
 * @author ice
 */

public class Constants {
    //图片文件夹的绝对路径
    public static final String IMAGE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "Andorid/data/" + BuildConfig.APPLICATION_ID + "/Images";

}
