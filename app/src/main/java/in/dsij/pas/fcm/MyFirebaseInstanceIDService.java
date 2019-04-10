

package in.dsij.pas.fcm;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseInstanceIDService extends FirebaseMessagingService {

    private static final String LOG_TAG = "MyFirebaseIIDService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
    }

    public void sendRegistrationToServer(String token) {
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);


    }
}
