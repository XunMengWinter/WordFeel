package top.wefor.wordfeel.model.result;

import com.google.gson.annotations.SerializedName;

import top.wefor.wordfeel.model.BaseResult;
import top.wefor.wordfeel.model.entity.WordEntity;

/**
 * Created on 2016/12/3.
 *
 * @author ice
 */

public class WordResult extends BaseResult {

    @SerializedName("data")
    public WordEntity wordEntity;

}
