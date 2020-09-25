package com.home.picturepick.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/25 17:34
 * desc : 有炫酷动画的一个按钮。
 * version : 1.0
 */
public class AniButtonView extends View {

    /**
     * view的宽度
     */
    private int width;
    /**
     * view的高度
     */
    private int height;
    /**
     * 圆角半径
     */
    private int circleAngle;
    /**
     * 根据view的大小设置成矩形
     */
    private RectF rectf = new RectF();
    /**
     * 默认两圆圆心之间的距离=需要移动的距离
     */
    private int default_two_circle_distance;
    /**
     * 两圆圆心之间的距离
     */
    private int two_circle_distance;

    public AniButtonView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public AniButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public AniButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        Canvas canvas = new Canvas();
        drawOvalToCircle(canvas);


    }

    private void drawOvalToCircle(Canvas canvas) {

        rectf.left = two_circle_distance;
        rectf.top = 0;
        rectf.right =width;
        //画圆角矩形
//        canvas.drawRoundRect(rectf,circleAngle,circleAngle,paint);


    }


}
