package in.dsij.pas.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import in.dsij.pas.MyApplication;
import in.dsij.pas.constants.C;


public class NetworkReceiver extends BroadcastReceiver {
    public NetworkReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            //connected
            MyApplication.setConnectionStatus(C.net.NETWORK_AVAILABLE);
        } else if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            MyApplication.setConnectionStatus(C.net.NETWORK_CONNECTING);
        } else {
            //not connected
            MyApplication.setConnectionStatus(C.net.NETWORK_DISCONNECTED);
        }
    }
}
