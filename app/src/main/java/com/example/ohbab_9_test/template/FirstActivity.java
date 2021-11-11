package com.example.ohbab_9_test.template;

import android.net.Uri;
import android.os.Bundle;

import com.example.ohbab_9_test.R;
import com.example.ohbab_9_test.template.core.Config;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import kr.co.july.devil.WildCardConstructor;
import kr.co.july.devil.core.DevilSdk;
import kr.co.july.devil.core.javascript.Jevil;
import kr.co.july.devil.core.javascript.JevilInstance;
import kr.co.july.devil.core.link.DevilLink;
import kr.co.july.devil.extra.FlexScreen;

public class FirstActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        checkDeepLink();

        FlexScreen.init(this);
        DevilSdk.setFirstActivity(FirstActivity.class);
        WildCardConstructor.getInstance(Config.PROJECT_ID).initWithLocal(this, Config.PROJECT_ID);
        closeIntro();

     /*   WildCardConstructor.getInstance(Config.PROJECT_ID).initWithOnLine(this, Config.PROJECT_ID, "0.0.1", new WildCardConstructor.InitComplete() {
            @Override
            public void onComplete(boolean success) {
                if(success) {
                    closeIntro();
                } else {
                    showAlertWithFinish("앱을 초기화하지 못했습니다.인터넷 연결을 확인해주세요.");
                }
            }
        });*/
    }

    void closeIntro()
    {
        findViewById(R.id.view).postDelayed(new Runnable() {
            @Override
            public void run() {
                next();
            }
        }, 500);
    }

    void next() {
        FlexScreen.getInstance().setScreenHeightView(findViewById(R.id.view));
        if(Jevil.get("x-access-token") != null)
            Jevil.go("main", null);
        else
            Jevil.go("login", null);

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        JevilInstance.getCurrentInstance().setActivity(this);
    }

    public void checkDeepLink(){
        if(!DevilLink.getInstance().parseIntent(this, getIntent()))
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
                                DevilLink.getInstance().setReserveUrl(getApplicationContext(), deepLink.toString());
                            }
                        }});
    }
}
