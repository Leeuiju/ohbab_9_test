package com.example.ohbab_9_test.template;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.ohbab_9_test.template.core.App;
import com.example.ohbab_9_test.volley.StringRequestJson;
import com.example.ohbab_9_test.volley.VolleyInstance;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.july.devil.WildCardConstructor;
import kr.co.july.devil.WildCardFrameLayout;
import kr.co.july.devil.WildCardMeta;
import kr.co.july.devil.core.DevilSdk;
import kr.co.july.devil.login.DevilLoginSdk;

public class MobileApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseAnalytics.getInstance(this);
        VolleyInstance.init(this);
        App.init(this);
        initSketchToApp();
        DevilSdk.init(this);
        DevilLoginSdk.init(this);
        DevilSdk.setFirstActivity(FirstActivity.class);

    }



    //스케치로 연결
    public void initSketchToApp()
    {
        WildCardConstructor.setOnNetworkImageCall(ImageView.class, new WildCardConstructor.ImageLoader() {
            @Override
            public void onLoad(WildCardMeta meta, ImageView iv, String url, boolean isLocal) {
                if(url == null || url.equals("")) {
                    return;
                }

                if(iv.getParent() instanceof WildCardFrameLayout){
                    WildCardFrameLayout parent  = ((WildCardFrameLayout) iv.getParent());
                    if(parent != null && parent.isBorderRound()) {
                        int round = (int)parent.getBorderRoundCorner();
                        Glide.with(getApplicationContext()).load(url)
                                .transform(new CenterCrop(),new RoundedCorners(round))
                                .transition(DrawableTransitionOptions.withCrossFade(200))
                                .into(iv);
                    } else
                        Glide.with(getApplicationContext()).load(url).centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade(200)).into(iv);
                }
                else
                    Glide.with(getApplicationContext()).load(url).centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade(200)).into(iv);
            }


            @Override
            public void onHttp(String url, final WildCardConstructor.WildCardHttpResponse wildCardHttpResponse) {
                StringRequestJson request = new StringRequestJson(url, null, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject res = new JSONObject(response);
                            wildCardHttpResponse.onResponse(res);
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    wildCardHttpResponse.onResponse(null);
                                }catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                VolleyInstance.getInstance().getRequestQueue().add(request);
            }
            @Override
            public void onHttpGet(String url, Map<String, String> header, final WildCardConstructor.WildCardHttpResponse callback) {

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponse(null);
                    }

                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> r = new HashMap<>();
                        for (Object k : header.keySet().toArray()) {
                            r.put(k.toString(), header.get(k));
                        }
                        r.put("Accept", "application/json");
                        return r;
                    }
                };

                VolleyInstance.getInstance().getRequestQueue().add(request);
            }

            @Override
            public void onHttpPost(String url, Map<String, String> header, final JSONObject postData, final WildCardConstructor.WildCardHttpResponse callback) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponse(null);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> r = new HashMap<>();
                        for (Object k : header.keySet().toArray()) {
                            r.put(k.toString(), header.get(k));
                        }
                        r.put("Content-Type", "application/json");
                        r.put("Accept", "application/json");
                        return r;
                    }
                };

                VolleyInstance.getInstance().getRequestQueue().add(request);
            }

            @Override
            public void onHttpPut(final String url, Map<String, String> header, final JSONObject postData, final WildCardConstructor.WildCardHttpResponse callback) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, postData, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onResponse(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onResponse(null);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> r = new HashMap<>();
                        for (Object k : header.keySet().toArray()) {
                            r.put(k.toString(), header.get(k));
                        }
                        r.put("Content-Type", "application/json");
                        r.put("Accept", "application/json");
                        return r;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleyInstance.getInstance().getRequestQueue().add(request);
            }
        });

        WildCardConstructor.setOnCustomAction(new WildCardConstructor.CustomActionCallback() {
            @Override
            public void onAction(Context context, WildCardMeta meta, String functionName, List<String> args, View triggerView) {

                try{


                }catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
