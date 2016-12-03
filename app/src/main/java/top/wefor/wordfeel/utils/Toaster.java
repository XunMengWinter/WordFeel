package top.wefor.wordfeel.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author kaka from github
 * @date 2014-12-22
 */
public class Toaster {

    private Toast mToast;
    private Context context;

    public Toaster(Context context) {
        this.context = context;
    }


    public Toast getToast(int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_SHORT);
    }

    public Toast getToast(CharSequence text) {
        return Toast.makeText(context, text, Toast.LENGTH_SHORT);
    }

    public Toast getLongToast(int resId) {
        return Toast.makeText(context, resId, Toast.LENGTH_LONG);
    }

    public Toast getLongToast(CharSequence text) {
        return Toast.makeText(context, text, Toast.LENGTH_LONG);
    }

    public void showToast(int resId) {
        if (mToast != null)
            mToast.cancel();
        mToast = getToast(resId);
        show();
    }

    public void showToast(CharSequence text) {
        if (mToast != null)
            mToast.cancel();
        mToast = getToast(text);
        show();
    }

    public void showLongToast(int resId) {
        if (mToast != null)
            mToast.cancel();
        mToast = getLongToast(resId);
        show();
    }

    public void showLongToast(CharSequence text) {
        if (mToast != null)
            mToast.cancel();
        mToast = getLongToast(text);
        show();
    }

    private void show() {
        mToast.show();
    }

}
