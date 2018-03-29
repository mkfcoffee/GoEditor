package com.coffee.editor;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.text.Html;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
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
    /*
    * EditText initializors
    */
    public String placeHolder = null;
    /*
    * Divider initializors
    */
    private final String SHAREDPREFERENCE = "QA";
    private Context mContext;
    private Activity mActivity;
    protected LinearLayout mParentView;
    private RenderType renderType;
    private Resources resources;
    private View activeView;
    private Gson gson;
    private Utilities utilities;
    private EditorListener listener;
    public final int MAP_MARKER_REQUEST = 20;
    public final int PICK_IMAGE_REQUEST = 1;
    private InputExtensions inputExtensions;
    private ImageExtensions imageExtensions;
    private HTMLExtensions htmlextensions;

    public EditorCore(Context _context, AttributeSet attrs) {
        super(_context, attrs);
        this.mContext = _context;
        this.mActivity = (Activity) _context;
        this.setOrientation(VERTICAL);
        initialize(_context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        loadStateFromAttrs(attrs);
        utilities = new Utilities();
        this.resources = context.getResources();
        gson = new Gson();
        inputExtensions = new InputExtensions(this);
        imageExtensions = new ImageExtensions(this);
        htmlextensions = new HTMLExtensions(this);
        this.mParentView = this;
    }

    //region Getters_and_Setters

    /**
     *
     *
     * Exposed
     */

    /**
     * returns activity
     *
     * @return
     */
    public Activity getActivity() {
        return this.mActivity;
    }

    /**
     * used to get the editor node
     *
     * @return
     */
    public LinearLayout getParentView() {
        return this.mParentView;
    }

    /**
     * Get number of childs in the editor
     *
     * @return
     */
    public int getParentChildCount() {
        return this.mParentView.getChildCount();
    }

    /**
     * returns whether editor is set as Editor or Rendeder
     *
     * @return
     */
    public RenderType getRenderType() {
        return this.renderType;
    }

    /**
     * no idea what this is
     *
     * @return
     */
    public Resources getResources() {
        return this.resources;
    }

    /**
     * The current active view on the editor
     *
     * @return
     */
    public View getActiveView() {
        return this.activeView;
    }

    public void setActiveView(View view) {
        this.activeView = view;
    }

    public Utilities getUtilitiles() {
        return this.utilities;
    }

    public EditorListener getEditorListener() {
        return this.listener;
    }

    public void setEditorListener(EditorListener _listener) {
        this.listener = _listener;
    }

    /*
     *
     * Getters and setters for  extensions
     *
     */
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
            return; // quick exit
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.editor);
            this.placeHolder = a.getString(R.styleable.editor_placeholder);
            String renderType = a.getString(R.styleable.editor_render_type);
            if (TextUtils.isEmpty(renderType)) {
                this.renderType = RenderType.Editor;
            } else {
                this.renderType = renderType.toLowerCase().equals("renderer") ? RenderType.Renderer : RenderType.Editor;
            }

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
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
        View _view = this.activeView;
        if (_view == null)
            return size;
        int currentIndex = this.mParentView.indexOfChild(_view);
        EditorType tag = getControlType(_view);
        if (tag == EditorType.INPUT) {
            int length = ((EditText) this.activeView).getText().length();
            if (length > 0) {
                return type == EditorType.UL_LI || type == EditorType.OL_LI ? currentIndex : currentIndex;
            } else {
                return currentIndex;
            }
        } else {
            return size;
        }
    }

    public boolean containsStyle(List<EditorTextStyle> _Styles, EditorTextStyle style) {
        for (EditorTextStyle item : _Styles) {
            if (item == style) {
                return true;
            }
            continue;
        }
        return false;
    }

    public EditorControl updateTagStyle(EditorControl controlTag, EditorTextStyle style, Op _op) {
        List<EditorTextStyle> styles = controlTag._ControlStyles;
        if (_op == Op.Delete) {
            int index = styles.indexOf(style);
            if (index != -1) {
                styles.remove(index);
                controlTag._ControlStyles = styles;
            }
        } else {
            int index = styles.indexOf(style);
            if (index == -1) {
                styles.add(style);
            }
        }
        return controlTag;
    }

    public EditorType getControlType(View _view) {
        if (_view == null)
            return null;
        EditorControl _control = (EditorControl) _view.getTag();
        return _control.Type;
    }

    public EditorControl getControlTag(View view) {
        if (view == null)
            return null;
        EditorControl control = (EditorControl) view.getTag();
        return control;
    }

    public EditorControl createTag(EditorType type) {
        EditorControl control = new EditorControl();
        control.Type = type;
        control._ControlStyles = new ArrayList<>();
        switch (type) {
            case hr:
            case img:
            case INPUT:
            case ul:
            case UL_LI:
        }
        return control;
    }

    public void deleteFocusedPrevious(EditText view) {
        int index = mParentView.indexOfChild(view);
        if (index == 0)
            return;
        EditorControl contentType = (EditorControl) ((View) view.getParent()).getTag();

        View toFocus = mParentView.getChildAt(index - 1);
        EditorControl control = (EditorControl) toFocus.getTag();

        /**
         * If its an image or map, do not delete edittext, as there is nothing to focus on after image
         */
        if (control.Type == EditorType.img || control.Type == EditorType.map) {
            return;
        }
        /*
         *
         * If the person was on edittext,  had removed the whole text, we need to move into the previous line
         *
         */

        if (control.Type == EditorType.ol || control.Type == EditorType.ul) {
         /*
         *
         * previous node on the editor is a list, set focus to its inside
         *
         */
            this.mParentView.removeView(view);
        } else {
            removeParent(view);
        }
    }


    public int removeParent(View view) {
        int indexOfDeleteItem = mParentView.indexOfChild(view);
        View nextItem = null;
        //remove hr if its on top of the delete field
        this.mParentView.removeView(view);
        Log.d("indexOfDeleteItem", "indexOfDeleteItem : " + indexOfDeleteItem);

        for (int i = 0; i < indexOfDeleteItem; i++) {
            if (getControlType(mParentView.getChildAt(i)) == EditorType.INPUT) {
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

    public String getValue(String Key, String defaultVal) {
        SharedPreferences _Preferences = mContext.getSharedPreferences(SHAREDPREFERENCE, 0);
        return _Preferences.getString(Key, defaultVal);

    }

    public void putValue(String Key, String Value) {
        SharedPreferences _Preferences = mContext.getSharedPreferences(SHAREDPREFERENCE, 0);
        SharedPreferences.Editor editor = _Preferences.edit();
        editor.putString(Key, Value);
        editor.apply();
    }

    public String getContentAsSerialized() {
        EditorContent state = getContent();
        return serializeContent(state);
    }

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

    public EditorContent getContent() {

        if (this.renderType == RenderType.Renderer) {
            utilities.toastItOut("This option only available in editor mode");
            return null;
        }

        int childCount = this.mParentView.getChildCount();
        EditorContent editorState = new EditorContent();
        List<Node> list = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            Node node = new Node();
            View view = mParentView.getChildAt(i);
            EditorType type = getControlType(view);
            node.type = type;
            node.content = new ArrayList<>();
            switch (type) {
                case INPUT:
                    EditText _text = (EditText) view;
                    EditorControl tag = (EditorControl) view.getTag();
                    node.contentStyles = tag._ControlStyles;
                    node.content.add(Html.toHtml(_text.getText()));
                    list.add(node);
                    break;
                case img:
                    EditorControl imgTag = (EditorControl) view.getTag();
                    if (!TextUtils.isEmpty(imgTag.path)) {
                        node.content.add(imgTag.path);
                        list.add(node);
                    }
                    //field type, content[]
                    break;
                case hr:
                    list.add(node);
                    break;
            }
        }
        editorState.nodes = list;
        return editorState;
    }

    public void renderEditor(EditorContent _state) {
        this.mParentView.removeAllViews();
        for (Node item : _state.nodes) {
            switch (item.type) {
                case INPUT:
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
                    String desc = "";
                    imageExtensions.loadImage(path, desc);
                    break;
            }
        }
    }


    public boolean isLastRow(View view) {
        int index = this.mParentView.indexOfChild(view);
        int length = this.mParentView.getChildCount();
        return length - 1 == index;
    }


    public void renderEditorFromHtml(String content) {
        htmlextensions.parseHtml(content);
    }

    public void clearAllContents() {
        this.mParentView.removeAllViews();

    }

    public void onBackspace(CustomEditText editText) {
        int len = editText.getText().length();
        int selection = editText.getSelectionStart();
        if (selection == 0)
            return;
        editText.getText().delete(selection, 1);

//                if(editText.requestFocus())
//                editText.setSelection(editText.getText().length());
    }

    public boolean onKey(View v, int keyCode, KeyEvent event, CustomEditText editText) {
        if (keyCode != KeyEvent.KEYCODE_DEL) {
            return false;
        }
        if (inputExtensions.isEditTextEmpty(editText)) {
            deleteFocusedPrevious(editText);
            int controlCount = getParentChildCount();
            if (controlCount == 1)
                return checkLastControl();
            return false;
        }
        int length = editText.getText().length();
        int selectionStart = editText.getSelectionStart();

        EditorType editorType = getControlType(this.activeView);
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

    private boolean checkLastControl() {
        EditorControl control = getControlTag(getParentView().getChildAt(0));
        if (control == null)
            return false;
        switch (control.Type) {
            case ul:
            case ol:
                mParentView.removeAllViews();
                break;
        }

        return false;
    }

    public class Utilities {
        public int[] getScreenDimension() {
            Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            int[] dimen = {width, height};
            return dimen;
        }

        public void toastItOut(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }
}
