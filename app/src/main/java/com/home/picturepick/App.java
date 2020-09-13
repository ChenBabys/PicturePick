package com.home.picturepick;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.RequestOptions;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //可以不初始化
//        Glide.init(getApplicationContext(),
//                new GlideBuilder().setDefaultRequestOptions(new RequestOptions()
//                        .placeholder(R.color.white)));
    }
}
