package com.home.picturepick.lifecycle;

import com.blankj.utilcode.util.LogUtils;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/21 0:12
 * desc : 一个lifecycle的活动生命周期观察类,通过注解功能来感知活动的生命周期 纯属玩的和功能无关，可删
 * version : 1.0
 */
public class OurLifecycleObserver implements LifecycleObserver {
    //构造函数传进他就可以主动获知当前的生命周期状态了,
    // 如果没有构造函数传入这个，那么虽然这个类可以感知活动的生命周期变化，但是却没办法主动获知当前的生命周期状态
    //那么下面构造函数这样做之后就可以在任何地方调用liefecycle.currentState来主动获知当前的生命周期状态了（枚举类型的）
    public OurLifecycleObserver(Lifecycle lifecycle) {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void activityStart() {
        LogUtils.d("onStartObserver");
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void activityStop() {
        LogUtils.d("onStopObserver");
    }


}
