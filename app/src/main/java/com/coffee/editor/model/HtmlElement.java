package com.coffee.editor.model;


import org.jsoup.nodes.Element;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class HtmlElement {
    public String TagName;
    public Element _Element;

    public  HtmlElement(String _TagName, Element _Element){
        this.TagName= _TagName;
        this._Element= _Element;
    }
}

