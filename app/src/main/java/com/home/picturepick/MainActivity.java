package com.home.picturepick;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView photoList;
    private TextView title;
    private MainAdapter adapter;
    private List<Bitmap> photos;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = this.findViewById(R.id.tv_title);
        title.setText("选择相册图片");
        photoList = this.findViewById(R.id.photoList);
        photos = new ArrayList<>();
        if (adapter == null) {
            adapter = new MainAdapter(photos);
            photoList.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
            adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //如果点击的是加号，因为适配器中内容的position是0开始的
                    //所以photos的所有内容的position刚好是photos.size()-1，那么在此之外的就是加号了
                    if (position == photos.size()) {
                        choose();
                    } else {
                        ToastUtils.showShort(view.getTag() + "");
                    }
                }

                @Override
                public void onLongClick(View view, int position) {
                    //如果下标不是内容外的那个加号
                    if (position != photos.size()) {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("是否删除这一项？")
                                .setMessage("点击删除后将不在显示")
                                .setCancelable(true) //点击对话框以外的区域是否让对话框消失
                                .setPositiveButton("确定", (dialogInterface, i) -> {
                                    adapter.remove(position);
                                })
                                .setNegativeButton("取消", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                }).create();
                        dialog.show();
                    }
                }
            });
            photoList.setAdapter(adapter);
        } else {
            adapter.updateAll(photos);
        }
    }


    private void choose() {
        PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, 1001);
                    }

                    @Override
                    public void onDenied() {

                    }
                }).request();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 1001:
                    Uri uri = data.getData();//可以直接传递uri过去并且可以复制给imagview,但是加号放不上去，所以不用了
                    //使用content的接口
                    ContentResolver cr = this.getContentResolver();
                    //获取图片
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                        photos.add(bitmap);
                        adapter.updateAll(photos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }


    }
}