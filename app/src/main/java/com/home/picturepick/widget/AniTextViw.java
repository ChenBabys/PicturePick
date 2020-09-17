package com.home.picturepick.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.home.picturepick.R;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/15 16:07
 * desc : 有底部滑出入动画的textview
 * version : 1.0
 */
public class AniTextViw extends androidx.appcompat.widget.AppCompatTextView {
    private Animation bottom_in;
    private Animation bottom_out;

    public AniTextViw(Context context) {
        super(context);
        init(context, null, 0);
    }


    public AniTextViw(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AniTextViw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }


    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        bottom_in = AnimationUtils.loadAnimation(context, R.anim.button_bottom_in);
        bottom_out = AnimationUtils.loadAnimation(context, R.anim.button_bottom_out);
        bottom_in.setDuration(600);
        bottom_out.setDuration(600);

        startInAnimation();//默认添加进入动画
    }


    /**
     * 开始进入动画
     */
    public void startInAnimation() {
        startAnimation(bottom_in);
    }

    /**
     * 开始出去动画
     */
    public void startOutAnimation() {
        startAnimation(bottom_out);
    }


    public void setBottomIn(Animation bottom_in) {
        this.bottom_in = bottom_in;
    }

    public void setBottomOut(Animation bottom_out) {
        this.bottom_out = bottom_out;
    }
}
