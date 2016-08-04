package com.ireadygo.app.wifiapproject;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lin.mq on 2016/6/27.
 */
public class WifiApModule extends ReactContextBaseJavaModule {

    private static final String TAG = "WifiApModule";
    private Context mContext = null;
    private static final String ADDRESS_KEY = "ADDRESS";
    private static final String BLOCK_KEY = "IS_BLOCKED";

    public WifiApModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        WifiApController.getInstance(reactContext).setReactContext(reactContext);
    }

    @Override
    public String getName() {
        return "WifiApModule";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(ADDRESS_KEY, WifiApController.USER_MAC_ADDRESS_KEY);
        constants.put(BLOCK_KEY, WifiApController.USER_IS_BLOCKED_KEY);
        return constants;
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
        WifiApController.getInstance(mContext).notifyWifiApChecked(true);
    }

    @ReactMethod
    public void closeWifiAp() {
        WifiApController.getInstance(mContext).closeWifiAp(mContext);
        WifiApController.getInstance(mContext).notifyWifiApChecked(false);
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
            configCallback.invoke(config.SSID, config.preSharedKey, WifiApController.getSecurityIndex(config));
        }
    }

    @ReactMethod
    public void saveWifiApConfig(String ssid, String passwd, int security, Callback saveCallback) {
        boolean isSuccess = WifiApController.getInstance(mContext).setAp(ssid, passwd, security);
        Log.d(TAG, "saveWifiApConfig isSuccess = " + isSuccess + "  passwd = " + passwd + "  security = " + security);
        saveCallback.invoke(isSuccess);
    }

    @ReactMethod
    public void getUserList(Callback callback) {
        WritableArray userList = WifiApController.getInstance(mContext).getUserArray();
        if (userList != null) {
            if (callback != null) {
                callback.invoke(userList);
            }
        }
    }

    @ReactMethod
    public void handleUser(String address, boolean isBlock, Callback callback) {
        if (!TextUtils.isEmpty(address)) {
            WifiApController controller = WifiApController.getInstance(mContext);
            boolean isSuccess = controller.handleUser(address, isBlock);
            if (callback != null && isSuccess) {
                callback.invoke(isSuccess);
            }
        }
    }

}
