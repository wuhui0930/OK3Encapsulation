package com.personal.ok3encapsulation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.personal.ok3encapsulation.network.OkHttpUtils;

public class MainActivity extends AppCompatActivity {


    private OkHttpUtils okHttpUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpUtils=OkHttpUtils.getInstance();

        okHttpUtils.asynJsonUrl("url", new OkHttpUtils.OutPutJson() {
            @Override
            public void onResponse(String result) {
                //....
            }
        });
    }
}
