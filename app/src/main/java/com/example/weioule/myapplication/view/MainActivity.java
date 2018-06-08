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

//    private String url = "https://blog.csdn.net/qq_34584049/article/details/78280815";
//    private String url = "https://www.12306.cn/";
    private String url = "https://blog.csdn.net/wol965/article/details/80537920";
    private String url = "https://pan.baidu.com/s/1uY-n8wyhm4eTSdJ4dQqnmw?qq-pf-to=pcqq.discussion";
    private String url = "http://xiazai.zol.com.cn/";

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
