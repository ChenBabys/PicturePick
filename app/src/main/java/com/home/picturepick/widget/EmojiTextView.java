package com.home.picturepick.widget;

import android.content.Context;
import android.text.InputFilter;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.emoji.widget.EmojiTextViewHelper;

/**
 * author : CYS
 * e-mail : 1584935420@qq.com
 * date : 2020/9/21 11:17
 * desc : 自定义的支持emoji的textview
 * version : 1.0
 */
public class EmojiTextView extends AppCompatTextView {
    private EmojiTextViewHelper emojiTextViewHelper;


    public EmojiTextView(@NonNull Context context) {
        super(context);
        init(context, null, 0);
    }

    public EmojiTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public EmojiTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        getEmojiTextViewHelper().updateTransformationMethod();
    }

    /**
     * 复写这两个方法，传入支持emoji
     *
     * @param filters
     */
    @Override
    public void setFilters(InputFilter[] filters) {
        super.setFilters(getEmojiTextViewHelper().getFilters(filters));
    }

    @Override
    public void setAllCaps(boolean allCaps) {
        super.setAllCaps(allCaps);
        getEmojiTextViewHelper().setAllCaps(allCaps);
    }

    private EmojiTextViewHelper getEmojiTextViewHelper() {
        if (emojiTextViewHelper == null) {
            emojiTextViewHelper = new EmojiTextViewHelper(this);
        }
        return emojiTextViewHelper;
    }


}
