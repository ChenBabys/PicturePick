package com.home.picturepick.http;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author BHB
 * @description 请描述当前类作用
 * @date 2019/2/24 2:11
 */
public abstract class BaseCallback<T> extends AbsCallback<T> {

//    private BxLoadingView bxLoadingView;
    private boolean isShowLoading = false;
    private Type type;
    private Class<T> clazz;

    public BaseCallback() {
    }

    public BaseCallback(boolean isLoading) {
        this.isShowLoading = isLoading;
    }

    public BaseCallback(Type type) {
        this.type = type;
    }

    public BaseCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
        if (response.body() != null) {
            onSuccess(response.body());
        }
    }

    /**
     * 请求成功后回调数据类
     *
     * @param t 数据类
     */
    public abstract void onSuccess(T t);

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        // 判断是否需要加载动画
//        if (isShowLoading) {
//            // 在请求开始时显示加载框
//            bxLoadingView = LoadingUtil.show();
//        }
        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        // start.headers("header1", "HeaderValue1")
        // .params("params1", "ParamsValue1")
        // .params("token", "3215sdf13ad1f65asd4f3ads1f");
    }

    @Override
    public void onFinish() {
        super.onFinish();
        // 在请求结束时销毁加载框
//        if (bxLoadingView != null) {
//            bxLoadingView.dismiss();
//        }
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        Log.d("BaseCallback_onError", "msg:" + response.getException().getMessage());
        // 在请求出错时销毁加载框
//        if (bxLoadingView != null) {
//            bxLoadingView.dismiss();
//        }
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) {

        //详细自定义的原理和文档，看这里： https://github.com/jeasonlzy/okhttp-OkGo/wiki/JsonCallback
        /*
         * 一般直接 new BaseCallback 会直接用无参构造器，但是无参构造器不能带有Bean类类型，
         * 无参的Bean类类型在泛型T中已传入，所以在这里先判断一下，如果为空，就获取一下。
         */
        if (type == null) {
            if (clazz == null) {
                Type genType = getClass().getGenericSuperclass();
                type = ((ParameterizedType) genType).getActualTypeArguments()[0];
            }
        }

        ResponseBody body = response.body();
        if (body == null) {
            return null;
        }
        T data = null;
        Gson gson = new Gson();
        JsonReader jsonReader = new JsonReader(body.charStream());
        if (type != null) {
            data = gson.fromJson(jsonReader, type);
        }
        if (clazz != null) {
            data = gson.fromJson(jsonReader, clazz);
        }
        return data;
    }
}
