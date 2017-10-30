package com.example.popularmovies;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.RequiresPermission;

/**
 * Created by karthik on 29/10/17.
 */

public class InternetUtils {

    private static ConnectivityManager connectivityManager;

    private static ConnectivityManager getConnectivityManager(Context context) {
        if (connectivityManager == null)
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager;
    }

    /**
     * Let's know whether internet is connected or not.
     * @param context
     * @return true if it is connected to internet, false otherwise
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean isConnected(Context context) {
        NetworkInfo networkInfo = getConnectivityManager(context).getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected());
    }

    /**
     * Return the type of the network
     * @param context
     * @return type of the network like "WIFI" or "MOBILE" if is connected. If it not connected then this method will return null
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static String getConnectionType(Context context) {
        return isConnected(context) ? getConnectivityManager(context).getActiveNetworkInfo().getTypeName() : null;
    }
}