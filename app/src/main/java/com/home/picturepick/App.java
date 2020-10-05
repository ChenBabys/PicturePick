package com.home.picturepick;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.loader.content.Loader;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tencent.mmkv.MMKV;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //替代sp的腾讯缓存库初始化
        MMKV.initialize(this);


        //可以不初始化的，但是最好初始化。设置一下配置
        Glide.init(getApplicationContext(),
                new GlideBuilder().setDefaultRequestOptions(new RequestOptions()
                        //.skipMemoryCache(true)// 不使用内存缓存
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                        .placeholder(R.color.white)));

        // testHandlerThread();

    }

//    private void testHandlerThread() {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                LogUtils.d("handler线程name:" + Thread.currentThread().getName());
//                LogUtils.d("handler线程ID:" + Thread.currentThread().getId());
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
////
////        Handler handler = new Handler();
//        //post类方法允许你排列一个Runnable对象到主线程队列中,当需要在不同于主UI线程中执行则需要配合HandlerThread进行使用
////        handler.post(runnable);
//
//        Thread thread = new Thread(runnable);
//        thread.start();
//        LogUtils.d("handler线程name:" + Thread.currentThread().getName());
//        LogUtils.d("handler线程ID:" + Thread.currentThread().getId());
//总结：handler 配合runnable并不会新建一个线程，而只有Thread配合runnable才是新线程
//
//    }




}
