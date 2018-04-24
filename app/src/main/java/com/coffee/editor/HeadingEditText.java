package com.coffee.editor;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.TypedValue;

/**
 * Created by Mcoffee on 2018/4/19.
 * Email: mkfcoffee@163.com
 */
public class HeadingEditText extends AppCompatEditText {

    private int heading1TextSize;
    private int heading2TextSize;
    private int heading3TextSize;

    private int headingLevel;

    public HeadingEditText(Context context) {
        super(context);
        initTextSize();
    }

    public HeadingEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTextSize();
    }

    public HeadingEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTextSize();
    }

    private void initTextSize() {
        Resources res = getResources();
        heading1TextSize = res.getDimensionPixelSize(R.dimen.font_heading_large);
        heading2TextSize = res.getDimensionPixelSize(R.dimen.font_heading_regular);
        heading3TextSize = res.getDimensionPixelSize(R.dimen.font_heading_small);
    }

    public void setLevel(@INativeEditor.HeadingLevel int level) {
        this.headingLevel = level;
        switch (level) {
            case INativeEditor.HEADING_1:
                setTextSize(TypedValue.COMPLEX_UNIT_PX, heading1TextSize);
                break;
            case INativeEditor.HEADING_2:
                setTextSize(TypedValue.COMPLEX_UNIT_PX, heading2TextSize);
                break;
            case INativeEditor.HEADING_3:
                setTextSize(TypedValue.COMPLEX_UNIT_PX, heading3TextSize);
                break;
            default:
                break;
        }
    }

    /**
     * 获取最终生成的html
     *
     * @return
     */
    public String getHtml() {
        return String.format("<h%d>%s</h%d>", headingLevel, getText().toString(), headingLevel);
    }
}
