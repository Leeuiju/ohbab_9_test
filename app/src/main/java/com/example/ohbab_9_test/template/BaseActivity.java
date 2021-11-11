package com.example.ohbab_9_test.template;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.ohbab_9_test.R;
import com.example.ohbab_9_test.template.core.App;
import com.example.ohbab_9_test.view.LeftMenuView;
import com.example.ohbab_9_test.view.LoadingDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.json.JSONObject;

import java.util.List;

import kr.co.july.devil.WildCardConstructor;
import kr.co.july.devil.WildCardMeta;

public class BaseActivity extends AppCompatActivity {

    int screenWidth = 0;
    int screenHeight = 0;
    Dialog dialog;


    public static BaseActivity instance = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Display display = getWindowManager().getDefaultDisplay();
            screenWidth = display.getWidth();
            screenHeight = display.getHeight();

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.WHITE);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try{
            instance = this;
            checkDeppLink();

//            if(WildCardConstructor.getInstance().getAllBlockJson() == null && !(this instanceof FirstActivity)){
//                Intent intent = new Intent(getApplicationContext(), FirstActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            }

            if(!(this instanceof FirstActivity) &&  WildCardConstructor.getInstance().getAllBlockJson() == null){
                Intent intent = new Intent(this, FirstActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void checkDeppLink(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }

                        if(deepLink != null) {
                            getIntent().setData(null);
                            App.getInstance().setReservedDeppLink(deepLink);

                            if(App.getInstance().isReadyDeepLink())
                                App.getInstance().consumeReservedDeepLink();
                        }
                    }
                });
    }

    public void showAlert(String a)
    {

        new AlertDialog.Builder(BaseActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(a)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    public void showAlertWithConfirm(String a, AlertDialog.OnClickListener listener)
    {
        new AlertDialog.Builder(BaseActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(a)
                .setPositiveButton(android.R.string.ok, listener)
                .setCancelable(true)
                .create()
                .show();
    }

    public void showAlertWithConfirmTwo(String a, AlertDialog.OnClickListener listener)
    {
        new AlertDialog.Builder(BaseActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(a)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.no, listener)
                .setCancelable(true)
                .create()
                .show();
    }

    public void showAlertWithFinish(String a)
    {
        new AlertDialog.Builder(BaseActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert)
                .setMessage(a)
                .setPositiveButton(android.R.string.ok,
                        new AlertDialog.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                finish();
                            }
                        })
                .setCancelable(false)
                .create()
                .show();
    }

    public void showIndicator()
    {
        try {

            if(dialog == null)
            {
                dialog = new LoadingDialog(this);
                dialog.show();
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void hideIndicator()
    {
        try {
            if (dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public interface PermissionCallback{
        void onComplete(boolean success);
    }
    PermissionCallback callback = null;
    public void requestLocationPermission(AppCompatActivity activity, PermissionCallback ccallback){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        ) {
            ccallback.onComplete(true);
        } else {
            callback = ccallback;
            ActivityCompat.requestPermissions(activity, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    443);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 443){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                if(callback != null)
                    callback.onComplete(true);
            } else {
                if(callback != null)
                    callback.onComplete(false);
            }
            callback = null;
        }
    }

    public void closeMenu(){
        if(leftMenuView != null)
            leftMenuView.uiDown();
    }

    long lastQuitPress = 0;
    @Override
    public void onBackPressed(){

        if(leftMenuView != null && leftMenuView.isOpen() ){
            leftMenuView.uiDown();
        }
        else if(this instanceof MainActivity)
        {
            if(System.currentTimeMillis() - lastQuitPress > 1000) {
                Toast.makeText(this, "Please , Back button one more time for exit", Toast.LENGTH_SHORT).show();
                lastQuitPress = System.currentTimeMillis();
            } else {
//                if(!Bill.getInstance().isAboveMember1())
//                    Ads.getInstance().show();
                finish();
            }
        }
        else
        {
            finish();
        }
    }

    public static String commaInEvery3Digit(String number) {
        if (number == null)
            return null;
        if(number.contains(","))
            return number;
        boolean minus = false;
        if(number.startsWith("-"))
        {
            minus = true;
            number = number.replace("-", "");
        }
        String r = "";
        int rLen = 1;
        for (int i = number.length() - 1; i >= 0; i--) {
            r = number.charAt(i) + r;

            if ((rLen % 3) == 0 && i != 0)
                r = "," + r;
            rLen++;
        }
        if(minus)
            return "-" + r;
        else
            return r;
    }


    LeftMenuView leftMenuView;
    View leftMenuViewContent;
    JSONObject leftData = new JSONObject();


    public void createLeftMenu() throws Exception {

        Display display = getWindowManager().getDefaultDisplay();
        int screenWidth = display.getWidth();

        JSONObject leftMenuCloudJson = WildCardConstructor.getInstance().getBlockJson("C140F0A5-B13B-4DD3-B753-FBB329613607");
        View v = WildCardConstructor.constructLayer(getApplicationContext(), null, leftMenuCloudJson, new WildCardMeta.WildCardConstructorInstanceCallback() {
            @Override
            public boolean onInstanceCustomAction(Context context, WildCardMeta meta, String function, List<String> args, View triggerView) {
                return false;
            }
        });
        leftMenuViewContent = v;
        WildCardConstructor.applyRule(getApplicationContext(), leftMenuViewContent, leftData);
        leftMenuView = findViewById(R.id.leftMenu);
        leftMenuView.setContentView(this , v, screenWidth*290/375);
    }
}