package com.coffee.editor;

import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by Mcoffee on 2018/4/19.
 * Email: mkfcoffee@163.com
 */
public interface IImageLoader {

    /**
     * 加载图片接口
     *
     * @param imageView
     * @param uri
     */
    void loadIntoImageView(ImageView imageView, Uri uri);
}
