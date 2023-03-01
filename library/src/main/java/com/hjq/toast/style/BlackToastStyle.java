package com.hjq.toast.style;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hjq.toast.Toaster;
import com.hjq.toast.config.IToastStyle;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/Toaster
 *    time   : 2018/09/01
 *    desc   : 默认黑色样式实现, 系统底部弹出
 */
@SuppressWarnings({"unused", "deprecation"})
public class BlackToastStyle implements IToastStyle<View> {

    private final int mGravity;
    private final int mXOffset;
    private final int mYOffset;
    private final float mHorizontalMargin;
    private final float mVerticalMargin;

    public BlackToastStyle(Context context) {
        Toast defaultToast = new Toast(context);
        mGravity = defaultToast.getGravity();
        mXOffset = defaultToast.getXOffset();
        mYOffset = defaultToast.getYOffset();
        mHorizontalMargin = defaultToast.getHorizontalMargin();
        mVerticalMargin = defaultToast.getVerticalMargin();
    }

    @Override
    public int getGravity() {
        return mGravity;
    }

    @Override
    public int getXOffset() {
        return mXOffset;
    }

    @Override
    public int getYOffset() {
        return mYOffset;
    }

    @Override
    public float getHorizontalMargin() {
        return mHorizontalMargin;
    }

    @Override
    public float getVerticalMargin() {
        return mVerticalMargin;
    }

    @Override
    public View createView(Context context) {
        TextView textView = new TextView(context);
        textView.setId(android.R.id.message);
        textView.setGravity(getTextGravity(context));
        textView.setTextColor(getTextColor(context));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize(context));

        int horizontalPadding = getHorizontalPadding(context);
        int verticalPadding = getVerticalPadding(context);

        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);

        // 适配布局反方向特性
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setPaddingRelative(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        } else {
            textView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        }

        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Drawable backgroundDrawable = getBackgroundDrawable(context);
        // 设置背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            textView.setBackground(backgroundDrawable);
        } else {
            textView.setBackgroundDrawable(backgroundDrawable);
        }

        // 设置 Z 轴阴影
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textView.setZ(getTranslationZ(context));
        }

        return textView;
    }

    protected int getTextGravity(Context context) {
        return Gravity.LEFT;
    }

    protected int getTextColor(Context context) {
        return 0XFFFFFFFF;
    }

    protected float getTextSize(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, context.getResources().getDisplayMetrics());
    }

    protected int getHorizontalPadding(Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
    }

    protected int getVerticalPadding(Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, context.getResources().getDisplayMetrics());
    }

    protected Drawable getBackgroundDrawable(Context context) {
        GradientDrawable drawable = new GradientDrawable();
        // 设置颜色
        drawable.setColor(0Xa6000000);
        // 设置圆角
        drawable.setCornerRadius(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, context.getResources().getDisplayMetrics()));
        return drawable;
    }

    protected float getTranslationZ(Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics());
    }
}