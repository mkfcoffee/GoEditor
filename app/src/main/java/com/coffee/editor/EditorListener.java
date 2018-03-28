package com.coffee.editor;

import android.graphics.Bitmap;
import android.text.Editable;
import android.widget.EditText;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public interface EditorListener{
    void onTextChanged(EditText editText, Editable text);
    void onUpload(Bitmap image, String uuid);
}