package com.coffee.editor.util;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.coffee.editor.model.HtmlTag;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class HtmlParser {
    private Context context;
    LinearLayout parentView;

    public HtmlParser(Context _context) {
        this.context = _context;
        parentView = new LinearLayout(this.context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentView.setLayoutParams(params);
    }

    public static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }
}

