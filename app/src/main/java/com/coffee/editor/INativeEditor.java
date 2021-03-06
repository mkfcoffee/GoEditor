package com.coffee.editor;

import android.net.Uri;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Mcoffee on 2018/4/19.
 * Email: mkfcoffee@163.com
 */
public interface INativeEditor {

    int HEADING_1 = 1;
    int HEADING_2 = 2;
    int HEADING_3 = 3;

    @IntDef({HEADING_1, HEADING_2, HEADING_3})
    @Retention(RetentionPolicy.SOURCE)
    @interface HeadingLevel {
    }

    /**
     * 设置html内容
     *
     * @param htmlContent
     */
    void setHtmlContent(String htmlContent);

    /**
     * 插入图片
     *
     * @param imgUri
     */
    void insertImage(Uri imgUri);

    /**
     * 添加标题
     */
    void insertHeading(@HeadingLevel int level);

    /**
     * 添加链接
     *
     * @param text 文字
     * @param link 链接
     */
    void insertHyperlink(String text, String link);

    /**
     * 插入新段落
     */
    void insertParagraph();

    /**
     * 加粗/取消加粗 文字
     */
    void toggleBoldSelectText();

    /**
     * 清除内容
     */
    void clearContent();

    /**
     * 获取生成的html内容
     *
     * @return
     */
    String getHtmlContent();
}
