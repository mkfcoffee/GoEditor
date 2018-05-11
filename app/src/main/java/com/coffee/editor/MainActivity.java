package com.coffee.editor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.List;

/**
 * Created by Mcoffee on 2018/3/20.
 * Email: mkfcoffee@163.com
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String KEY_CONTENT = "key_content";
    private static final int REQUEST_CODE_PERMISSION = 0x12;
    RelativeLayout communityRl;
    TextView communityTv;
    TextView pageTitleTv;
    EditText titleEt;
    Toolbar toolbar;
    NativeEditor nativeEditor;
    private ImageButton boldIbt;
    private ImageButton italicIbt;
    private ImageButton imageIbt;
    private ImageButton linkIbt;
    private Dialog linkTextDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nativeEditor = findViewById(R.id.native_editor);
        communityRl = (RelativeLayout) findViewById(R.id.editor_communityRl);
        communityTv = (TextView) findViewById(R.id.editor_community);
        pageTitleTv = (TextView) findViewById(R.id.editor_page_title);
        titleEt = (EditText) findViewById(R.id.editor_title);
        toolbar = (Toolbar) findViewById(R.id.editor_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        pageTitleTv.setText("文章");
        initEditor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        MenuItem menuItem = menu.findItem(R.id.action_release);
        if (menuItem != null) {
            menuItem.setTitle(R.string.release_article);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_release:
                String content = nativeEditor.getHtmlContent();
                if (!TextUtils.isEmpty(content)) {
                    Log.v("htmlContent", content);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == PictureConfig.CHOOSE_REQUEST && resultCode == RESULT_OK) {
            List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
            if (selectList != null && selectList.size() > 0) {
                LocalMedia localMedia = selectList.get(0);
                String path = localMedia.getPath();
                if (!TextUtils.isEmpty(path)) {
                    Uri uri = Uri.fromFile(new File(path));
                    nativeEditor.insertImage(uri);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_CODE_PERMISSION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startSelectImage();
            }
        }
    }

    /**
     * 初始化编辑器
     */
    private void initEditor() {
        boldIbt = findViewById(R.id.action_bold);
        italicIbt = findViewById(R.id.action_Italic);
        imageIbt = findViewById(R.id.action_insert_image);
        linkIbt = findViewById(R.id.action_insert_link);
        boldIbt.setOnClickListener(this);
        italicIbt.setOnClickListener(this);
        imageIbt.setOnClickListener(this);
        linkIbt.setOnClickListener(this);
        nativeEditor.setImageLoader(new IImageLoader() {
            @Override
            public void loadIntoImageView(ImageView imageView, Uri uri) {
                Glide.with(MainActivity.this).load(uri).apply(new RequestOptions().centerCrop()).into(imageView);
            }
        });

        nativeEditor.setUploadEngine(new IUploadEngine() {

            @Override
            public void uploadImage(Uri imgUri, UploadProgressListener listener) {
            }

            @Override
            public void cancelUpload(Uri imgUrl) {

            }
        });
        String htmlContent = getIntent().getStringExtra(KEY_CONTENT);
        if (!TextUtils.isEmpty(htmlContent)) {
            nativeEditor.setHtmlContent(htmlContent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_bold:
                textBoldStyle();
                break;
            case R.id.action_Italic:
                textItalicStyle();
                break;
            case R.id.action_insert_image:
                insertImage();
                break;
            case R.id.action_insert_link:
                showLinkTextDialog();
                break;
        }
    }

    /**
     * 插入图片
     */
    private void insertImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            startSelectImage();
        }
    }

    /**
     * 从相册选择照片
     */
    private void startSelectImage() {
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .imageSpanCount(3)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(true)
                .isZoomAnim(true)
                .sizeMultiplier(0.5f)
                .enableCrop(false)
                .compress(true)
                .glideOverride(160, 160)
                .isGif(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 字体加粗样式
     */
    private void textBoldStyle() {
        nativeEditor.toggleBoldSelectText();
    }

    /**
     * 字体倾斜样式
     */
    private void textItalicStyle() {
        nativeEditor.toggleItalicSelectText();
    }

    /**
     * 添加超链接文本
     *
     * @param text
     * @param link
     */
    private void addLinkText(String text, String link) {
        nativeEditor.insertHyperlink(text, link);
    }

    /**
     * 显示超链接输入框
     */
    private void showLinkTextDialog() {
        if (linkTextDialog == null) {
            View linkInputView = LayoutInflater.from(this).inflate(R.layout.dialog_link, null);
            final EditText etLink = (EditText) linkInputView.findViewById(R.id.et_link);
            final EditText etText = (EditText) linkInputView.findViewById(R.id.et_text);
            linkTextDialog = new AlertDialog.Builder(this)
                    .setView(linkInputView)
                    .setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = etText.getText().toString().trim();
                            String link = etLink.getText().toString().trim();
                            if (TextUtils.isEmpty(text) || TextUtils.isEmpty(link)) {
                                Toast.makeText(getApplication(), "内容不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            addLinkText(text, link);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
        }

        linkTextDialog.show();
    }
}
