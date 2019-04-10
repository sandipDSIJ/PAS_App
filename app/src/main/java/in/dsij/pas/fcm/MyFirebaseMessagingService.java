

package in.dsij.pas.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import in.dsij.pas.activity.MainActivity;
import io.realm.Realm;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }
    private void handleDataMessage(JSONObject json) {
        Log.e(LOG_TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(LOG_TAG, "title: " + title);
            Log.e(LOG_TAG, "message: " + message);
            Log.e(LOG_TAG, "isBackground: " + isBackground);
            Log.e(LOG_TAG, "payload: " + payload.toString());
            Log.e(LOG_TAG, "imageUrl: " + imageUrl);
            Log.e(LOG_TAG, "timestamp: " + timestamp);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(LOG_TAG, "Exception: " + e.getMessage());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(LOG_TAG, "From: " + remoteMessage.getFrom());

        /*if (remoteMessage.getData().size() > 0) {

            Map<String, String> message = remoteMessage.getData();
            Intent intent;
            String messageString = message.get("message");
            Log.d(LOG_TAG, "Message Content: " + messageString);
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            CloudMessage cloudMessage = new Gson().fromJson(messageString, CloudMessage.class);

            Log.d(LOG_TAG, "Message data payload: " + remoteMessage.getData());

            int cloudMessageType = 5;
            try {
                cloudMessageType = cloudMessage.getType();
            } catch (Exception e) {
                cloudMessageType = 5;
                Log.e(LOG_TAG, e.toString());
            }

            switch (cloudMessageType) {
                case 1:
                    insertChat(cloudMessage);
                    intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    if (!MyApplication.isAppIsInBackground(getApplicationContext())) {
                        // app is in foreground, broadcast the push message
                        clearNotifications(getApplicationContext());
                        Intent pushNotification = new Intent(C.Config.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", cloudMessage.getNotificationMessage());
                        pushNotification.putExtra("cloudMessageType", cloudMessageType);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                    } else {
                        clearNotifications(getApplicationContext());
                    }
                    break;
                case 2:
                    intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sendNotification(cloudMessage.getNotificationTitle(), cloudMessage.getNotificationMessage(), intent, cloudMessageType);
                    break;
                case 3:
                    //insertNotificationMessage(cloudMessage);
                    intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    sendNotification(cloudMessage.getNotificationTitle(), cloudMessage.getNotificationMessage(), intent, cloudMessageType);
                    break;
                case 4:
                    intent = new Intent(this, WebViewActivity.class);
                    Intent webIntent =WebViewActivity.getIntent(this.getApplicationContext(), "MY OFFERS", C.url.API_BASE_URL+cloudMessage.getpageURL());
                    intent.addFlags(webIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    showBigNotification(cloudMessage.getImage(),cloudMessage.getNotificationTitle(),cloudMessage.getNotificationMessage(),webIntent);
                    break;
                case 5:
                    try {
                        JSONObject json = new JSONObject(remoteMessage.getData().toString());
                        handleDataMessage(json);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG,e.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }*/
        if (remoteMessage.getNotification() != null) {
        }

    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void insertNotificationMessage(CloudMessage cloudMessage) {

        Realm realm;
        realm = Realm.getDefaultInstance();
        try {

            realm.beginTransaction();

           /* DbChatHistory dbChatHistory = realm.createObject(DbChatHistory.class, cloudMessage.getCommentId())
                    .setName(cloudMessage.getName())
                    .getCreatedDate(cloudMessage.getTime())
                    .setComment(cloudMessage.getNotificationMessage());

            realm.commitTransaction();*/
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }



    private void handleNow() {
        Log.d(LOG_TAG, "Short lived task is done.");
    }





    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
