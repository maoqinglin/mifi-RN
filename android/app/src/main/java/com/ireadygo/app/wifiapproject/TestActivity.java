package com.ireadygo.app.wifiapproject;

import android.app.Activity;
import android.net.wifi.HotspotClient;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

/**
 * Created by lin.mq on 2016/6/30.
 */
public class TestActivity extends Activity {
    private Button mOpenBtn, mCloseBtn, mSetBtn, mUserList,mUserArray;
    private LinearLayout mLayout;
    private static final String TAG = "TestActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main2);
        init();
        WifiApController.getInstance(this).registerNetwork();
    }

    private void init() {
        mOpenBtn = (Button) findViewById(R.id.openAp);
        mCloseBtn = (Button) findViewById(R.id.closeAp);
        mSetBtn = (Button) findViewById(R.id.setAp);
        mUserList = (Button) findViewById(R.id.userList);
        mLayout = (LinearLayout) findViewById(R.id.container);
        mUserArray = (Button) findViewById(R.id.userArray);
        mOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApController.getInstance(TestActivity.this).openWifiAp();
            }
        });
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApController.getInstance(TestActivity.this).closeWifiAp(TestActivity.this);
            }
        });
        mSetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiApController.getInstance(TestActivity.this).setAp("ireadygo", "12345688", 0);
            }
        });
        mUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<HotspotClient> clientList = WifiApController.getInstance(TestActivity.this).getUserList();

                if (clientList != null) {

                    for (int i = 0; i < clientList.size(); i++) {
                        Log.d(TAG, "deviceAddress = " + clientList.get(i).deviceAddress + " isBlocked = " + clientList.get(i).isBlocked);

                        HotspotClient user = clientList.get(i);
                        if(!TextUtils.isEmpty(user.deviceAddress)){
                            Button userBtn = new Button(TestActivity.this);
                            userBtn.setOnClickListener(new UserOnclickListener());
                            userBtn.setText(user.isBlocked?"解锁":"封锁");
                            userBtn.setTag(user);
                            mLayout.addView(userBtn);
                        }
                    }
                } else {
                    Log.d(TAG, "用户列表为空");
                }
            }
        });

        mUserArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               WritableArray array = WifiApController.getInstance(TestActivity.this).getUserArray();
                if(array != null && array.size() > 0){
                    for(int i = 0 ;i<array.size();i++){
                        ReadableMap map = array.getMap(i);
                        String mac = map.getString(WifiApController.USER_MAC_ADDRESS_KEY);
                        boolean block = map.getBoolean(WifiApController.USER_IS_BLOCKED_KEY);
                        Log.d(TAG,"mac = "+mac+" block = "+block);
                    }
                }
            }
        });
    }


    class UserOnclickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            HotspotClient user = (HotspotClient) v.getTag();
            if(user != null){
                WifiApController.getInstance(getApplicationContext()).handleUser(user.deviceAddress,user.isBlocked);
                ((Button)v).setText(!user.isBlocked?"解锁":"封锁");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiApController.getInstance(this).unregisterNetwork();
    }
}
