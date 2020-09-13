package com.home.picturepick.adapter;

import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ViewPagerAdapter extends PagerAdapter {
    private List<ImageView> imageList;
    private List<Uri> imageUris;


    public ViewPagerAdapter(List<ImageView> imageList, List<Uri> imageUris) {
        this.imageList = imageList;
        this.imageUris = imageUris;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }


    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(imageList.get(position));
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Glide.with(imageList.get(position)).load(imageUris.get(position)).into(imageList.get(position));
        container.addView(imageList.get(position));
        return imageList.get(position);//返回一个imageview
    }
}
