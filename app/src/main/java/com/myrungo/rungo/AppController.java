package com.myrungo.rungo;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.myrungo.rungo.utils.MyTrackerReceiver;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import io.fabric.sdk.android.Fabric;


public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        initFabric();

        initYandexMetrica();

        mInstance = this;
    }

    private void initFabric() {
        Fabric.with(this, new Crashlytics());
    }

    private void initYandexMetrica() {
        // Создание расширенной конфигурации библиотеки.
        YandexMetricaConfig config = YandexMetricaConfig
                .newConfigBuilder("bb71ae13-3e41-4920-9ac2-0258337217b0")
                .withLocationTracking(false)
                .withCrashReporting(true)
                .withStatisticsSending(true)
                .withNativeCrashReporting(true)
                .withInstalledAppCollecting(true)
                .build();

        // Инициализация AppMetrica SDK.
        YandexMetrica.activate(this, config);
        // Отслеживание активности пользователей.
        YandexMetrica.enableActivityAutoTracking(this);

        //If AppMetrica has received referrer broadcast our own MyTrackerReceiver prints it to log
        YandexMetrica.registerReferrerBroadcastReceivers(new MyTrackerReceiver());

        YandexMetrica.setLocationTracking(false);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }
        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}