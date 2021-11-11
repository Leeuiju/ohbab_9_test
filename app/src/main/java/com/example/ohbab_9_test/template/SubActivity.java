package com.example.ohbab_9_test.template;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ohbab_9_test.R;

import org.json.JSONObject;

import java.util.List;

import kr.co.july.devil.WildCardConstructor;
import kr.co.july.devil.WildCardFrameLayout;
import kr.co.july.devil.WildCardListView;
import kr.co.july.devil.WildCardMeta;

public class SubActivity extends BaseActivity implements WildCardMeta.WildCardConstructorInstanceCallback{

    ScrollView scrollView;
    FrameLayout contentView;
    WildCardListView listView;
    JSONObject data = new JSONObject();
    WildCardFrameLayout mainWc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        contentView = findViewById(R.id.contentView);
        scrollView = findViewById(R.id.scrollView);
        listView = findViewById(R.id.listView);
        data = new JSONObject();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void grayBg(){
        findViewById(R.id.root).setBackgroundColor(Color.parseColor("#FAFAFA"));
    }

    public void setTitle(String title) {
        findViewById(R.id.subtitle).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.subtitle)).setText(title);
    }

    public void constructContent(String blockKey){
        JSONObject cloudJson = WildCardConstructor.getInstance().getBlockJson(blockKey);
        mainWc = (WildCardFrameLayout) WildCardConstructor.constructLayer(getApplicationContext(), contentView, cloudJson, this);
        WildCardConstructor.applyRule(this, mainWc, data);
    }

    public void constructScollView(String blockKey){
        JSONObject cloudJson = WildCardConstructor.getInstance().getBlockJson(blockKey);
        mainWc = (WildCardFrameLayout) WildCardConstructor.constructLayer(getApplicationContext(), scrollView, cloudJson, this);
        WildCardConstructor.applyRule(this, mainWc, data);
    }

    public void constructListView(String screenName) throws Exception {
        listView.setVisibility(View.VISIBLE);
        listView.init(screenName, this);
        listView.setData(data);
    }

    public void blankHeader(){
        findViewById(R.id.header).setVisibility(View.GONE);
        FrameLayout.LayoutParams l = (FrameLayout.LayoutParams)findViewById(R.id.content).getLayoutParams();
        l.topMargin = 0;
    }

    public void bottomButton(String text, View.OnClickListener click){
        findViewById(R.id.bottomText).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.bottomText)).setText(text);
        findViewById(R.id.bottomText).setOnClickListener(click);
    }

    public void hideBottomButton(){
        findViewById(R.id.bottomText).setVisibility(View.GONE);
    }

    public void reloadBlock(){
        WildCardConstructor.applyRule(this, mainWc, data);
    }

    public void constructRightButton(int drawable_id, View.OnClickListener onClick){
        ((ImageView)findViewById(R.id.right)).setVisibility(View.VISIBLE);
        ((ImageView)findViewById(R.id.right)).setImageResource(drawable_id);
        findViewById(R.id.right).setOnClickListener(onClick);
    }
    public void removeRightButton(){
        ((ImageView)findViewById(R.id.right)).setVisibility(View.GONE);
    }

    @Override
    public boolean onInstanceCustomAction(Context context, WildCardMeta meta, String function, List<String> args, View triggerView) {
        return false;
    }






    public interface PermissionCallback  {
        void onComplete(boolean sucdess);
    }
    PermissionCallback callback;
    public static final int REQUEST_PERMISSIONS = 5123;
    public boolean requestPermission(PermissionCallback callback){
        int sdkVersion = Build.VERSION.SDK_INT;
        if(sdkVersion >= Build.VERSION_CODES.M) {
            this.callback = callback;
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);

            }else {
                this.callback.onComplete(true);
            }
        }else{

            return true;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (REQUEST_PERMISSIONS == requestCode) {
            if (grantResults.length >= 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                if(this.callback != null)
                    this.callback.onComplete(true);
            } else {
                this.callback.onComplete(false);
                showAlert("권한을 허용해주세요.");
            }
            return;
        }
    }
}
