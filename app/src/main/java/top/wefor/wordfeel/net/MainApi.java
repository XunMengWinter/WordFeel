package top.wefor.wordfeel.net;

import retrofit2.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.wefor.wordfeel.model.result.WordResult;

/**
 * Created on 2016/12/3.
 *
 * @author ice
 */

public class MainApi extends BaseApi implements MainService {

    private MainService mService = getRetrofit().create(MainService.class);

    @Override
    public Observable<WordResult> getWord(@Query("word") String word) {
        return mService.getWord(word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
