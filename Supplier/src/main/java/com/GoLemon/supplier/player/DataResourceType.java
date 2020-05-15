package com.GoLemon.supplier.player;

import android.net.Uri;
import android.text.TextUtils;

public class DataResourceType {
    public static final String SCHEME_ASSET = "asset";
    public static final String SCHEME_CONTENT = "content";

    public static boolean isLocalFileUri(Uri uri) {
        String scheme = uri.getScheme();
        return TextUtils.isEmpty(scheme) || "file".equals(scheme);
    }
}
