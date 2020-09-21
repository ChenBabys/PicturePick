package com.home.picturepick.fragment;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.provider.FontRequest;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.emoji.text.FontRequestEmojiCompatConfig;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.home.picturepick.R;
import java.lang.ref.WeakReference;

/**
 * 从下部弹出的可以跟随手指滑动的弹框，可以滑动关闭
 */
public class BottomSheetMyDialogFragment extends BottomSheetDialogFragment {

    /**
     * Change this to {@code false} when you want to use the downloadable Emoji font.
     */
    private static final boolean USE_BUNDLED_EMOJI = true;
    // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F4BB] (PERSONAL COMPUTER)
    private static final String WOMAN_TECHNOLOGIST = "\uD83D\uDC69\u200D\uD83D\uDCBB";

    // [U+1F469] (WOMAN) + [U+200D] (ZERO WIDTH JOINER) + [U+1F3A4] (MICROPHONE)
    private static final String WOMAN_SINGER = "\uD83D\uDC69\u200D\uD83C\uDFA4";

    static final String EMOJI = WOMAN_TECHNOLOGIST + " " + WOMAN_SINGER;


    private int topOffset = 300;
    private BottomSheetBehavior<FrameLayout> behavior;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadEmojiFontFromNetWork();
        return inflater.inflate(R.layout.fragment_bottom_sheet_my_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(@NonNull View view) {

        // TextView variant provided by EmojiCompat library
        //emoji库自带的
        final TextView emojiTextView = view.findViewById(R.id.emojitextview);
        emojiTextView.setText("emojiTextView" + EMOJI);

        // EditText variant provided by EmojiCompat library
        //emoji库自带的
        final TextView emojiEditText = view.findViewById(R.id.emojiedittext);
        emojiEditText.setText("emojiEditText" + EMOJI);

        // Button variant provided by EmojiCompat library
        //emoji库自带的
        final TextView emojiButton = view.findViewById(R.id.emojibuttom);
        emojiButton.setText("emojiButton" + EMOJI);

        // Regular TextView without EmojiCompat support; you have to manually process the text
        //这是一个很普通的textView，再集成
        final TextView regularTextView = view.findViewById(R.id.regular_text_view);
        EmojiCompat.get().registerInitCallback(new InitCallback(regularTextView));

        // Custom TextView
        //这是一个我自定义的textview
        final TextView customTextView = view.findViewById(R.id.Comemojitextview);
        customTextView.setText("customTextView" + EMOJI);


    }

    /**
     * 初始化和下载
     */
    private void loadEmojiFontFromNetWork() {
        final EmojiCompat.Config config;
        if (USE_BUNDLED_EMOJI) {
            //这里给的是默认用这个的，而没有执行else中的内容
            config = new BundledEmojiCompatConfig(getContext());
        } else {
            //国内可能没用
            FontRequest fontRequest = new FontRequest(
                    "com.google.android.gms.fonts",
                    "com.google.android.gms",
                    "Noto Color Emoji Compat",
                    R.array.com_google_android_gms_fonts_certs);
            config = new FontRequestEmojiCompatConfig(getContext(), fontRequest);
        }
        config.setReplaceAll(true)
                .registerInitCallback(new EmojiCompat.InitCallback() {
                    @Override
                    public void onInitialized() {
                        super.onInitialized();

                    }

                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        super.onFailed(throwable);
                    }
                });

        EmojiCompat.init(config);
    }


    private static class InitCallback extends EmojiCompat.InitCallback {
        private final WeakReference<TextView> mRegularTextViewRef;

        public InitCallback(TextView mRegularTextView) {
            this.mRegularTextViewRef = new WeakReference<>(mRegularTextView);
        }

        @Override
        public void onInitialized() {
            super.onInitialized();
            final TextView regularTextView = mRegularTextViewRef.get();

            if (regularTextView != null) {
                final EmojiCompat compat = EmojiCompat.get();
                final Context context = regularTextView.getContext();
                regularTextView.setText(compat.process("regularTextView" + EMOJI));
            }


        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // 设置软键盘不自动弹出
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        FrameLayout bottomSheet = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
            layoutParams.height = getHeight();
            behavior = BottomSheetBehavior.from(bottomSheet);
            //背景设为透明
            bottomSheet.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            // 初始为2/3展开状态
            behavior.setState(BottomSheetBehavior.STATE_DRAGGING);
        }
    }


    private int getHeight() {
        int height = 1920;
        if (getContext() != null) {
            WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (windowManager != null) {
                windowManager.getDefaultDisplay().getSize(point);
                height = point.y - getTopOffset();
            }
        }
        return height;
    }

    public int getTopOffset() {
        return topOffset;
    }

    public void setTopOffset(int topOffset) {
        this.topOffset = topOffset;
    }


    public BottomSheetBehavior<FrameLayout> getBehavior() {
        return behavior;
    }

}