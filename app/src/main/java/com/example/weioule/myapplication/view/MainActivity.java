package com.example.weioule.myapplication.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.weioule.myapplication.R;

/**
 * @author weioule
 * @Create on 2018.6.7
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String url = "https://github.com/weioule/WebViewDemo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, WebViewH5Activity.class);
        intent.putExtra("URL", url);
        startActivity(intent);
    }
}
