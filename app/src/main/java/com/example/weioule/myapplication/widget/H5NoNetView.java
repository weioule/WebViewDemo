package com.example.weioule.myapplication.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.weioule.myapplication.R;


/**
 * @author weioule
 * @Create on 2018.6.7
 */

public class H5NoNetView extends RelativeLayout {

    private LinearLayout reloadhtml_root_view;
    private HtmlReloadListener mListener;
    private ImageView tryAgain;

    public H5NoNetView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.network_load_fail, this, true);
        tryAgain = (ImageView) findViewById(R.id.html_load_fail);
        reloadhtml_root_view = (LinearLayout) findViewById(R.id.reloadhtml_root_view);
        reloadhtml_root_view.setOnTouchListener(new OnTouchListener());
    }

    public void setRefreshListener(HtmlReloadListener listener) {
        mListener = listener;
    }

    private class OnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_UP) {
                mListener.triggerRefresh();
                tryAgain.setAlpha(100);
                tryAgain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tryAgain.setAlpha(255);
                    }
                }, 100);
            }
            return true;
        }
    }

    public interface HtmlReloadListener {
        void triggerRefresh();
    }

}
