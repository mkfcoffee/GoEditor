package com.coffee.editor.component;

import android.widget.TextView;

import com.coffee.editor.EditorCore;
import com.coffee.editor.model.EditorContent;
import com.coffee.editor.model.EditorTextStyle;
import com.coffee.editor.model.EditorType;
import com.coffee.editor.model.HtmlTag;
import com.coffee.editor.model.Node;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class HTMLExtensions {
    EditorCore editorCore;

    public HTMLExtensions(EditorCore editorCore) {
        this.editorCore = editorCore;
    }

    public void parseHtml(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        Element body = doc.body();
        for (Element element : body.children()) {
            if (!matchesTag(element.tagName().toLowerCase()))
                continue;
            buildNode(element);
        }
    }

    private void buildNode(Element element) {
        String text;
        HtmlTag tag = HtmlTag.valueOf(element.tagName().toLowerCase());
        int count = editorCore.getParentView().getChildCount();
        if ("<br>".equals(element.html().replaceAll("\\s+", "")) || "<br/>".equals(element.html().replaceAll("\\s+", ""))) {
            editorCore.getInputExtensions().insertEditText(count, null, null);
            return;
        }
        switch (tag) {
            case p:
                text = element.html();
                editorCore.getInputExtensions().insertEditText(count, null, text);
                break;
            case img:
                renderImage(element);
                break;
            case div:
                buildNode(element);
                break;
        }
    }

    private void renderDiv(Element element) {
        String tag = element.attr("data-tag");
        if (tag.equals("img")) {
            renderImage(element);
        }
    }

    private void renderImage(Element element) {
        Element img = element.child(0);
        Element descTag = element.child(1);
        String src = img.attr("src");
        String desc = descTag.html();
        int Index = editorCore.getParentChildCount();
        editorCore.getImageExtensions().executeDownloadImageTask(src, Index, desc);
    }

    private void renderHeader(HtmlTag tag, Element element) {
        int count = editorCore.getParentView().getChildCount();
        String text = getHtmlSpan(element);
        TextView editText = editorCore.getInputExtensions().insertEditText(count, null, text);
        editorCore.getInputExtensions().UpdateTextStyle(null, editText);
    }

    private String getHtmlSpan(Element element) {
        Element el = new Element(Tag.valueOf("span"), "");
        el.attributes().put("style", element.attr("style"));
        el.html(element.html());
        return el.toString();
    }

    private boolean hasChildren(Element element) {
        return element.getAllElements().size() > 0;
    }


    private static boolean matchesTag(String test) {
        for (HtmlTag tag : HtmlTag.values()) {
            if (tag.name().equals(test)) {
                return true;
            }
        }
        return false;
    }

    private String getTemplateHtml(EditorType child) {
        String template = null;
        switch (child) {
            case input:
                template = "<{{$tag}} {{$style}}>{{$content}}</{{$tag}}>";
                break;
            case img:
                template = "<p><a href= \"{{$content}}\"><img src=\"{{$content}}\"></a></p>";
                break;

        }
        return template;
    }

    private String getInputHtml(Node item) {
        boolean isParagraph = true;
        String tmpl = getTemplateHtml(EditorType.input);
        String trimmed = Jsoup.parse(item.content.get(0)).body().select("p").html();
        if (item.contentStyles.size() > 0) {
            for (EditorTextStyle style : item.contentStyles) {
                switch (style) {
                    case BOLD:
                        tmpl = tmpl.replace("{{$content}}", "<b>{{$content}}</b>");
                        break;
                    case BOLDITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<b><i>{{$content}}</i></b>");
                        break;
                    case ITALIC:
                        tmpl = tmpl.replace("{{$content}}", "<i>{{$content}}</i>");
                        break;
                    case NORMAL:
                        tmpl = tmpl.replace("{{$tag}}", "p");
                        isParagraph = true;
                        break;
                }
            }
            if (isParagraph) {
                tmpl = tmpl.replace("{{$tag}}", "p");
            }
            tmpl = tmpl.replace("{{$content}}", trimmed);
            tmpl = tmpl.replace("{{$style}}", "");
            return tmpl;
        }
        tmpl = tmpl.replace("{{$tag}}", "p");
        tmpl = tmpl.replace("{{$content}}", trimmed);
        tmpl = tmpl.replace(" {{$style}}", "");
        return tmpl;
    }

    public String getContentAsHTML(EditorContent content) {
        StringBuilder htmlBlock = new StringBuilder();
        String html;
        for (Node item : content.nodes) {
            switch (item.type) {
                case input:
                    html = getInputHtml(item);
                    htmlBlock.append(html);
                    break;
                case img:
                    htmlBlock.append(getTemplateHtml(item.type).replace("{{$content}}", item.content.get(0)));
                    break;
                case hr:
                    htmlBlock.append(getTemplateHtml(item.type));
            }
        }
        return htmlBlock.toString();
    }

    public String getContentAsHTML(String editorContentAsSerialized) {
        EditorContent content = editorCore.getContentDeserialized(editorContentAsSerialized);
        return getContentAsHTML(content);
    }
}