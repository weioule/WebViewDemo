package com.example.weioule.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.weioule.myapplication.controller.ProgressBarController;
import com.example.weioule.myapplication.R;
import com.example.weioule.myapplication.widget.H5NoNetView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author weioule
 * @Create on 2018.6.7
 */

public class WebViewH5Activity extends AppCompatActivity {

    private String blackUrl = "about:blank";//浏览器空白页
    protected RelativeLayout webviewcontainer;
    private HideBarTimeTask hideBarTimeTask;
    private Timer hideProgressBarTimer;
    private H5NoNetView mH5NoNetView;
    private ProgressBar progressbar;
    protected WebView webView;
    protected String rootUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view_h5_activity);

        rootUrl = getIntent().getExtras().getString("URL");
        if (rootUrl != null) {
            rootUrl = rootUrl.trim();
        }

        initView();
        loadWebView();
    }

    protected void initView() {
        webviewcontainer = (RelativeLayout) findViewById(R.id.webviewcontainer);
        webView = (WebView) findViewById(R.id.detail_webview);
        WebSettings webseting = webView.getSettings();
        webseting.setSupportZoom(true);
        webseting.setUseWideViewPort(true);
        webseting.setJavaScriptEnabled(true);
        webseting.setBuiltInZoomControls(true);
        webseting.setLoadWithOverviewMode(true);
        webseting.setDisplayZoomControls(false);
        webseting.setJavaScriptCanOpenWindowsAutomatically(false);

        // 设置Web视图
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setDownloadListener(new DownloadListener());

        progressbar = (ProgressBar) findViewById(R.id.progressBar);
        mH5NoNetView = (H5NoNetView) findViewById(R.id.nonetview);
        mH5NoNetView.setRefreshListener(new H5NoNetView.HtmlReloadListener() {
            @Override
            public void triggerRefresh() {
                if (netIsAvailable()) {
                    mH5NoNetView.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
                loadWebView();
            }
        });
    }

    protected void loadWebView() {
        if (rootUrl.startsWith("https://") || rootUrl.startsWith("http://")) {
            //同步Cookies
            CookieSyncManager.createInstance(this);
            CookieSyncManager.getInstance().sync();
            webView.loadUrl(rootUrl);
        } else {
            webView.loadData(rootUrl, "text/html", "UTF-8");
        }
    }

    public boolean netIsAvailable() {
        ConnectivityManager cwjManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cwjManager.getActiveNetworkInfo() != null && cwjManager.getActiveNetworkInfo().isAvailable();
    }

    private void runTimer(int delay) {
        stopTimer();
        hideProgressBarTimer = new Timer(true);
        hideBarTimeTask = new HideBarTimeTask();
        hideProgressBarTimer.schedule(hideBarTimeTask, delay);
    }

    private void stopTimer() {
        if (hideBarTimeTask != null) {
            hideBarTimeTask.cancel();
            hideBarTimeTask = null;
        }

        if (hideProgressBarTimer != null) {
            hideProgressBarTimer.cancel();
            hideProgressBarTimer.purge();
            hideProgressBarTimer = null;
        }
    }

    private ProgressBarController progressBarController = new ProgressBarController(new ProgressBarController.ControllerListener() {

        @Override
        public void stop() {
            runTimer(500);
        }

        @Override
        public void setProgress(int progress) {
            progressbar.setProgress(progress);
        }

        @Override
        public void start() {
            if (progressbar.getVisibility() == View.GONE) {
                progressbar.setVisibility(View.VISIBLE);
            }
            stopTimer();
        }

    });

    class HideBarTimeTask extends TimerTask {
        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 10000;
            webviewHandler.sendMessage(msg);
        }
    }

    private Handler webviewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10000) {
                progressbar.setVisibility(View.GONE);
                progressbar.setProgress(0);
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView != null && webView.canGoBack() && !checkBackUrl(blackUrl)) {
                webView.goBack();
            } else {
                webView.stopLoading();
                finish();
            }

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean checkBackUrl(String url) {
        WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();

        String backUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex() - 1).getUrl();
        if (backUrl != null && backUrl.equalsIgnoreCase(url)) {
            return true;
        }
        return false;
    }


    private class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            //开始加载，显示进度
            progressBarController.preloading();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);

            if (failingUrl.equalsIgnoreCase(rootUrl) == false) {
                return;
            }

            webView.loadUrl(blackUrl);
            //只有加载完毕才应该调用clearHistory()
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.clearHistory();
                    mH5NoNetView.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            }, 500);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //忽略SSL证书错误检测,使用SslErrorHandler.proceed()来继续加载
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            //通知程序当前页面加载进度
            progressBarController.setCurrentValue(newProgress);
        }
    }

    private class DownloadListener implements android.webkit.DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            //需要Webview开启下载监听,否则点击下载连接，没有反应
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }
}
