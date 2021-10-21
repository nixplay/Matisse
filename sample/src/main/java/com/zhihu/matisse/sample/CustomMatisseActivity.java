package com.zhihu.matisse.sample;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.ImageEngine;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.CaptureStrategy;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.model.SelectedItemCollection;
import com.zhihu.matisse.listener.OnCameraSelected;
import com.zhihu.matisse.listener.SelectionDelegate;

import java.util.List;
import java.util.Set;

/**
 * Custom Matisse
 */
public class CustomMatisseActivity extends AppCompatActivity implements View.OnClickListener, SelectionDelegate, OnCameraSelected {

    private static final int REQUEST_CODE_CHOOSE = 23;
    private static final String TAG = CustomMatisseActivity.class.getSimpleName();

    private List<Uri> mSelectedUris;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_matisse);

        findViewById(R.id.btn_go).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            // Use Uri result
            TextView resultTextView = (TextView) findViewById(R.id.tv_result);
            resultTextView.setText("");
            mSelectedUris = Matisse.obtainResult(data);
            for (Uri uri : mSelectedUris) {
                resultTextView.append(uri.toString());
                resultTextView.append("\n");
            }

            // Use absolute path result
            TextView pathTextView = (TextView) findViewById(R.id.tv_path_result);
            pathTextView.setText("");
            List<String> pathResult = Matisse.obtainPathResult(data);
            for (String path : pathResult) {
                pathTextView.append(path);
                pathTextView.append("\n");
            }
        }
    }

    @Override
    public void onClick(View v) {
        CheckBox imageCheckBox = (CheckBox) findViewById(R.id.cb_choice_image);
        CheckBox videoCheckBox = (CheckBox) findViewById(R.id.cb_choice_video);
        RadioButton zhihuRadioButton = (RadioButton) findViewById(R.id.rb_theme_zhihu);
        RadioButton draculaRadioButton = (RadioButton) findViewById(R.id.rb_theme_dracula);
        RadioButton customThemeButton = (RadioButton) findViewById(R.id.rb_theme_custom);
        RadioButton glideRadioButton = (RadioButton) findViewById(R.id.rb_glide);
        RadioButton picassoRadioButton = (RadioButton) findViewById(R.id.rb_picasso);
        RadioButton uilRadioButton = (RadioButton) findViewById(R.id.rb_uil);
        EditText selectCountEditor = (EditText) findViewById(R.id.et_selectable_count);
        EditText selectVideoCountEditor = (EditText) findViewById(R.id.et_video_selectable_count);

        CheckBox countableCheckBox = (CheckBox) findViewById(R.id.cb_countable);
        CheckBox captureCheckBox = (CheckBox) findViewById(R.id.cb_capture);

        Set<MimeType> mimeTypes;
        if (imageCheckBox.isChecked() && videoCheckBox.isChecked()) {
            mimeTypes = MimeType.ofAll();
        } else if (imageCheckBox.isChecked()) {
            mimeTypes = MimeType.ofImage();
        } else {
            mimeTypes = MimeType.ofVideo();
        }

        ImageEngine imageEngine = null;
        if (glideRadioButton.isChecked()) {
            imageEngine = new GlideEngine();
        } else if (picassoRadioButton.isChecked()) {
            imageEngine = new PicassoEngine();
        } else if (uilRadioButton.isChecked()) {
            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
            imageEngine = new UILEngine();
        }

        String maxCount = selectCountEditor.getText().toString();
        String maxVideoCount = selectVideoCountEditor.getText().toString();
        int maxSelectable = Integer.parseInt(maxCount);
        int maxVideoSeletable = Integer.parseInt(maxVideoCount);

        int theme = R.style.Matisse_Dracula;
        if (zhihuRadioButton.isChecked()) {
            theme = R.style.Matisse_Zhihu;
        } else if (draculaRadioButton.isChecked()) {
            theme = R.style.Matisse_Dracula;
        } else if (customThemeButton.isChecked()) {
            theme = R.style.CustomTheme;
        } else {
            // custom theme
        }

        boolean countable = countableCheckBox.isChecked();
        boolean capture = captureCheckBox.isChecked();

        /*Matisse.from(this)
                .choose(mimeTypes, false)
                .showSingleMediaType(false)
                .capture(capture)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider", "Pictures"))
                .countable(countable)
                .maxSelectable(maxSelectable)
                .enablePreview(false)
//                .maxSelectablePerMediaType(maxSelectable, maxVideoSeletable)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(imageEngine)
                .theme(theme)
                .delegate(this)
                .allowsMultipleSelection(true)
                .maxVideoLength(120)
                .hasFeatureEnabled(true)
                .dontShowVideoAlert(false)
                .forResult(REQUEST_CODE_CHOOSE, mSelectedUris);*/

        Matisse.from(this)
                .choose(mimeTypes, false)
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                .maxSelectable(maxSelectable)
                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(imageEngine)
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.d("onSelected", "onSelected: pathList=" + pathList);
                })
                .showSingleMediaType(true)
                .originalEnable(true)
                .maxOriginalSize(10)
                .maxVideoLength(120)
                .allowsMultipleSelection(true)
                .delegate(this)
                .hasFeatureEnabled(true)
                .dontShowVideoAlert(true)
                .theme(theme)
                .autoHideToolbarOnSingleTap(true)
                .setOnSelectedListener((uriList, pathList) -> {
                    Log.d("onSelected", "onSelected: pathList=" + pathList);
                })
                .setOnCheckedListener(isChecked -> {
                    Log.d("isChecked", "onCheck: isChecked=" + isChecked);
                })
                .setOnCameraSelectedListener(this)
                .forResult(REQUEST_CODE_CHOOSE, mSelectedUris);

    }

    @Override
    public String getCause(SelectedItemCollection.MaxItemReach reach) {

        switch (reach) {
            case MIX_REACH:
                return "Mix cause";
            case IMAGE_REACH:
                return "Image cause";
            case VIDEO_REACH:
                return "Video cause";
            default:
                return "My cause";
        }

    }

    @Override
    public void onTapItem(Item item, Boolean isDontShow) {
        if (item != null) {
            Log.d("ACTIVITY_MATISSE", String.format("DURATION: %d seconds", (item.duration/1000)));
        }
    }

    @Override
    public void cameraSelected() {
        Log.d(getClass().getName(), "Camera was selected");
    }
}
