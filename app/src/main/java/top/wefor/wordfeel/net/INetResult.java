package top.wefor.wordfeel.net;

import android.support.annotation.NonNull;

/**
 * Created on 16/6/28 16:40.
 * 自定义网络请求返回接口，将onComplete设置为必定事件，将onSuccess和onFail设置为互补事件
 *
 * @author ice, GitHub: https://github.com/XunMengWinter
 */
public interface INetResult<T> {

    /**
     * 该方法在请求完成是第一个执行,
     * 可用于执行一些必须操作, 比如停止进度条.
     */
    abstract void onComplete();

    /**
     * 请求成功, 返回一个实体
     */
    abstract void onSuccess(@NonNull T model);

    /**
     * 请求失败, 对错误信息进行处理,
     * 默认显示一个Toast提醒用户.
     */

    void onFail(int errorCode, String msg);

}
