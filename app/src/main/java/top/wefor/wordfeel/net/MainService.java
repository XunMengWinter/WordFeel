package top.wefor.wordfeel.net;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import top.wefor.wordfeel.model.result.WordResult;

/**
 * Created on 16/12/3.
 *
 * @author ice
 */

public interface MainService {

    @GET("/bdc/search/")
    Observable<WordResult> getWord(@Query("word") String word);

}
