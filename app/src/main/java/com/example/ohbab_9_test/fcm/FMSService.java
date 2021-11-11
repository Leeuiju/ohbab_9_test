package com.example.ohbab_9_test.fcm;

import android.content.Intent;
import android.util.Log;

import com.example.ohbab_9_test.R;
import com.example.ohbab_9_test.template.core.App;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import kr.co.july.devil.core.link.DevilLink;


public class FMSService extends FirebaseMessagingService {

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("Service", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Service", "onDestroy");
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        App.getInstance().savePushToken(s);
        App.getInstance().sendPush();
    }

    public static final String TAG = "FMSService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            DevilLink.getInstance().remotePush(getApplicationContext(),
                    R.mipmap.ic_launcher_round,
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(),
                    remoteMessage.getNotification().getImageUrl(),
                    remoteMessage.getData()
            );
        }
    }

}