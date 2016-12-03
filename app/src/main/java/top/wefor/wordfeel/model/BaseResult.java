package top.wefor.wordfeel.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created on 2016/12/3.
 *
 * @author ice
 */

public class BaseResult implements Serializable {
    public static final String SUCCESS_MSG = "SUCCESS";

    @SerializedName("status_code")
    public int statusCode;

    @SerializedName("msg")
    public String msg;

//    public Model data;  //其它返回参数均在Data中，由于retrofit返回结果不支持范型，所以采用继承方案。

}
