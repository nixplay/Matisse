package com.zhihu.matisse.internal.entity;

import android.net.Uri;

import com.zhihu.matisse.MimeType;

public class MockItem extends Item {
    private Uri contentUri;

    private MockItem(String mimeType, Uri uri) {
        super(-1, mimeType, 0, 0);
        this.contentUri = uri;
    }

    @Override
    public Uri getContentUri() {
        return contentUri;
    }

    public static Item image() {
        return new MockItem(MimeType.JPEG.toString(), Uri.parse("file:///image.jpg"));
    }

    public static Item video() {
        return new MockItem(MimeType.MP4.toString(), Uri.parse("file:///video.mp4"));
    }
}
