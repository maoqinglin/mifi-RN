package com.ireadygo.app.wifiapproject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by lin.mq on 2016/6/30.
 */
public class TestActivity extends Activity {
    private Button mOpenBtn,mCloseBtn,mSetBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main2);
        init();
        WifiApController.getInstance(this).registerNetwork();
    }

    private void init(){
        mOpenBtn = (Button)findViewById(R.id.openAp);
        mCloseBtn = (Button)findViewById(R.id.closeAp);
        mSetBtn = (Button)findViewById(R.id.setAp);
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
                WifiApController.getInstance(TestActivity.this).setAp("ireadygo","12345688",0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiApController.getInstance(this).unregisterNetwork();
    }
}
