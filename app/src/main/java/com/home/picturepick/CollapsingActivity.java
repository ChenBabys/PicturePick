package com.home.picturepick;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.telephony.emergency.EmergencyNumber;
import android.widget.TextView;

import com.blankj.utilcode.util.BusUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.home.picturepick.broadcast.TimeChangeReceiver;
import com.home.picturepick.constant.Constant;
import com.home.picturepick.fragment.BottomSheetMyDialogFragment;
import com.home.picturepick.lifecycle.OurLifecycleObserver;
import com.home.picturepick.viewModel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CollapsingActivity extends AppCompatActivity {

    private Toolbar toolbar;//使用了CollapsingToolbarLayout，那么标题就是他控制了，而不是你
    private CollapsingToolbarLayout collToobar;
    private FloatingActionButton floatingActionButton;
    private TextView textView;
    private TimeChangeReceiver receiver;
    private Handler handler;
    private Runnable runnable;
    private MainViewModel model;


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collpsing);
        toolbar = this.findViewById(R.id.titleBar);
        collToobar = this.findViewById(R.id.collTitle);
        textView = this.findViewById(R.id.mian_text);
        floatingActionButton = this.findViewById(R.id.floating);
        //注册bus
        BusUtils.register(this);
        //获取系统的隐式广播:时间变化
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        receiver = new TimeChangeReceiver();
        registerReceiver(receiver, intentFilter);


        //handle刷新UI
        handler = new Handler();
        runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void run() {
                //LogUtils.d("执行了hanldler");
                Date date = new Date();
                //String time = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd").format(new Date());
                String time = new SimpleDateFormat("HH:mm").format(date.getTime());
                String dates = new SimpleDateFormat("yyyy-MM-dd").format(date.getTime());
                collToobar.setTitle(time);
                textView.setText("现在是北京时间" + "\n" + dates + "\n" + time);//使用他设置标题才能实时更新，toobar不行
            }
        };

        handler.post(runnable);


        floatingActionButton.setOnClickListener(view -> {
            startActivity(new Intent(CollapsingActivity.this, AddImageActivity.class));
            //以后像那些个评论界面等等的地步弹出的就用它吧，好用的一批。
//            BottomSheetMyDialogFragment fragment = new BottomSheetMyDialogFragment();
//            fragment.showNow(getSupportFragmentManager(), "");
        });


        textView.setOnClickListener(v -> {
            BottomSheetMyDialogFragment fragment = new BottomSheetMyDialogFragment();
            fragment.showNow(getSupportFragmentManager(), "");
        });


        init();


    }

    private void init() {
        //第二个参数是一个接口，给构造函数传递值的，要是MainViewModel没有设置构造函数，则可以去掉
        model = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new MainViewModel(2);
            }
        }).get(MainViewModel.class);
        model.plusone();
//        model.clear();
        model.counter.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {

            }
        });
        //只要activity是继承自AppCompatActivity就不用再创建LifecycleOwner类了，可以直接使用lifecycle的实例的
        //但是这个lifecycle实例也是从LifecycleOwner类中创建的。
        //只要这一行代码，OurLifecycleObserver就能感知这个活动的生命周期了
        getLifecycle().addObserver(new OurLifecycleObserver(getLifecycle()));


    }


    //更新时间
    @BusUtils.Bus(tag = Constant.BUS_POST_NOTIFY_TIME)
    public void onBusNotifyTime() {
        //LogUtils.d("执行了");
        handler.post(runnable);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        BusUtils.unregister(this);
    }
}