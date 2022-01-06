/*
 * Copyright 2017 Zhihu Inc.
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
package com.zhihu.matisse.engine.impl;

import static androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180;
import static androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_270;
import static androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zhihu.matisse.engine.ImageEngine;

/**
 * {@link ImageEngine} implementation using Picasso.
 */

public class PicassoEngine implements ImageEngine {

    private static final String[] CONTENT_ORIENTATION = new String[] {
            MediaStore.Images.ImageColumns.ORIENTATION
    };

    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Picasso.get().load(uri).placeholder(placeholder)
                .resize(resize, resize)
                .centerCrop()
                .rotate(getRotation(getExifOrientation(context.getContentResolver(), uri)))
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 Uri uri) {
        loadThumbnail(context, resize, placeholder, imageView, uri);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(resizeX, resizeY)
                .priority(Picasso.Priority.HIGH)
                .centerInside().into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        loadImage(context, resizeX, resizeY, imageView, uri);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }

    private int getRotation(int rotationId) {
        int rotationValue = 0;
        switch (rotationId) {
            case ORIENTATION_ROTATE_90:
                rotationValue = 90;
                break;
            case ORIENTATION_ROTATE_180:
                rotationValue = 180;
                break;
            case ORIENTATION_ROTATE_270:
                rotationValue = 270;
                break;
        }

        return rotationValue;
    }

    private int getExifOrientation(ContentResolver contentResolver, Uri uri) {
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(uri, CONTENT_ORIENTATION, null, null, null);

            if (cursor == null || !cursor.moveToFirst()) {
                return 0;
            }
            // CONTENT_ORIENTATION returns the actual angle integer such as 90, 180, etc.
            // But BitmapHunter requires the ExifInterface's constants.
            int contentOrientation = cursor.getInt(0);
            int exifOrientation;
            switch (contentOrientation) {
                case 90:
                    exifOrientation = ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    exifOrientation = ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    exifOrientation = ORIENTATION_ROTATE_270;
                    break;
                default:
                    exifOrientation = 0;
                    break;
            }
            Log.d("PicassoEngine", "exifOrientation: " + exifOrientation);
            return exifOrientation;
        } catch (RuntimeException ignored) {
            // If the orientation column doesn't exist, assume no rotation.
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
