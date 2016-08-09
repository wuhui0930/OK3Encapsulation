package com.personal.ok3encapsulation.network;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by WuHui on 2016/8/1.
 */
public class OkHttpUtils {

    private OkHttpClient httpClient;
    private volatile static OkHttpUtils httpUtils;
    private final String TAG = OkHttpUtils.class.getSimpleName(); //获取类名
    private Handler httpHandler;

    //提交json数据
    private static final MediaType TYPE_Json = MediaType.parse("application/json;charset=utf-8");
    //提交字符串
    private final MediaType TYPE_String = MediaType.parse("text/x-markdown;charset=utf-8");

    private OkHttpUtils() {
        httpClient = new OkHttpClient();
        httpHandler = new Handler(Looper.getMainLooper());
    }

    //采用单列模式获取对象
    public static OkHttpUtils getInstance() {
        OkHttpUtils instance = null;
        if (httpUtils == null) {
            synchronized (OkHttpUtils.class) {
                if (instance == null) {
                    instance = new OkHttpUtils();
                    httpUtils = instance;
                }
            }
        }
        return instance;
    }

    /**
     * 同步请求
     *
     * @param url
     * @return
     */
    public String synJsonUrl(String url) {
        Request request = new Request.Builder().get().url(url).build();
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();//同步请求
            if (response.isSuccessful()) {
                return response.body().string();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 请求url 返回json字符串
     *
     * @param url
     * @param callBack
     */
    public void asynJsonUrl(String url, final OutPutJson callBack) {
        final Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSueccessJsonString(response.body().string(), callBack);
                }
            }
        });
    }


    /**
     * 请求url 返回json对象
     *
     * @param url
     * @param callBack
     */
    public void asynJsonObjUrl(String url, final OutPutJsonObj callBack) {
        final Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSueccessJsonObj(response.body().string(), callBack);
                }
            }
        });
    }


    /**
     * 请求url 返回byte字节数组
     *
     * @param url
     * @param callBack
     */
    public void asyJsonByteUrl(String url, final OutPutByte callBack) {
        final Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSueccessByte(response.body().bytes(), callBack);
                }
            }
        });
    }

    /**
     * 提交字符串 返回jsonObj
     *
     * @param url
     * @param content
     * @param callBack
     */
    public void onSubmitJsonString(String url, String content, final OutPutJsonObj callBack) {
        Request request = new Request.Builder().url(url).post(RequestBody.create(TYPE_String, content)).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null && response.isSuccessful()) {
                    onSueccessJsonObj(response.body().string(), callBack);
                }
            }
        });

    }

    /**
     * 提交表单  返回jsonObj
     *
     * @param url
     * @param params
     * @param callBack
     */
    public void onSubmitMapJsonObj(String url, Map<String, String> params, final OutPutJsonObj callBack) {
        FormBody.Builder form_builder = new FormBody.Builder();//表单对象，包含以input开始的对象，以html表单为主
        if (params != null && !params.isEmpty()) {
            for(Map.Entry<String,String> entry : params.entrySet()){
                form_builder.add(entry.getKey(),entry.getValue());
            }
        }
        RequestBody request_body = form_builder.build();
        Request request = new Request.Builder().url(url).post(request_body).build();//采用post方式提交
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response!=null&&response.isSuccessful()){
                    onSueccessJsonObj(response.body().string(), callBack);
                }
            }
        });
    }

    /**
     * 请求返回的结果是json字符串
     *
     * @param jsonValue
     * @param callBack
     */
    private void onSueccessJsonString(final String jsonValue, final OutPutJson callBack) {
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(jsonValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 请求返回的结果是json对象
     *
     * @param jsonObj
     * @param callBack
     */
    private void onSueccessJsonObj(final String jsonObj, final OutPutJsonObj callBack) {
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    try {
                        callBack.onResponse(new JSONObject(jsonObj));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 请求返回的结果是Byte字节数组
     *
     * @param bytes
     * @param callBack
     */
    private void onSueccessByte(final byte[] bytes, final OutPutByte callBack) {
        httpHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onResponse(bytes);
                }
            }
        });
    }


    public interface OutPutJson {
        void onResponse(String result);
    }

    public interface OutPutBitMap {
        void onResponse(Bitmap result);
    }

    public interface OutPutByte {
        void onResponse(byte[] result);
    }

    public interface OutPutJsonObj {
        void onResponse(JSONObject result);
    }
}
