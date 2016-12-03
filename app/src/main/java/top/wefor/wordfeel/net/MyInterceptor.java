package top.wefor.wordfeel.net;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求拦截器
 */

public class MyInterceptor implements Interceptor {

    public MyInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        Request.Builder builder = request.newBuilder();
        //添加必要的信息
        head(builder, httpUrl);
        Response response = chain.proceed(builder.build());
        return response;
    }

    private void head(Request.Builder builder, HttpUrl httpUrl) {
        builder
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .url(httpUrl);
    }

}
