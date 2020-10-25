package com.home.picturepick.task;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.ToastUtils;
import com.home.picturepick.R;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;

import java.io.File;


/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/25 15:21
 * desc : 异步耗时任务。更新进度到状态栏 点击标题后调用，做来玩的
 * 第一个参数是传入doinbackgroud的参数，第二个是在publishProgress()中的参数，第三个参数是结果的类型
 * version : 1.0
 */
public class ProccessAsyncTack extends AsyncTask<String, Progress, File> {

    @SuppressLint("StaticFieldLeak")//持有上下文，有可能会造成内存泄漏
    private Context context;

    public ProccessAsyncTack(Context context) {
        this.context = context;
    }

    /**
     * 运行前的准备
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //textView.setText("开始准备了");//做UI的准备
    }

    /**
     * 后台运行的子线程
     *
     * @param strings
     * @return
     */
    @Override
    protected File doInBackground(String... strings) {
        OkGo.<File>get("http://192.168.1.147:8080/download/gxnx.apk")
                .tag(this)
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        Intent intent = new Intent(InstallAppBroadCast.ACTION_APPLICATION_INSTALL);
                        intent.putExtra(InstallAppBroadCast.INSTALL_KEY_PATH, response.body().getPath());
                        context.sendBroadcast(intent);
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        super.downloadProgress(progress);
                        publishProgress(progress);
                    }


                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                        Log.d("ProccessAsyncTack", String.valueOf(response.getException()));
                    }
                });
        return null;
    }


    /**
     * doInBackground执行完毕后把返回的file传递到这里，这里是UI线程
     * 因为doInBackground中也是用了网路请求框架。所以，当前的onSuccess方法也是完成了才会执行 的，但是它还没完成
     * 这个doInBackground方法就已经执行完了，还是空的file返回啊。没啥用。改用广播的方式吧
     *
     * @param file
     */
    @Override
    protected void onPostExecute(File file) {
        super.onPostExecute(file);
        Log.d("onPostExecute", "来了");
        //放弃在这里做完成后的操作了改成广播的形式了
    }

    /**
     * 在doInBackground中执行了publishProgress方法就可以在这里实时更新进度
     *
     * @param progresses
     */
    @SuppressLint("DefaultLocale")
    @Override
    protected void onProgressUpdate(Progress... progresses) {
        super.onProgressUpdate(progresses);
        Progress progress = progresses[0];
        float totalSize = progress.totalSize / 1024f;//总共大小
        float currentSize = progress.currentSize / 1024f;//当前大小
        float speed = progress.speed / 1024f;//网速
        float percent = currentSize / totalSize * 100f;//进度百分比
        String msg = String.format("已下载:%.0f%%",
                percent);


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
                .setContentTitle("正在下载广西农信，" + msg)
                .setContentText(msg)
                .setProgress(100, (int) percent, false)
                //加上这个属性会让通知显示完整，可以折叠
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(msg))
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(BitmapFactory.decodeResource(context.getResources(), R.mipmap.gril)))
                .setAutoCancel(false)//下载中的不能自动结束
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //这个id是时间毫秒，0则立刻发出
        manager.notify(0, notificationBuilder.build());

    }

}
