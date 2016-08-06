package com.sharathp.blabber.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

public class NetworkUtils {

    public static boolean isOnline(final Context context) {
        return isNetworkAvailable(context) && isInternetAvailable();
    }

    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public static boolean isInternetAvailable() {
        final Runtime runtime = Runtime.getRuntime();
        try {
            final Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            final int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
