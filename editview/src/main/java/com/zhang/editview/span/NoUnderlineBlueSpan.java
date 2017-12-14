package com.zhang.editview.span;

import android.text.TextPaint;
import android.text.style.UnderlineSpan;

/**
 * Created by zhang on 2017/3/29.
 */

/**
 * 文字有颜色
 */
public class NoUnderlineBlueSpan extends UnderlineSpan {

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(ds.linkColor);
        ds.setUnderlineText(false);
    }
}
