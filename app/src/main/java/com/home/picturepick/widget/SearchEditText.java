package com.home.picturepick.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.home.picturepick.R;

import java.util.Objects;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/17 11:04
 * desc : 搜索框输入框（你在使用这个控件时候右图标是默认隐藏的）
 * version : 1.0
 */
public class SearchEditText extends AppCompatEditText {
    private Drawable drawableLeft, drawableRight;


    public SearchEditText(Context context) {
        super(context);
        init(context, null, 0);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setPadding(SizeUtils.dp2px(8), 0, SizeUtils.dp2px(8), 0);
        //直接拿使用这个控件的地方设置这左右两个按钮
        drawableLeft = getCompoundDrawablesRelative()[0];
        drawableRight = getCompoundDrawablesRelative()[2];
        if (drawableLeft == null || drawableRight == null) {
            Toast.makeText(context, "你还没设置drawableLeft或者drawableRight呢", Toast.LENGTH_SHORT).show();
            //久方法换新
//            drawableLeft = getResources().getDrawable(R.drawable.ic_search);
//            drawableRight = getResources().getDrawable(R.drawable.ic_delete);
            drawableLeft = ContextCompat.getDrawable(context, R.drawable.ic_search);
            drawableRight = ContextCompat.getDrawable(context, R.drawable.ic_delete);
        }
        setCompoundDrawablesRelative(drawableLeft, null, drawableRight, null);
        //有比较好，没也没啥影响，但是还是建议写的
        drawableLeft.setBounds(0, 0, drawableLeft.getIntrinsicWidth(), drawableLeft.getIntrinsicHeight());
        drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
        // 默认隐藏右边的图标，所以你在使用这个控件时候右图标是默认隐藏的
        setDrawableVisible(false);
        //文本框监听
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {//如果文本框内文本空了，则清空列表
                    setDrawableVisible(false);//文本产生变化后，并且为0则不显示
                } else {
                    setDrawableVisible(true);//文本产生变化后，并且不为0则显示
                }
            }
        });

        //右边按钮点击事件
        setOnTouchListener((v, event) -> {
            //            //如果不是按下事件，不再处理
            if (event.getAction() != MotionEvent.ACTION_UP)
                return false;
            if (event.getX() > getWidth()
                    - getPaddingRight()
                    - drawableRight.getIntrinsicWidth()) {
                if (Objects.requireNonNull(getText()).length() != 0)
                    getText().clear();
            }
            return false;
        });


    }

    /**
     * 设置搜索框内的删除小按钮的显示与否
     *
     * @param visible
     */
    private void setDrawableVisible(boolean visible) {
        Drawable right = visible ? drawableRight : null;
        // 这里的getCompoundDrawables应该改为getCompoundDrawablesRelative，前者为空。不知是不是google官方api改了
        //setCompoundDrawables也应该改成setCompoundDrawablesRelative才行，要不然左边的按钮会在文本不为0时候消失
        setCompoundDrawablesRelative(drawableLeft,
                getCompoundDrawablesRelative()[1], right, getCompoundDrawablesRelative()[3]);
    }

}
