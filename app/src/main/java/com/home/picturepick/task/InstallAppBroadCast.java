package com.home.picturepick.task;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.home.picturepick.R;

import java.io.File;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

/**
 * 接到广播就开始从intent中拿出path,再识别到apk的file文件去安装程序
 */
public class InstallAppBroadCast extends BroadcastReceiver {
    public static final String ACTION_APPLICATION_INSTALL = "app_install_broadcast_receiver_action_application_install";
    public static final String INSTALL_KEY_PATH = "install_key_path";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null
                && intent.getAction().equals(ACTION_APPLICATION_INSTALL)) {
            String path = intent.getStringExtra(INSTALL_KEY_PATH);
            if (path != null && !path.isEmpty()) {
                Log.d("path", path);
                File file = new File(path);
                if (file.isFile()) {
                    InstallApp(context, file);
                } else {
                    Log.d("file", "不是文件");
                }
            } else {
                Log.d("file", "path空");
            }
        }
    }

    private void InstallApp(Context context, File apkFile) {
        String fileType = "application/vnd.android.package-archive";
        //安装程序
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        //installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //读取文件的uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = "com.home.picturepick.FileProvider";
            Uri contentUri = FileProvider.getUriForFile(context, authority, apkFile);
            Log.d("contentUri",contentUri.toString());
            installIntent.setDataAndType(contentUri, fileType);
        } else {
            installIntent.setDataAndType(Uri.fromFile(apkFile), fileType);
            Log.d("Uri",Uri.fromFile(apkFile).toString());
        }

        PendingIntent updatePendingIntent = PendingIntent.getActivity(context, 0, installIntent, FLAG_UPDATE_CURRENT);
        // 新建一个通知到状态栏
        //通知的信道id;
        String mNotificationChannelId = "updateApp";
        //  NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //代替上面这句，你进去NotificationManagerCompat.from你会发现里面已经写好了上面这一句的内容，所以下面这种写法会好些
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        //大于或者等于sdk26（android8.0）(创建一个消息通道)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //为这个程序创建一个通道。可以打开系统设置查看本应用的程序信息中的通知，你可以找到这个通道的。
            // 第一个参数是通道id,第二个参数是通道名字，第三个参数是给广播定优先等级
            NotificationChannel channel = new NotificationChannel(mNotificationChannelId, "自动更新安装", NotificationManager.IMPORTANCE_DEFAULT);
            //描述
            channel.setDescription("安装专用");
            manager.createNotificationChannel(channel);
        }
        //创建一条通知，记住要用Builder.而不要直接实例化NotificationCompat，通知栏推送代码
        Notification notification = new NotificationCompat.Builder(context, mNotificationChannelId)
                .setSmallIcon(R.mipmap.gril)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_SOUND)
                .setTicker("App下载完成")
                .setAutoCancel(false)
                .setContentIntent(updatePendingIntent)
                .setContentText("App下载完成，点击安装")
                //加上这个属性会让通知显示完整，可以折叠
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("App更新下载完成"))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gril)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        //这个id是时间毫秒，0则立刻发出
        manager.notify(10001, notification);
        //启动安装程序
        context.startActivity(installIntent);

    }

}
