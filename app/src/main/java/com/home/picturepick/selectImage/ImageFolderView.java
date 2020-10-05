package com.home.picturepick.selectImage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.home.picturepick.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/18 16:42
 * desc :
 * version : 1.0
 */
public class ImageFolderView extends FrameLayout implements ImageFolderAdapter.OnItemClickListener {
    private View mShadowView;
    private String mShadowViewColor = "#50000000";
    private RecyclerView mImageFolderRv;
    private List<ImageFolder> mImageFolders;
    private ImageFolderViewListener mListener;
    private int mImageFolderHeight;
    private boolean mShow;

    public ImageFolderView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public ImageFolderView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ImageFolderView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor(mShadowViewColor));//打开文件夹时候背后列表的透明颜色
        mImageFolderRv = new RecyclerView(context);
        //mImageFolderRv.setBackgroundColor(Color.parseColor("#ffffff"));//白色
        mImageFolderRv.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_sheet_shape_style));
        FrameLayout.LayoutParams rvParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rvParams.gravity = Gravity.BOTTOM;
        mImageFolderRv.setLayoutParams(rvParams);
        mImageFolderRv.setPadding(0, SizeUtils.dp2px(20), 0, 0);
        //设置布局管理器setLayoutManager
        mImageFolderRv.setLayoutManager(new LinearLayoutManager(context));
        addView(mShadowView);
        addView(mImageFolderRv);
        //开始不显示阴影
        mShadowView.setAlpha(0);
        mShadowView.setVisibility(GONE);
    }

    /**
     * 设置图片文件夹
     *
     * @param mImageFolders
     */
    public void setImageFolders(List<ImageFolder> mImageFolders) {
        this.mImageFolders = mImageFolders;
    }

    public void setAdapter(ImageFolderAdapter adapter) {
        if (adapter == null) {
            throw new NullPointerException("adapter not null！");
        }
        mImageFolderRv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    public void setListener(ImageFolderViewListener listener) {
        this.mListener = listener;
    }

    /**
     * 显示
     */
    public void show() {
        if (mShow) {
            return;
        }
        if (mListener != null) {
            mListener.onShow();
        }
        mShow = true;
        mShadowView.setVisibility(VISIBLE);
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(mImageFolderRv, "translationY", mImageFolderHeight, 0);
        translationYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0f, 1f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationYAnimator, alphaAnimator);
        animatorSet.setDuration(388);
        animatorSet.start();
    }

    /**
     * 隐藏
     */
    public void hide() {
        if (!mShow) {
            return;
        }
        if (mListener != null) {
            mListener.onDismiss();
        }
        ObjectAnimator translationYAnimator = ObjectAnimator.ofFloat(mImageFolderRv,
                "translationY", 0, mImageFolderHeight);
        translationYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1f, 0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translationYAnimator, alphaAnimator);
        animatorSet.setDuration(388);
        animatorSet.start();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShow = false;
                mShadowView.setVisibility(GONE);
            }
        });
    }


    @Override
    //测量
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        mImageFolderHeight = (int) (height * 0.9f);//设置他的高度只有屏幕的90%高
        ViewGroup.LayoutParams layoutParams = mImageFolderRv.getLayoutParams();
        layoutParams.height = mImageFolderHeight;//把rv列表的高度也设置为这个高度
        mImageFolderRv.setLayoutParams(layoutParams);
        measureChild(mImageFolderRv, widthMeasureSpec, heightMeasureSpec);//测量子项mImageFolderRv
        //开始的时候，也就是mShow是初始的false的时候，移动下去隐藏住，这个判断必须要，否则会出现bug（在展开时候滑动回突然消失。）
        if (!mShow)
            mImageFolderRv.setTranslationY(mImageFolderHeight);
    }

    public boolean isShowing() {
        return mShow;
    }


    @Override
    public void onItemClick(View view, ImageFolder imageFolder, int position) {
        if (mListener != null) {
            mListener.onSelectFolder(this, mImageFolders.get(position));
            hide();
        }
    }

    @Override
    public void onLongClick(View view, ImageFolder imageFolder, int position) {

    }

    public interface ImageFolderViewListener {
        void onSelectFolder(ImageFolderView imageFolderView, ImageFolder imageFolder);

        void onDismiss();

        void onShow();
    }

}
