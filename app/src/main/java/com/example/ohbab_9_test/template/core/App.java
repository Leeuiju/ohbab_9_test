package com.example.ohbab_9_test.template.core;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.ohbab_9_test.volley.StringRequestJson;
import com.example.ohbab_9_test.volley.VolleyInstance;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class App { public static final String DEVIL_PROJECT_ID = "1605234988599";

    private static App instance = null;
    private static Context context = null;
    public static void init(Context context){
        App.context = context;
    }
    JSONObject member = null;

    public static App getInstance()
    {
        if( instance == null )
            instance = new App();
        return instance;
    }

    public App() {
        try {
            SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
            loginToken = pref.getString("TOKEN", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    boolean readyDeepLink = false;
    public void setReadyDeepLink(boolean ready){
        this.readyDeepLink = ready;
    }
    public boolean isReadyDeepLink(){
        return readyDeepLink;
    }

    Uri reservedDeepLink = null;
    public void setReservedDeppLink(Uri deepLink){
        this.reservedDeepLink = deepLink;
    }

    public void consumeReservedDeepLink(){
        if(reservedDeepLink == null)
            return;
        /**
         * 초대하면 다음과 같이 나온다.
         */
        if(reservedDeepLink.getPath().equals("/share")) {

        } else if(reservedDeepLink.getPath().equals("/product")) {

        }
        reservedDeepLink = null;
    }

    public void setName(String name){
        try {
            if (member != null)
                member.put("name", name);
            SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
            pref.edit().putString("NAME", name).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getName(){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String n = pref.getString("NAME", null);
        if(n != null)
            return n;
        if(member != null)
            return member.optString("name");
        return null;
    }

    public void setInviteShortCutUrl(String url){
        try {
            SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
            pref.edit().putString("INVITE_SHORT_CUT_URL", url).commit();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getInviteShortCutUrl(){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String n = pref.getString("INVITE_SHORT_CUT_URL", null);
        return n;
    }

    public void setInviteCode(String code){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        pref.edit().putString("INVITE_CODE", code).commit();
    }
    public String getInviteCode(){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        String n = pref.getString("INVITE_CODE", null);
        return n;
    }

    public String getProfile(){
        if(member != null)
            return member.optString("profile");
        return null;
    }

    public String getMemberNo(){
        if(member != null)
            return member.optString("member_no");
        return null;
    }

    public String getEmail(){
        if(member != null)
            return member.optString("email");
        return null;
    }

    public String getLoginType(){
        if(member != null)
            return member.optString("type");
        return null;
    }

    public String getUdid(){
        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return android_id;
    }



    public boolean shouldTracking(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public interface Listener{
        void onComplete(boolean success, JSONObject res);
    }

    public void checkMember(final String type, final String identifier, final Listener listener){
        request("/member/check", new HashMap<String, String>() {{
            put("type", type);
            put("identifier", identifier);
        }}, new HttpCallback() {
            @Override
            public void onComplete(boolean success, JSONObject response) {
                listener.onComplete(success, response);
            }
        });
    }

    public boolean isLogin(){
        return loginToken != null;
    }

    public void isLogin(final Listener listener){
        request("/member/islogin", null, new HttpCallback() {
            @Override
            public void onComplete(boolean success, JSONObject response) {
                member = response.optJSONObject("member");
                listener.onComplete(success, response);
            }
        });
    }

    public void logout(){
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        }
        else {
            cookieManager.removeAllCookie();

        }

        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        pref.edit().remove("TOKEN").commit();
        member = null;
        loginToken = null;
        setReadyDeepLink(false);
    }

    public void login(final String type, final String email, final String passwordOrAccessToken, final Listener listener){
        request("/member/login", new HashMap<String, String>() {{
            put("type", type);
            put("email", email);
            put("pass", passwordOrAccessToken);
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            put("udid", android_id);
        }}, new HttpCallback() {
            @Override
            public void onComplete(boolean success, JSONObject response) {
                if(success && response.optBoolean("r")) {
                    member = response.optJSONObject("member");

                    String token = response.optString("token");
                    SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
                    pref.edit().putString("TOKEN", token).commit();

                    sendPush();
                }
                listener.onComplete(success, response);
            }
        });
    }
    public void joinMember(final String type, final String identifier, final String email, final String password,
                           final String name, final String profile, final String sex, final String age, final String agree1, final String agree2,
                           final String agree3, final String agree4, final String agree5,
                           final Listener listener){
        request("/member/join", new HashMap<String, String>() {{
            put("type", type);
            put("identifier", identifier);
            put("email", email);
            put("password", password);
            put("name", name);
            put("profile", profile);
            put("sex", sex);
            put("age", age);
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            put("udid", android_id);
            put("agree1", agree1);
            put("agree2", agree2);
            put("agree3", agree3);
            put("agree4", agree4);
            put("agree5", agree5);
        }}, new HttpCallback() {
            @Override
            public void onComplete(boolean success, JSONObject response) {
                listener.onComplete(success, response);
            }
        });
    }

    public interface HttpStringCallback{
        void onComplete(boolean success, String response);
    }

    public void request(String url, final HttpStringCallback callback){
        StringRequest req = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onComplete(true, response);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onComplete(false, null);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyInstance.getInstance().getRequestQueue().add(req);
    }

    public interface HttpCallback{
        void onComplete(boolean success, JSONObject response);
    }

    public void request(String path, Map<String, String> param, final HttpCallback callback) {
        String url = Config.HOST_API + path;


        if(param != null) {
            Object[] keys = param.keySet().toArray();
            for(Object key : keys)
                if (param.get(key) == null)
                    param.remove(key);
        }

        StringRequestJson req = new StringRequestJson(url, param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            callback.onComplete(true, new JSONObject(response));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    JSONObject res = new JSONObject();
                    res.put("msg", "일시적인 네트워크 오류가 발생하였습니다.");
                    callback.onComplete(false, res);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        VolleyInstance.getInstance().getRequestQueue().add(req);
    }


    public void savePushToken(String token){
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        pref.edit().putString("FCM", token).commit();
    }
    public void sendPush(){
        try {
            String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
            String fcm = pref.getString("FCM", null);
            if(fcm == null)
                return;
            String url = Config.HOST_API + "/push/key?fcm="+fcm + "&udid="+android_id + "&os=android";
            StringRequestJson req = new StringRequestJson(url, null,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("push", response);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleyInstance.getInstance().getRequestQueue().add(req);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    String loginToken;
    public void saveLoginToken(String token){
        loginToken = token;
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        pref.edit().putString("TOKEN", token).commit();
    }
    public void removeLoginToken(){
        loginToken = null;
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        pref.edit().remove("TOKEN").commit();
    }
    public String getLoginToken(){
        return loginToken;
    }
}

