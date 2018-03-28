package com.coffee.editor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class Node {
    public EditorType type;
    public ArrayList<String> content;
    public List<EditorTextStyle> contentStyles;
}
