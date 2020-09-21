package com.home.picturepick.broadcast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.blankj.utilcode.util.BusUtils;
import com.home.picturepick.R;
import com.home.picturepick.constant.Constant;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/20 13:08
 * desc : 一个接收系统发出的时间变化的广播的广播接收器
 * version : 1.0
 */
public class TimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //通知的信道id;
        String mNotificationChannelId = "Timeid";
        //  NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //代替上面这句，你进去NotificationManagerCompat.from你会发现里面已经写好了上面这一句的内容，所以下面这种写法会好些
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        //大于或者等于sdk26（android8.0）(创建一个消息通道)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //为这个程序创建一个通道。可以打开系统设置查看本应用的程序信息中的通知，你可以找到这个通道的。
            // 第一个参数是通道id,第二个参数是通道名字，第三个参数是给广播定优先等级
            NotificationChannel channel = new NotificationChannel(mNotificationChannelId, "系统时间捕捉", NotificationManager.IMPORTANCE_DEFAULT);
            //描述
            channel.setDescription("就在刚刚，你看到了时间的变化");
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        //创建一条通知，记住要用Builder.而不要直接实例化NotificationCompat，通知栏推送代码
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, mNotificationChannelId)
                .setSmallIcon(R.mipmap.gril)
                .setContentTitle("如你所见，这是一条时间变化了的通知")
                .setContentText("哈哈哈哈哈,这条通知还行吗")
                //加上这个属性会让通知显示完整，可以折叠
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("哈哈哈哈哈,这条通知还行吗哈哈哈哈哈,这条通知还行吗哈哈哈哈哈,这条通知还行吗"))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gril)))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (manager != null) {
            //这个id是时间毫秒，0则立刻发出
            manager.notify(2, notificationBuilder.build());
        }


        //顺便通知一下某界面更新时间
        BusUtils.post(Constant.BUS_POST_NOTIFY_TIME);

    }
}
