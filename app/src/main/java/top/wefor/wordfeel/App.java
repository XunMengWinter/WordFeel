package top.wefor.wordfeel;

import android.app.Application;

import top.wefor.wordfeel.utils.Toaster;

/**
 * Created on 2016/12/3.
 *
 * @author ice
 */

public class App extends Application {

    private static App sApp;
    private static Toaster sToaster;

    public static App get() {
        App inst = sApp;  // 在这里创建临时变量
        if (inst == null) {
            synchronized (App.class) {
                inst = sApp;
                if (inst == null) {
                    inst = new App();
                    sApp = inst;
                }
            }
        }
        return inst;  // 注意这里返回的是临时变量
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;

    }

    public static void showToast(CharSequence msg) {
        if (sToaster == null)
            sToaster = new Toaster(App.get());
        if (msg != null)
            sToaster.showToast(msg);
    }

}
