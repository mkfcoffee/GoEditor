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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
public class MainActivity extends AppCompatActivity {

    private static final String KEY_CONTENT = "key_content";
    private static final int REQUEST_CODE_PERMISSION = 0x12;
    RelativeLayout communityRl;
    TextView communityTv;
    TextView pageTitleTv;
    EditText titleEt;
    Toolbar toolbar;
    NativeEditor nativeEditor;
    private Dialog linkInputDialog;

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
        initRichEditor();
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("退出编辑")
                .setMessage("Are you sure you want to exit the editor?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void initRichEditor() {

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoldClick(v);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItalicClick(v);
            }
        });

        findViewById(R.id.action_insert_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage(v);
            }
        });

        findViewById(R.id.action_insert_link).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLinkClick(v);
            }
        });

        //设置图片加载器，必须
        nativeEditor.setImageLoader(new IImageLoader() {
            @Override
            public void loadIntoImageView(ImageView imageView, Uri uri) {
                Glide.with(MainActivity.this).load(uri).apply(new RequestOptions().centerCrop()).into(imageView);
            }
        });

        //设置图片上传
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


    private void addImage(View v) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            startSelectImage();
        }
    }

    private void onBoldClick(View v) {
        nativeEditor.toggleBoldSelectText();
    }

    private void onItalicClick(View v) {
        nativeEditor.toggleItalicSelectText();
    }

    private void onLinkClick(View v) {
        if (linkInputDialog == null) {
            View linkInputView = LayoutInflater.from(this).inflate(R.layout.dialog_link, null);
            final EditText etText = (EditText) linkInputView.findViewById(R.id.et_text);
            final EditText etLink = (EditText) linkInputView.findViewById(R.id.et_link);
            linkInputDialog = new AlertDialog.Builder(this)
                    .setTitle("添加链接")
                    .setView(linkInputView)
                    .setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String text = etText.getText().toString().trim();
                            String link = etLink.getText().toString().trim();
                            if (text.isEmpty() || link.isEmpty()) {
                                Toast.makeText(getApplication(), "内容不能为空", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            addLinkText(text, link);
                        }
                    }).create();
        }

        linkInputDialog.show();
    }

    private void addLinkText(String text, String link) {
        nativeEditor.insertHyperlink(text, link);
    }

    private void startSelectImage() {
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量 int
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .previewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .glideOverride(160, 160)// int glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .isGif(true)// 是否显示gif图片 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }

}
