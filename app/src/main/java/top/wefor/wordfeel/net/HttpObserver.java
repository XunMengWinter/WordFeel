package top.wefor.wordfeel.net;

import android.accounts.NetworkErrorException;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Observer;
import top.wefor.wordfeel.App;
import top.wefor.wordfeel.model.BaseResult;


/**
 * 网络请求返回需要的模型
 * Created by ice on 3/3/16.
 */
public abstract class HttpObserver<T extends BaseResult> implements Observer<T>, INetResult<T> {

    /**
     * 请求失败, 对错误信息进行处理,
     * 默认显示一个Toast提醒用户.
     */
    @Override
    public void onFail(int errorCode, String msg) {
        App.showToast(msg);
    }


    @Override
    public void onCompleted() {
        onComplete();
    }

    @Override
    public void onError(Throwable e) {
        onComplete();
        e.printStackTrace();
        Logger.e(e.toString());
        int errorCd = -1000;
        String errorMsg = null;

        if (e instanceof SocketTimeoutException) {
            errorCd = -1001;
            errorMsg = "连接超时";
        } else if (e instanceof NetworkErrorException) {
            errorCd = -1002;
            errorMsg = "网络连接不可用";

        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            int errorCode = httpException.code();
            Logger.e(httpException.code() + " " + httpException.message());
            errorCd = httpException.code();
            if (httpException.response() != null && httpException.response().errorBody() != null) {
                try {
                    Logger.e(httpException.response().message());
                    String bodyStr = httpException.response().errorBody().string();
                    Logger.e(bodyStr);
                    errorMsg = bodyStr;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        onFail(errorCd, errorMsg);
    }

    @Override
    public void onNext(T model) {
        if (model == null) {
            onFail(1111, "数据为空");
            return;
        }
        if (BaseResult.SUCCESS_MSG.equals(model.msg))
            onSuccess(model);
        else
            onFail(model.statusCode, model.msg);
    }

}
