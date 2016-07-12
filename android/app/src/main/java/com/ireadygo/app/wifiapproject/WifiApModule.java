package com.ireadygo.app.wifiapproject;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
/**
 * Created by lin.mq on 2016/6/27.
 */
public class WifiApModule extends ReactContextBaseJavaModule {

    private static final String TAG = "WifiApModule";
    private Context mContext = null;

    public WifiApModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        WifiApController.getInstance(reactContext).setReactContext(reactContext);
    }

    @Override
    public String getName() {
        return "WifiApModule";
    }

    @ReactMethod
    public void getWifiApState(Callback stateCallback) {
        boolean state = WifiApController.isWifiApEnabled(mContext);
        stateCallback.invoke(state);
        Log.d(TAG, "getWifiApState state = " + state);
    }

    @ReactMethod
    public void openWifiAp() {
        WifiApController.getInstance(mContext).openWifiAp();
    }

    @ReactMethod
    public void closeWifiAp() {
        WifiApController.getInstance(mContext).closeWifiAp(mContext);
    }

    @ReactMethod
    public void getWifiApName(Callback nameCallback) {
        WifiConfiguration config = WifiApController.getInstance(mContext).getWifiApConfig();
        if (config != null) {
            String name = config.SSID;
            nameCallback.invoke(name);
        }
    }

    @ReactMethod
    public void getWifiApConfig(Callback configCallback) {
        WifiConfiguration config = WifiApController.getInstance(mContext).getWifiApConfig();
        if (config != null) {
            Toast.makeText(mContext, "getWifiApConfig ssid = " + config.SSID + " preSharedKey = " + config.preSharedKey + " getSecurityIndex = " + WifiApController.getSecurityIndex(config), Toast.LENGTH_LONG).show();
            configCallback.invoke(config.SSID, config.preSharedKey, WifiApController.getSecurityIndex(config));
        }
    }

    @ReactMethod
    public void saveWifiApConfig(String ssid,String passwd,int security,Callback saveCallback) {
        boolean isSuccess = WifiApController.getInstance(mContext).setAp(ssid,passwd,security);
        Log.d(TAG,"saveWifiApConfig isSuccess = "+isSuccess+"  passwd = "+passwd+"  security = "+security);
        saveCallback.invoke(isSuccess);
    }
}
