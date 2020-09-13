package com.home.picturepick;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.home.picturepick.adapter.MainAdapter;
import com.home.picturepick.fragment.PreViewDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView photoList;
    private TextView title;
    private MainAdapter adapter;
    private List<Uri> photos;

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
                        //ToastUtils.showShort(view.getTag() + "");
                        PreViewDialogFragment fragment = new PreViewDialogFragment();
                        fragment.setImageUris(photos);
                        fragment.setPosition(position);
                        fragment.show(getSupportFragmentManager(), "PreViewDialogFragment");
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
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        //开启多选(上面要是用pick的话，vivo手机不行，小米可以)
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
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
        if (resultCode == RESULT_OK && null != data) {
            switch (requestCode) {
                case 1001:
                    //多选用getClipData，这时候getData是空的
                    ClipData imageClips = data.getClipData();
                    if (imageClips != null) {
                        for (int i = 0; i < imageClips.getItemCount(); i++) {
                            Uri uri = imageClips.getItemAt(i).getUri();
                            setImageToList(uri);
                        }
                    } else {
                        //单选getData才有数据
                        Uri uri = data.getData();
                        if (uri != null) {
                            setImageToList(uri);
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 提取出来的设置图片资源到适配器
     */
    private void setImageToList(Uri uri) {
        //Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        photos.add(uri);
        adapter.updateAll(photos);
    }

}