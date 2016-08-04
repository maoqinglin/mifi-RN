package com.ireadygo.app.wifiapproject;

/**
 * Created by lin.mq on 2016/6/23.
 */

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.HotspotClient;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * 创建热点
 */
public class WifiApController {
    public static final String TAG = "WifiApController";

    private WifiManager mWifiManager = null;
    private Context mContext = null;
    private ReactContext mReactContext = null;

    private static final String METHOD_SET_WIFI_AP_ENABLED = "setWifiApEnabled";
    private static final String METHOD_GET_WIFI_AP_CONFIG = "getWifiApConfiguration";
    private static final String METHOD_IS_WIFI_AP_ENABLED = "isWifiApEnabled";
    private static final String METHOD_SET_WIFI_AP_CONFIG = "setWifiApConfiguration";


    private static final String METHOD_GET_HOTSPOT_CLIENTS = "getHotspotClients";
    private static final String METHOD_UNLOCK_CLIENT = "unblockClient";
    private static final String METHOD_BLOCK_CLIENT = "blockClient";
    public static final String USER_MAC_ADDRESS_KEY = "macAddress";
    public static final String USER_IS_BLOCKED_KEY = "isBlocked";


    private static WifiApController mInstance;
    private MyTimerCheck mTimerCheck;
    public static final boolean DEBUG = false;
    private static final String WIFI_AP_CHECKED_ACTION = "com.android.intent.ireadygo.app.wifiap.enable";

    private WifiApController(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiApController getInstance(Context context) {
        if (mInstance == null) {
            synchronized (WifiApController.class) {
                if (mInstance == null) {
                    mInstance = new WifiApController(context);
                }
            }

        }
        return mInstance;
    }

    public boolean setAp(String ssid, String passwd, int security) {
        if (TextUtils.isEmpty(ssid)) {
            return false;
        }
        if (security != WifiConfiguration.KeyMgmt.NONE && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
            Toast.makeText(mContext, "密码至少为8位", Toast.LENGTH_LONG).show();
            return false;
        }
        return saveAp(ssid, passwd, security);
    }

    private boolean saveAp(String ssid, String passwd, int security) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        if (security == WifiConfiguration.KeyMgmt.WPA_PSK) {//如果加密模式为WPA PSK
            config.preSharedKey = passwd;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        } else if (security == HideWifiApField.WPA2_PSK) {
            config.preSharedKey = passwd;
            config.allowedKeyManagement.set(HideWifiApField.WPA2_PSK);
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        boolean enable = isWifiApEnabled(mContext);

        /*if (enable) {
            mWifiManager.setWifiApEnabled(null, false);
            mWifiManager.setWifiApEnabled(mWifiConfig, true);
        } else {
            mWifiManager.setWifiApConfiguration(mWifiConfig);
        }*/
        try {
            Method method = WifiManager.class.getMethod(METHOD_SET_WIFI_AP_ENABLED, WifiConfiguration.class, Boolean.TYPE);
            if (enable) {
                method.invoke(mWifiManager, null, false);
                return (Boolean) method.invoke(mWifiManager, config, true);
            } else {
                Method method1 = WifiManager.class.getMethod(METHOD_SET_WIFI_AP_CONFIG, WifiConfiguration.class);
                return (Boolean) method1.invoke(mWifiManager, config);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Log.d(TAG, "e = " + e.getMessage());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public WifiConfiguration getWifiApConfig() {
        WifiConfiguration configuration = null;
        try {
            Method method = WifiManager.class.getMethod(METHOD_GET_WIFI_AP_CONFIG);
            method.setAccessible(true);
            configuration = (WifiConfiguration) method.invoke(mWifiManager);
        } catch (NoSuchMethodException e) {

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return configuration;
    }

    public void openWifiAp() {
        try {
            //如果wifi没有关闭，先关闭
            checkWifiState(true);

            Method openAp = mWifiManager.getClass().getMethod(METHOD_SET_WIFI_AP_ENABLED,
                    WifiConfiguration.class, boolean.class);
            openAp.setAccessible(true);
            boolean isSucces = (Boolean) openAp.invoke(mWifiManager, null, true);
            Log.d(TAG, "openWifiAp isSucces = " + isSucces);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "openWifiAp " + e.getMessage());
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "openWifiAp " + e.getMessage());
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "openWifiAp " + e.getMessage());
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "openWifiAp " + e.getMessage());
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "openWifiAp " + e.getMessage());
        }
        checkTask();
    }

    public void closeWifiAp(Context context) {
        removeTimeTask();
        WifiManager wifiManager = getWifiManager(context);
        if (isWifiApEnabled(context)) {
            try {
                Method setAp = wifiManager.getClass().getMethod(METHOD_SET_WIFI_AP_ENABLED, WifiConfiguration.class, boolean.class);
                setAp.setAccessible(true);
                setAp.invoke(wifiManager, null, false);
                checkWifiState(false);//只能在关闭热点后，打开wifi，否则打开失败
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void checkWifiState(boolean wifiApEnable) {
        final ContentResolver cr = mContext.getContentResolver();
        int wifiState = mWifiManager.getWifiState();
        if (wifiApEnable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
            mWifiManager.setWifiEnabled(false);
            if (!DEBUG) {
                Settings.Global.putInt(cr, HideWifiApField.WIFI_SAVED_STATE, 1);// 需要系统权限
            }
        }
        if (!wifiApEnable) {
            int wifiSavedState = 0;
            try {
                wifiSavedState = Settings.Global.getInt(cr, HideWifiApField.WIFI_SAVED_STATE);
            } catch (Settings.SettingNotFoundException e) {
                Log.e(TAG, "SettingNotFoundException");
            }
            if (wifiSavedState == 1) {
                mWifiManager.setWifiEnabled(true);
                if (!DEBUG) {
                    Settings.Global.putInt(cr, HideWifiApField.WIFI_SAVED_STATE, 0);
                }

            }
        }
    }

    public static boolean isWifiApEnabled(Context context) {
        WifiManager manager = getWifiManager(context);
        try {
            Method method = manager.getClass().getMethod(METHOD_IS_WIFI_AP_ENABLED);
            method.setAccessible(true);
            return (Boolean) method.invoke(manager);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<HotspotClient> getUserList() {

        WifiManager manager = getWifiManager(mContext);
        try {
            Method method = manager.getClass().getMethod(METHOD_GET_HOTSPOT_CLIENTS);
            method.setAccessible(true);
            return (List<HotspotClient>) method.invoke(manager);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public WritableArray getUserArray() {
        List<HotspotClient> userList = getUserList();
        WritableArray array = Arguments.createArray();
        if (userList != null && !userList.isEmpty()) {
            for (int i = 0; i < userList.size(); i++) {
                HotspotClient user = userList.get(i);
                WritableMap map = Arguments.createMap();
                map.putBoolean(USER_IS_BLOCKED_KEY, user.isBlocked);
                map.putString(USER_MAC_ADDRESS_KEY, user.deviceAddress);
                array.pushMap(map);
            }
        }
        return array;
    }

    public boolean handleUser(String address, boolean isBlock) {
        boolean success = false;
        if (!TextUtils.isEmpty(address)) {
            List<HotspotClient> userList = getUserList();
            if (userList != null) {
                for (int i = 0; i < userList.size(); i++) {
                    HotspotClient user = userList.get(i);
                    if (address.equals(user.deviceAddress)) {
                        if (isBlock) {
                            success = unBlockUser(user);
                        } else {
                            success = blockUser(user);
                        }
                        if (success) {
                            Log.d(TAG, "handleUser 设置成功 address = " + address);
                            handleWifiApClientsChanged();
                            break;
                        }
                    }
                }
            }
        }
        return success;
    }

    public boolean blockUser(HotspotClient client) {
        WifiManager manager = getWifiManager(mContext);
        try {
            Method method = manager.getClass().getMethod(METHOD_BLOCK_CLIENT, HotspotClient.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(manager, client);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch blockf
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean unBlockUser(HotspotClient client) {
        WifiManager manager = getWifiManager(mContext);
        try {
            Method method = manager.getClass().getMethod(METHOD_UNLOCK_CLIENT, HotspotClient.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(manager, client);
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch blockf
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void checkTask() {
        removeTimeTask();

        mTimerCheck = new MyTimerCheck() {

            @Override
            public void doTimerCheckWork() {
                // TODO Auto-generated method stub

                if (isWifiApEnabled(mContext)) {
                    Log.v(TAG, "Wifi enabled success!");
                    this.exit();
                } else {
                    Log.v(TAG, "Wifi enabled failed!");
                }
            }

            @Override
            public void doTimeOutWork() {
                // TODO Auto-generated method stub
                this.exit();
            }
        };
        mTimerCheck.start(10, 1000);
    }

    private void removeTimeTask() {
        if (mTimerCheck != null) {
            mTimerCheck.exit();
            mTimerCheck = null;
        }
    }

    private static WifiManager getWifiManager(Context context) {
        return (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    private static class HideWifiApField {
        public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
        public static final String WIFI_HOTSPOT_CLIENTS_CHANGED_ACTION = "android.net.wifi.WIFI_HOTSPOT_CLIENTS_CHANGED";
        public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
        public static final int WIFI_AP_STATE_DISABLING = 10;
        public static final int WIFI_AP_STATE_DISABLED = 11;
        public static final int WIFI_AP_STATE_ENABLING = 12;
        public static final int WIFI_AP_STATE_ENABLED = 13;
        public static final int WIFI_AP_STATE_FAILED = 14;
        public static final int WPA2_PSK = 4;
        public static final String WIFI_SAVED_STATE = "wifi_saved_state";
        private static final String DEVICE_ADDRESS = "deviceAddress";
        private static final String IS_BLOCKED = "isBlocked";
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (HideWifiApField.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
                handleWifiApStateChanged(intent.getIntExtra(
                        HideWifiApField.EXTRA_WIFI_AP_STATE, HideWifiApField.WIFI_AP_STATE_FAILED));
            } else if (HideWifiApField.WIFI_HOTSPOT_CLIENTS_CHANGED_ACTION.equals(action)) {
                handleWifiApClientsChanged();
            }
        }
    };

    /**
     * 将状态返回给js
     *
     * @param state
     */
    private void handleWifiApStateChanged(int state) {
        WritableMap params = Arguments.createMap();
        switch (state) {
            case HideWifiApField.WIFI_AP_STATE_ENABLING:
                break;
            case HideWifiApField.WIFI_AP_STATE_ENABLED:
                params.putBoolean("state", true);
                if (mReactContext != null) {
                    sendEvent(mReactContext, "wifiState", params);
                }
                break;
            case HideWifiApField.WIFI_AP_STATE_DISABLING:
                break;
            case HideWifiApField.WIFI_AP_STATE_DISABLED:
                params.putBoolean("state", false);
                if (mReactContext != null) {
                    sendEvent(mReactContext, "wifiState", params);
                }
                break;
            default:
                break;
        }
    }

    public void handleWifiApClientsChanged() {
        WritableMap params = Arguments.createMap();
//        params.putBoolean("state", false);
        if (mReactContext != null) {
            sendEvent(mReactContext, "updateUserList", params);
        }
    }

    public void registerNetwork() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(HideWifiApField.WIFI_AP_STATE_CHANGED_ACTION);
        filter.addAction(HideWifiApField.WIFI_HOTSPOT_CLIENTS_CHANGED_ACTION);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterNetwork() {
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * 获取加密方式
     *
     * @param config
     * @return
     */
    public static int getSecurityIndex(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return WifiConfiguration.KeyMgmt.WPA_PSK;
        } else if (config.allowedKeyManagement.get(HideWifiApField.WPA2_PSK)) {
            return HideWifiApField.WPA2_PSK;
        }
        return WifiConfiguration.KeyMgmt.NONE;
    }

    public void setReactContext(ReactContext reactContext) {
        mReactContext = reactContext;
    }

    private void sendEvent(ReactContext reactContext,
                           String eventName,
                           @Nullable WritableMap params) {
        try {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        } catch (RuntimeException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void notifyWifiApChecked(boolean isChecked){
        Intent intent = new Intent(WIFI_AP_CHECKED_ACTION);
        intent.putExtra("checked", isChecked);
        mContext.sendBroadcast(intent);
    }
}
