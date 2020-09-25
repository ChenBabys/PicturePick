package com.home.picturepick.fragment;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.home.picturepick.R;
import com.home.picturepick.adapter.ImageViewAdapter;
import com.home.picturepick.adapter.MuchImagePagerAdapter;
import com.home.picturepick.selectImage.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/18 16:42
 * desc :数量大的图片预览器
 * version : 1.0
 */
public class MuchsPreViewDialogFragment extends DialogFragment {
    private TextView changeCount;
    private ViewPager viewPager;
    private List<Image> imagePathList;
    private Context context;
    private MuchImagePagerAdapter adapter;
    private int mPosition;//当前位置下标
    private String formatIndicatorStyle = "%d\u0020/\u0020%d";


    public void setImagePathList(List<Image> imagePathList) {
        this.imagePathList = imagePathList;
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
        return inflater.inflate(R.layout.preview_page_common, null, false);
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
        if (imagePathList != null && !imagePathList.isEmpty()) {
            adapter = new MuchImagePagerAdapter(imagePathList);
            viewPager.setAdapter(adapter);
            viewPager.setOffscreenPageLimit(imagePathList.size() - 1);
            viewPager.setCurrentItem(mPosition);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    changeCount.setText(String.format(formatIndicatorStyle, position + 1, imagePathList.size()));
                }

                boolean isScrolled = false;

                @Override
                public void onPageScrollStateChanged(int state) {
                    switch (state) {
                        case 1:// 手势滑动
                            isScrolled = false;
                            break;
                        case 2:// 界面切换
                            // 当前为最后一张，此时从右向左滑，则切换到第一张
                            if (viewPager.getCurrentItem() == adapter.getCount() - 1 && !isScrolled) {
                                viewPager.setCurrentItem(0);
                            }
                            // 当前为第一张，此时从左向右滑，则切换到最后一张
                            else if (viewPager.getCurrentItem() == 0 && !isScrolled) {
                                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1);
                            }
                            break;
                        case 0:
                            break;
                    }
                }
            });
            changeCount.setText(String.format(formatIndicatorStyle, mPosition + 1, imagePathList.size()));

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