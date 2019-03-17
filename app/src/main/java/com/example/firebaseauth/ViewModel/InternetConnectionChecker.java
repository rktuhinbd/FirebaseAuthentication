package com.example.firebaseauth.ViewModel;

import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Md. Rejaul Karim on 3/8/2019.
 */
public class InternetConnectionChecker {

    Context context;

    public InternetConnectionChecker(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                if (info.getState() == NetworkInfo.State.CONNECTED || info.isConnectedOrConnecting()) {
                    return true;
                }
            }
        }
        return false;
    }
}