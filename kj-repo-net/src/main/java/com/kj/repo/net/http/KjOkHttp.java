package com.kj.repo.net.http;

import okhttp3.OkHttpClient;

public class KjOkHttp {
    public static final OkHttpClient DEFAULT;

    static {
        DEFAULT = new OkHttpClient();
    }

    public static OkHttpClient getClient() {
        return DEFAULT;
    }
}
