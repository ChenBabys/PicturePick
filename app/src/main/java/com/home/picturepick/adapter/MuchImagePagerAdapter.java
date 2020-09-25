package com.home.picturepick.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.home.picturepick.selectImage.Image;

import java.util.List;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/18 16:42
 * desc : 大量图片预览的ViewPager适配器
 * version : 1.0
 */
public class MuchImagePagerAdapter extends PagerAdapter {
    private List<Image> mDrawableResIdList;


    public MuchImagePagerAdapter(List<Image> mDrawableResIdList) {
        this.mDrawableResIdList = mDrawableResIdList;
    }

    @Override
    public int getCount() {
        return mDrawableResIdList == null ? 0 : mDrawableResIdList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public int getItemPosition(@NonNull Object object) {
        if (mDrawableResIdList != null) {
            String resId = (String) ((ImageView) object).getTag();
            if (resId != null) {
                for (int i = 0; i < mDrawableResIdList.size(); i++) {
                    if (resId.equals(mDrawableResIdList.get(i))) {
                        return i;
                    }
                }
            }
        }
        return ViewPager.NO_ID;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (mDrawableResIdList != null && position < mDrawableResIdList.size()) {
            //分页加载，更整除4的时候就加载图片
            Image image = mDrawableResIdList.get(position);
            if (image != null) {
                ImageView imageView = new ImageView(container.getContext());
                Glide.with(imageView).load(image.getPath()).into(imageView);
                //此处假设所有的照片都不同，用resId唯一标识一个itemView；也可用其它Object来标识，只要保证唯一即可
                imageView.setTag(image.getPath());
                container.addView(imageView);
                return imageView;
            }
        }
        return null;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        int count = container.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = container.getChildAt(i);
            //正在销毁第几页："+position+",正在销毁对应mViews的第几个数据源
            if (childView == object) {
                container.removeView(childView);
                break;
            }
        }
    }

    public void updateData(List<Image> mDrawableResIdList) {
        if (mDrawableResIdList == null) return;
        this.mDrawableResIdList = mDrawableResIdList;
        this.notifyDataSetChanged();
    }

}
