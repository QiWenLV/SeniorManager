package com.zqw.appmanger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 启文 on 2017/2/17.
 */
public class AppDetailed extends Activity implements View.OnClickListener {

    Button openApp;
    TextView tv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_detailed);

        openApp = (Button) findViewById(R.id.btn1);

       // AppInfo APP = (AppInfo) getIntent().getSerializableExtra("key");


        openApp.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Toast.makeText(AppDetailed.this,"lll",Toast.LENGTH_SHORT).show();
    }
}
