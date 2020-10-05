package com.home.picturepick.task;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.home.picturepick.R;


/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/25 15:21
 * desc : 异步耗时任务。更新进度到状态栏 点击标题后调用，做来玩的
 * version : 1.0
 */
public class ProccessAsyncTack extends AsyncTask<Integer, Integer, String> {

    @SuppressLint("StaticFieldLeak")//持有上下文，有可能会造成内存泄漏
    private Context context;

    public ProccessAsyncTack(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //textView.setText("开始准备了");//做UI的准备
    }

    @Override
    protected String doInBackground(Integer... integers) {
        //模拟一个耗时操作
        int i = 0;
        for (i = 10; i <= 1000; i += 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }
        return i + "";
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int value = values[0];
        // progressBar.setProgress(value);//这里可以跟新UI

        // 新建一个通知到状态栏
        //通知的信道id;
        String mNotificationChannelId = "downId";
        //  NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //代替上面这句，你进去NotificationManagerCompat.from你会发现里面已经写好了上面这一句的内容，所以下面这种写法会好些
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        //大于或者等于sdk26（android8.0）(创建一个消息通道)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //为这个程序创建一个通道。可以打开系统设置查看本应用的程序信息中的通知，你可以找到这个通道的。
            // 第一个参数是通道id,第二个参数是通道名字，第三个参数是给广播定优先等级
            NotificationChannel channel = new NotificationChannel(mNotificationChannelId, "下载进度", NotificationManager.IMPORTANCE_DEFAULT);
            //描述
            channel.setDescription("下载专用");
            manager.createNotificationChannel(channel);
        }
        //创建一条通知，记住要用Builder.而不要直接实例化NotificationCompat，通知栏推送代码
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, mNotificationChannelId)
                .setSmallIcon(R.mipmap.gril)
                .setContentTitle("下载")
                .setContentText(String.valueOf(value))
                .setProgress(1000, value, false)
                //加上这个属性会让通知显示完整，可以折叠
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("正在下载中。。。。"))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gril)))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //这个id是时间毫秒，0则立刻发出
        manager.notify(0, notificationBuilder.build());

    }
}
