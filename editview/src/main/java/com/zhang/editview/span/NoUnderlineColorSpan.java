package com.zhang.editview.span;

import android.text.TextPaint;
import android.text.style.UnderlineSpan;

/**
 * Created by zhang on 2017/3/29.
 */

/**
 * 文字有颜色
 */
public class NoUnderlineColorSpan extends UnderlineSpan {

    protected int color;

    public NoUnderlineColorSpan() {
    }

    public NoUnderlineColorSpan(int color) {
        this.color = color;
    }
    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false);
    }
}
