package com.example.ohbab_9_test.volley;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.File;

public class VolleyInstance {
    private static Context mContext = null;
    private static VolleyInstance mInstance = null;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private DiskBasedCache diskBasedCache;

    public class SimpleRetryPolicy extends DefaultRetryPolicy
    {
        @Override
        public int getCurrentTimeout() {
            return 10000;
        }
    }
    SimpleRetryPolicy policy;
    private VolleyInstance(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
        diskBasedCache = new DiskBasedCache(new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/volleyImageDisk"), 100000000);
        mImageLoader = new ImageLoader(this.mRequestQueue, new BitmapLruCache());
        policy = new SimpleRetryPolicy();
    }

    public static VolleyInstance getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("Did you call VolleySingleton.initialize()?");
        }
        return mInstance;
    }

    public static Context getContext() {
        return mContext;
    }

    public SimpleRetryPolicy getPolicy() {
        return policy;
    }

    public static void init(Context context) {
        mContext = context;
        if (mInstance == null) {
            mInstance = new VolleyInstance(context);
        }
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

}


