package com.shang.games.Data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.shang.games.Interface.onWebviewLoadFinish;
import com.shang.games.Utils.GlobelData;
import com.shang.games.Utils.LogUtil;
import com.shang.games.Utils.PrefUtil;
import com.shang.games.Utils.Utils;

import android.content.Context;
import android.net.http.SslError;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

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
        LogUtil.e("url = " + url);
        if (Utils.isOnline(ctx)) {
        	if (view.getVisibility() == View.GONE) {
        		view.setVisibility(View.VISIBLE);
			}
		}else{
			Toast.makeText(ctx, "网络连接不可用，请稍后重试", Toast.LENGTH_SHORT).show();
		}

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
                        LogUtil.e("code = " + code);
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
        LogUtil.e("111");
        onLoadFinish.isPass(false);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (url.contains("data:text/html,chromewebdata")) {
        	onLoadFinish.isPass(false);
        }else{
        	onLoadFinish.isPass(true);
        }
        view.getSettings().setBlockNetworkImage(false);
        
        CookieSyncManager.createInstance(ctx);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        String cookies = CookieManager.getInstance().getCookie(url);
        CookieSyncManager.getInstance().sync();
        PrefUtil pref = new PrefUtil(ctx);
        if (cookies.contains("uinfo=")) {//user authcookie
        	cookieManager.setCookie(url, cookies);//cookies是在HttpClient中获得的cookie
        	GlobelData.CacheUrl = url;
        	pref.saveString(PrefUtil.CacheUrl, url);
        	pref.saveString(PrefUtil.Cache, cookies);
//			String cookieStr = cookies.substring(cookies.indexOf("uinfo=") + 6, cookies.length()).split(";")[0];
		}
        LogUtil.e("cookies = " + cookies);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        LogUtil.e("333");
        onLoadFinish.isPass(false);
        view.setVisibility(View.GONE);
        LogUtil.e("fail = " + failingUrl + " des " +  description);
    }

//    public interface onLoadFinish{
//        public void isPass(boolean isPass);
//    }
}