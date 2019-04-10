package in.dsij.pas;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import android.support.multidex.MultiDex;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.List;
import java.util.UUID;

import in.dsij.pas.constants.C;
import in.dsij.pas.util.NetworkReceiver;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class MyApplication extends Application {

    private static final String LOG_TAG = "MyApplication";
    private static boolean connected;
    private static MyApplication me;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        MultiDex.install(getApplicationContext());
        super.onCreate();
        me = this ;
        // Initialize Realm
        Realm.init(getApplicationContext());

        // Realm configuration
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        /*Fresco.initialize(getApplicationContext());*/
        // Initialize Stetho
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this)
                                .withLimit(1000)
                                .build())
                        .build());
        getDeviceDetails();
        Fresco.initialize(getApplicationContext());
//check if network available
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //connected
            connected = true;
        } else {
            //not connected
            connected = false;
        }
        String uniqueID = UUID.randomUUID().toString();
        //register receiver for runtime network change
        registerReceiver(new NetworkReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }
    public static MyApplication getInstance() {
        return me;
    }
    private void getDeviceDetails() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            C.device.VERSION_NAME = pInfo.versionName;
            C.device.VERSION_CODE = String.valueOf(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String str = Build.MODEL;
        C.device.DEVICE_SERIAL = Settings.Secure.getString(
                getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        C.device.DEVICE_SERIAL = androidId;

    }

    public static boolean isConnected() {
        return connected;
    }

    public static void setConnectionStatus(int status) {

        Log.d("Network", "Network state change");

        if (status == C.net.NETWORK_AVAILABLE) {
            connected = true;
            Log.d(LOG_TAG, "Network Available");
        } else if (status == C.net.NETWORK_CONNECTING) {
            Log.d(LOG_TAG, "Network Connecting");
        } else {
            connected = false;
            Log.d(LOG_TAG, "Network Not Available");
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

}
