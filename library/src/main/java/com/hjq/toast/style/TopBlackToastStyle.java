package com.hjq.toast.style;

import android.content.Context;
import android.view.Gravity;

/**
 * 顶部弹出
 */
public class TopBlackToastStyle extends BlackToastStyle{

    public TopBlackToastStyle(Context context) {
        super(context);
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER_HORIZONTAL | Gravity.TOP;
    }

    @Override
    public int getYOffset() {
        return super.getYOffset();
    }
}
