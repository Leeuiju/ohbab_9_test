package com.example.ohbab_9_test.template;

import android.os.Bundle;
import android.view.View;

import com.example.ohbab_9_test.R;
import com.example.ohbab_9_test.template.core.App;

import org.json.JSONObject;

public class MainActivity extends SubActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.back).setVisibility(View.GONE);
        setTitle("프로젝트 목록");
        showIndicator();
        App.getInstance().request("/front/api/project", null, new App.HttpCallback() {
            @Override
            public void onComplete(boolean success, JSONObject response) {
                try{
                    hideIndicator();
                    data.put("list", response.optJSONArray("list"));
                    constructContent("1605324337776");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
