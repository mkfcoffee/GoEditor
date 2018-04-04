package com.coffee.editor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coffee.editor.component.CustomEditText;
import com.coffee.editor.component.HTMLExtensions;
import com.coffee.editor.component.ImageExtensions;
import com.coffee.editor.component.InputExtensions;
import com.coffee.editor.model.EditorContent;
import com.coffee.editor.model.EditorControl;
import com.coffee.editor.model.EditorTextStyle;
import com.coffee.editor.model.EditorType;
import com.coffee.editor.model.Node;
import com.coffee.editor.model.Op;
import com.coffee.editor.model.RenderType;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class EditorCore extends LinearLayout {
    public String placeHolder = null;
    private final String SHAREDPREFERENCE = "QA";
    private Context mContext;
    private Activity mActivity;
    protected LinearLayout mParentView;
    private RenderType renderType;
    private Resources resources;
    private View activeView;
    private Gson gson;
    private EditorListener listener;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions inputExtensions;
    private ImageExtensions imageExtensions;
    private HTMLExtensions htmlextensions;

    public EditorCore(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.setOrientation(VERTICAL);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        this.resources = context.getResources();
        gson = new Gson();
        inputExtensions = new InputExtensions(this);
        imageExtensions = new ImageExtensions(this);
        htmlextensions = new HTMLExtensions(this);
        this.mParentView = this;
    }

    public Activity getActivity() {
        return this.mActivity;
    }

    public LinearLayout getParentView() {
        return this.mParentView;
    }

    public int getParentChildCount() {
        return this.mParentView.getChildCount();
    }

    public RenderType getRenderType() {
        return this.renderType;
    }

    public Resources getResources() {
        return this.resources;
    }

    public View getActiveView() {
        return this.activeView;
    }

    public void setActiveView(View view) {
        this.activeView = view;
    }

    public EditorListener getEditorListener() {
        return this.listener;
    }

    public void setEditorListener(EditorListener listener) {
        this.listener = listener;
    }

    public InputExtensions getInputExtensions() {
        return this.inputExtensions;
    }

    public ImageExtensions getImageExtensions() {
        return this.imageExtensions;
    }

    public HTMLExtensions getHtmlExtensions() {
        return this.htmlextensions;
    }

    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            return;
        }
        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.GoEditor);
            this.placeHolder = a.getString(R.styleable.GoEditor_placeholder);
            String renderType = a.getString(R.styleable.GoEditor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.renderType = RenderType.Editor;
            } else {
                this.renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
            }

        } finally {
            if (a != null) {
                a.recycle();
            }
        }
    }

    /**
     * determine target index for the next insert,
     *
     * @param type
     * @return
     */
    public int determineIndex(EditorType type) {
        int size = this.mParentView.getChildCount();
        if (this.renderType == RenderType.Renderer)
            return size;
        View view = this.activeView;
        if (view == null)
            return size;
        int currentIndex = this.mParentView.indexOfChild(view);
        EditorType tag = getControlType(view);
        if (tag == EditorType.input) {
//            int length = ((EditText) this.activeView).getText().length();
            return currentIndex;
        } else {
            return size;
        }
    }

    public boolean containsStyle(List<EditorTextStyle> styles, EditorTextStyle style) {
        return styles.contains(style);
    }

    public EditorControl updateTagStyle(EditorControl controlTag, EditorTextStyle style, Op op) {
        List<EditorTextStyle> styles = controlTag.styleList;
        int index = styles.indexOf(style);
        if (op == Op.Delete) {
            if (index != -1) {
                styles.remove(index);
                controlTag.styleList = styles;
            }
        } else {
            if (index == -1) {
                styles.add(style);
            }
        }
        return controlTag;
    }

    public EditorType getControlType(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        return control.type;
    }

    public EditorControl getControlTag(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        return control;
    }

    public EditorControl createTag(EditorType type) {
        EditorControl control = new EditorControl();
        control.type = type;
        control.styleList = new ArrayList<>();
        return control;
    }

    public void deleteFocusedPrevious(EditText view) {
        int index = mParentView.indexOfChild(view);
        if (index == 0)
            return;
        EditorControl contentType = (EditorControl) ((View) view.getParent()).getTag();

        View toFocus = mParentView.getChildAt(index - 1);
        EditorControl control = (EditorControl) toFocus.getTag();
        // If its an image or map, do not delete edittext, as there is nothing to focus on after image
        if (control.type == EditorType.img || control.type == EditorType.map) {
            return;
        }
        //If the person was on edittext,  had removed the whole text, we need to move into the previous line
        removeParent(view);
    }


    public int removeParent(View view) {
        int indexOfDeleteItem = mParentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        this.mParentView.removeView(view);
        Log.d("indexOfDeleteItem", "indexOfDeleteItem : " + indexOfDeleteItem);

        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(mParentView.getChildAt(i)) == EditorType.input) {
                nextItem = mParentView.getChildAt(i);
                continue;
            }
        }
        if (nextItem != null) {
            CustomEditText text = (CustomEditText) nextItem;
            if (text.requestFocus()) {
                text.setSelection(text.getText().length());
            }
            this.activeView = nextItem;
        }
        return indexOfDeleteItem;
    }


    public EditorContent getStateFromString(String content) {
        if (content == null) {
            content = getValue("editorState", "");
        }
        EditorContent deserialized = gson.fromJson(content, EditorContent.class);
        return deserialized;
    }

    public String getValue(String key, String defaultValue) {
        SharedPreferences sp = mContext.getSharedPreferences(SHAREDPREFERENCE, 0);
        return sp.getString(key, defaultValue);

    }

    public void putValue(String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(SHAREDPREFERENCE, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * 将内容作为EditorState的序列化形式返回
     *
     * @return
     */
    public String getContentAsSerialized() {
        EditorContent state = getContent();
        return serializeContent(state);
    }

    /**
     * 以序列化的形式返回提供的参数
     *
     * @param state
     * @return
     */
    public String getContentAsSerialized(EditorContent state) {
        return serializeContent(state);
    }

    public EditorContent getContentDeserialized(String EditorContentSerialized) {
        EditorContent Deserialized = gson.fromJson(EditorContentSerialized, EditorContent.class);
        return Deserialized;
    }

    public String serializeContent(EditorContent _state) {
        String serialized = gson.toJson(_state);
        return serialized;
    }

    /**
     * 返回编辑器中的内容 EditorContent
     *
     * @return
     */
    public EditorContent getContent() {

        if (this.renderType == RenderType.Renderer) {
            Toast.makeText(getContext(), "这个选项只能在编辑模式下使用", Toast.LENGTH_SHORT).show();
            return null;
        }

        int childCount = this.mParentView.getChildCount();
        EditorContent editorContent = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            Node node = new Node();
            View view = mParentView.getChildAt(i);
            EditorType type = getControlType(view);
            node.type = type;
            node.content = new ArrayList<>();
            switch (type) {
                case input:
                    EditText text = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    node.contentStyles = tag.styleList;
                    node.content.add(Html.toHtml(text.getText()));
                    list.add(node);
                    break;
                case img:
                    EditorControl imgTag = (EditorControl) view.getTag();
                    if (!TextUtils.isEmpty(imgTag.path)) {
                        node.content.add(imgTag.path);
                        list.add(node);
                    }
                    break;
            }
        }
        editorContent.nodes = list;
        return editorContent;
    }

    /**
     * 以EditorContent渲染编辑器
     *
     * @param content
     */
    public void renderEditor(EditorContent content) {
        this.mParentView.removeAllViews();
        for (Node item : content.nodes) {
            switch (item.type) {
                case input:
                    String text = item.content.get(0);
                    TextView view = inputExtensions.insertEditText(0, this.placeHolder, text);
                    if (item.contentStyles != null) {
                        for (EditorTextStyle style : item.contentStyles) {
                            inputExtensions.UpdateTextStyle(style, view);
                        }
                    }
                    break;
                case img:
                    String path = item.content.get(0);
                    imageExtensions.loadImage(path, "");
                    break;
            }
        }
    }

    /**
     * 判断是否是最后一行
     *
     * @param view
     * @return
     */
    public boolean isLastRow(View view) {
        int index = this.mParentView.indexOfChild(view);
        int count = this.mParentView.getChildCount();
        return count - 1 == index;
    }

    /**
     * 从html渲染编辑器
     *
     * @param content
     */
    public void renderEditorFromHtml(String content) {
        htmlextensions.parseHtml(content);
    }

    /**
     * 清除内容
     */
    public void clearAllContents() {
        this.mParentView.removeAllViews();

    }

    public void onBackspace(CustomEditText editText) {
        int selection = editText.getSelectionStart();
        if (selection == 0)
            return;
        editText.getText().delete(selection, 1);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            return false;
        }
        if (inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        CustomEditText nextFocus;
        if (selectionStart == 0 && length > 0) {
            int index = getParentView().indexOfChild(editText);
            if (index == 0)
                return false;
            nextFocus = inputExtensions.getEditTextPrevious(index);
            deleteFocusedPrevious(editText);
            nextFocus.setText(nextFocus.getText().toString() + editText.getText().toString());
        }
        return false;
    }
}
