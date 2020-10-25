package com.home.picturepick;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.loader.content.Loader;
import okhttp3.OkHttpClient;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.home.picturepick.task.InstallAppBroadCast;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.tencent.mmkv.MMKV;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

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
        SSLNetInit();
        //注册一个安装更新的广播接收器
        registerReceiver(new InstallAppBroadCast(),
                new IntentFilter(InstallAppBroadCast.ACTION_APPLICATION_INSTALL));
    }

    /**
     * 初始化OkGo
     */
    private void SSLNetInit() {
        try {
            // trustAllCerts信任所有的证书 
            SSLContext sc = SSLContext.getInstance("TLS");
            // 初始化OkGo
            OkGo.getInstance()
                    .setOkHttpClient(new OkHttpClient.Builder()
                            // Session保持
                            .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                            //超时时间20秒
                            .connectTimeout(10L, TimeUnit.SECONDS)
                            .hostnameVerifier((hostname, session) -> true)
                            .sslSocketFactory(sc.getSocketFactory(), new X509TrustManager() {
                                @Override
                                public X509Certificate[] getAcceptedIssuers() {
                                    return new X509Certificate[0];
                                }

                                @Override
                                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                                }

                                @Override
                                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                                }
                            })
                            .build()
                    )
                    .setRetryCount(1)
                    .init(this);
        } catch (Exception ignored) {

            // 初始化OkGo
            OkGo.getInstance()
                    .setOkHttpClient(new OkHttpClient.Builder()
                            // Session保持
                            .cookieJar(new CookieJarImpl(new MemoryCookieStore()))
                            //超时时间20秒
                            .connectTimeout(10L, TimeUnit.SECONDS)
                            .hostnameVerifier((hostname, session) -> true)
                            .build()
                    )
                    .setRetryCount(1)
                    .init(this);
        }
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
