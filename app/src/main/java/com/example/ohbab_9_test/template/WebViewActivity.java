package com.example.ohbab_9_test.template;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.ohbab_9_test.R;
import com.example.ohbab_9_test.template.core.Config;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.List;

public class WebViewActivity extends SubActivity{
    WebView webview = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    String startUrl = null;
    JSONObject res = null;

    public static void goUrlAbsolute(String path, String title){
        Intent intent = new Intent(BaseActivity.instance, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", path);
        BaseActivity.instance.startActivity(intent);
    }

    public static void load(String path, String title){
        Intent intent = new Intent(BaseActivity.instance, WebViewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", Config.HOST_WEB + path);
        //intent.putExtra("url", "https://m.google.co.kr");
        BaseActivity.instance.startActivity(intent);
    }

    public static void load(String path, JSONObject res){
        Intent intent = new Intent(BaseActivity.instance, WebViewActivity.class);
        intent.putExtra("url", Config.HOST_WEB + path);
        intent.putExtra("res", res.toString());
        //intent.putExtra("url", "https://m.google.co.kr");
        BaseActivity.instance.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            String title = getIntent().getStringExtra("title");
            setTitle(title);
            String url = getIntent().getStringExtra("url");
            String strres = getIntent().getStringExtra("res");
            if(strres != null)
                res = new JSONObject(strres);

            showIndicator();

            FrameLayout contentView = findViewById(R.id.contentView);
            webview = new WebView(this);
            contentView.addView(webview);
            LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.LEFT | Gravity.TOP);
            l.weight = 1.0f;
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webview.setLongClickable(true);
            webview.setFocusable(true);
            webview.setFocusableInTouchMode(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setDisplayZoomControls(false);
            webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webview.getSettings().setSavePassword(true);
            webview.getSettings().setSaveFormData(true);
            webview.getSettings().setSupportMultipleWindows(true);

            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setDatabaseEnabled(true);
            webview.getSettings().setAppCachePath("storage");
            webview.getSettings().setAppCacheEnabled(true);
            webview.setWebContentsDebuggingEnabled(true);

            webview.setWebChromeClient(new MyWebChromeClient());
            webview.setWebViewClient(new MyWebViewClient());

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            if (Build.VERSION.SDK_INT >= 21)
                CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String token = pref.getString("TOKEN", null);
            startUrl = url.contains("?")? url+"&token="+token : url+"?token="+token;

            webview.loadUrl(startUrl);
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//            findViewById(R.id.right).setVisibility(View.VISIBLE);
//            ((ImageView)findViewById(R.id.right)).setImageResource(R.drawable.refresh);
//            findViewById(R.id.right).setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    webview.loadUrl(startUrl);
//                }
//            });
        }catch(Exception e){
            e.printStackTrace();
        }


    }





    class MyWebChromeClient extends WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
//    		super.onProgressChanged(view, newProgress);
//    		bar.setProgress( (int)(20+ (float)newProgress / 100 * 80));
        }

        @Override
        public boolean onJsAlert(WebView view, String url, final String message, final android.webkit.JsResult result)
        {
            new AlertDialog.Builder(WebViewActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.confirm();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        };

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {

            new AlertDialog.Builder(WebViewActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes,
                            new AlertDialog.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if(DialogInterface.BUTTON_POSITIVE == which)
                                        result.confirm();
                                }
                            })
                    .setNegativeButton(android.R.string.no,
                            new AlertDialog.OnClickListener()
                            {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    result.cancel();
                                }
                            })
                    .setCancelable(false)
                    .create()
                    .show();

            return true;
        }
    }



    class MyWebViewClient extends WebViewClient
    {
        public MyWebViewClient()
        {
            super();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)  {
            Log.e("WEBVIEW",url);
            if(url.startsWith("intent://"))
            {
                try {

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.CONTENT, url);
                    mFirebaseAnalytics.logEvent("intent_called", bundle);

                    Intent intent = Intent.parseUri(url, 0);

                    boolean packageAvailable = false;

                    List<ResolveInfo> list = WebViewActivity.this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

                    if (list == null)
                        packageAvailable = false;
                    else
                        packageAvailable = list.size() > 0;

                    if (packageAvailable) {
                        WebViewActivity.this.startActivity(intent);
                    } else {
                        if (intent != null && url.startsWith("intent:")) {
                            final String appPackageName = intent.getPackage();
                            try {
                                WebViewActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                WebViewActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    }
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            hideIndicator();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showIndicator();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            hideIndicator();
        }
    }

    public void go(String path){
        String url = Config.HOST_WEB + path;
        webview.loadUrl(url);
    }

    public void webViewBack(WebView webview)
    {
        WebBackForwardList l = webview.copyBackForwardList();
        if(webview.copyBackForwardList().getCurrentIndex()!=0)
        {
            webview.goBack();
        }
    }

    @Override
    public void onBackPressed(){

        int index = -1;
        if(webview != null)
        {
            index = webview.copyBackForwardList().getCurrentIndex();
            if(index <= 0)
                finish();
            else
                webViewBack(webview);
        }
        else
        {
            finish();
        }
    }
}
