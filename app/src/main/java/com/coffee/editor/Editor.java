/*
 * Copyright (C) 2016 Muhammed Irshad
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.coffee.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.coffee.editor.component.CustomEditText;
import com.coffee.editor.model.EditorContent;
import com.coffee.editor.model.EditorTextStyle;
import com.coffee.editor.model.RenderType;

import java.util.Map;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class Editor extends EditorCore {
    public Editor(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setEditorListener(null);
        //  initialize(context,parentView,renderType,_PlaceHolderText);
    }

    public void setEditorListener(EditorListener _listener) {
        super.setEditorListener(_listener);
    }

    public EditorContent getContent() {
        return super.getContent();
    }

    public String getContentAsSerialized() {
        return super.getContentAsSerialized();
    }

    public String getContentAsSerialized(EditorContent state) {
        return super.getContentAsSerialized(state);
    }

    public EditorContent getContentDeserialized(String EditorContentSerialized) {
        return super.getContentDeserialized(EditorContentSerialized);
    }

    public String getContentAsHTML(EditorContent content) {
        return getHtmlExtensions().getContentAsHTML(content);
    }

    public String getContentAsHTML(String editorContentAsSerialized) {
        return getHtmlExtensions().getContentAsHTML(editorContentAsSerialized);
    }

    public void render(EditorContent content) {
        super.renderEditor(content);
    }

    public void render(String HtmlString) {
        renderEditorFromHtml(HtmlString);
    }

    public void render() {
        if (getRenderType() == RenderType.Editor) {
            getInputExtensions().insertEditText(0, this.placeHolder, null);
        }
    }

    private void restoreState() {
        EditorContent state = getStateFromString(null);
        render(state);
    }

    public void clearAllContents() {
        super.clearAllContents();
        if (getRenderType() == RenderType.Editor) {
            getInputExtensions().insertEditText(0, this.placeHolder, null);
        }
    }
//
//    @Override
//    public int removeParent(View view) {
//        int indexOfDeleteItem = super.removeParent(view);
//        if (indexOfDeleteItem < 0)
//            render();
//        return indexOfDeleteItem;
//    }


    /**
     * size in sp
     *
     * @param size
     */
    public void setNormalTextSize(int size) {
        getInputExtensions().setNormalTextSize(size);
    }

    /**
     * Set the fontface for the editor
     *
     * @deprecated use {@link #setContentTypeface(Map)} and {@link #setHeadingTypeface(Map)} (Map)} ()} instead.
     */
    @Deprecated
    public void setFontFace(int StringResource) {
        getInputExtensions().setFontFace(StringResource);
    }


    public void updateTextStyle(EditorTextStyle style) {
        getInputExtensions().UpdateTextStyle(style, null);
    }

    public void insertLink() {
        getInputExtensions().insertLink();
    }

    public void insertLink(String link) {
        getInputExtensions().insertLink(link);
    }


    /**
     * setup the fontfaces for editor content
     * For eg:
     * Map<Integer, String> typefaceMap = new HashMap<>();
     * typefaceMap.put(Typeface.NORMAL,"fonts/GreycliffCF-Medium.ttf");
     * typefaceMap.put(Typeface.BOLD,"fonts/GreycliffCF-Bold.ttf");
     * typefaceMap.put(Typeface.ITALIC,"fonts/GreycliffCF-Medium.ttf");
     * typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/GreycliffCF-Medium.ttf");
     *
     * @param map
     */

    public void setContentTypeface(Map<Integer, String> map) {
        getInputExtensions().setContentTypeface(map);
    }

    public Map<Integer, String> getContentTypeface() {
        return getInputExtensions().getContentTypeface();
    }

    /**
     * setup the fontfaces for editor heding tags (h1,h2,h3)
     * for Eg:
     * Map<Integer, String> typefaceMap = new HashMap<>();
     * typefaceMap.put(Typeface.NORMAL,"fonts/GreycliffCF-Medium.ttf");
     * typefaceMap.put(Typeface.BOLD,"fonts/GreycliffCF-Bold.ttf");
     * typefaceMap.put(Typeface.ITALIC,"fonts/GreycliffCF-Medium.ttf");
     * typefaceMap.put(Typeface.BOLD_ITALIC,"fonts/GreycliffCF-Medium.ttf");
     *
     * @param map
     */
    public void setHeadingTypeface(Map<Integer, String> map) {
        getInputExtensions().setHeadingTypeface(map);
    }

    public Map<Integer, String> getHeadingTypeface() {
        return getInputExtensions().getHeadingTypeface();
    }


    /**
     * Image Extension
     *
     * @param layout
     */
    public void setEditorImageLayout(int layout) {
        this.getImageExtensions().setEditorImageLayout(layout);
    }

    public void openImagePicker() {
        getImageExtensions().openImageGallery();
    }

    public void insertImage(Bitmap bitmap) {
        getImageExtensions().insertImage(bitmap, -1, null);
    }

    public void onImageUploadComplete(String url, String imageId) {
        getImageExtensions().onPostUpload(url, imageId);
    }

    public void onImageUploadFailed(String imageId) {
        getImageExtensions().onPostUpload(null, imageId);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        boolean onKey = super.onKey(v, keyCode, event, editText);
        if (getParentChildCount() == 0)
            render();
        return onKey;
    }
}
