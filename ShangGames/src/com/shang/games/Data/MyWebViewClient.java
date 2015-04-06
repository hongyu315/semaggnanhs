package com.shang.games.Data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.shang.games.Interface.onWebviewLoadFinish;
import com.shang.games.Utils.LogUtil;

import android.content.Context;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by rain on 15/3/16.
 */
public class MyWebViewClient extends WebViewClient {
    Context ctx ;
    onWebviewLoadFinish onLoadFinish;
    Handler handler ;
    WebView mWebView ;

    public MyWebViewClient(Context context, onWebviewLoadFinish finish){
        this.ctx = context;
        this.onLoadFinish = finish;
        handler = new MyHandler();
    }

    public class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            LogUtil.e("msg = " );

            if (mWebView != null) mWebView.loadUrl(webUrl);
        }
    }

    String webUrl;
    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
        webUrl = url;
        mWebView = view;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (url.equals("")) return;
                    URL pageUrl = new URL(url);
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) pageUrl.openConnection();
                        urlConnection.setInstanceFollowRedirects(false);
                        urlConnection.connect();
                        int code = urlConnection.getResponseCode();
                        if (code == 302) {
                            handler.sendEmptyMessage(0);
                        }else {
                            onLoadFinish.launchActivity(url);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        LogUtil.e(error.toString());
        handler.proceed();
        onLoadFinish.isPass(false);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        onLoadFinish.isPass(true);
        view.getSettings().setBlockNetworkImage(false);
        CookieSyncManager.getInstance().sync();
        LogUtil.e("on page finish" + url);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        onLoadFinish.isPass(false);
        LogUtil.e("fail = " + failingUrl + " des " +  description);
    }

//    public interface onLoadFinish{
//        public void isPass(boolean isPass);
//    }
}