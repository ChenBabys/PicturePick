package com.home.picturepick.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.home.picturepick.R;
import com.home.picturepick.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览弹框，暂时没有打开后的双击图片放大和两手指撑开放大的功能，后续做吧
 */
public class PreViewDialogFragment extends DialogFragment {
    private TextView changeCount;
    private ViewPager viewPager;
    private List<ImageView> imageList;
    private List<Uri> imageUris;
    private Context context;
    private ViewPagerAdapter adapter;
    private int mPosition;//当前位置下标
    private String formatIndicatorStyle = "%d\u0020/\u0020%d";


    public void setImageUris(List<Uri> imageUris) {
        this.imageUris = imageUris;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_pre_view, null, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        changeCount = view.findViewById(R.id.tv_change_count);
        viewPager = view.findViewById(R.id.photo_pager);

    }

    private void initData() {
        if (imageUris != null && !imageUris.isEmpty()) {
            imageList = new ArrayList<>();
            for (int i = 0; i < imageUris.size(); i++) {
                ImageView View = new ImageView(context);
                imageList.add(View);
                View.setOnClickListener(view -> dismiss());
            }
            adapter = new ViewPagerAdapter(imageList, imageUris);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(imageList.size() - 1);
            viewPager.setCurrentItem(mPosition);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    changeCount.setText(String.format(formatIndicatorStyle, position + 1, imageUris.size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            changeCount.setText(String.format(formatIndicatorStyle, mPosition + 1, imageUris.size()));

        }
    }

    /**
     * 设置弹框的背景和宽高度，这些要是建立了很多歌dialogfragment应该要写到共用的父类去
     */
    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            //设置背景透明
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.background_dark));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            }

            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            //设置宽高
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            //设置输入模式
            layoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING;
            window.setAttributes(layoutParams);
            //            if (setDimAmount() != -1) {
//                win.setDimAmount(setDimAmount());
//            }
//            if (setAnimations() != 0) {
//                win.setWindowAnimations(setAnimations());
//            }
        }


    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        return super.show(transaction, tag);
    }
}