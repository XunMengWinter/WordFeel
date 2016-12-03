package top.wefor.wordfeel.net;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 16/8/4.
 *
 * @author ice
 */
public class BaseApi {

    private Retrofit getRetrofit(String baseUrl) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //token处理
        httpClient.addInterceptor(new MyInterceptor());

        OkHttpClient okHttpClient = httpClient.build();

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public Retrofit getRetrofit() {
        return getRetrofit(URLs.BASE_URL);
    }

}
