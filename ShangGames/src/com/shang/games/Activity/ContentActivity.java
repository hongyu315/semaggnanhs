package com.shang.games.Activity;

import java.io.File;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shang.games.R;
import com.shang.games.Data.Globle;
import com.shang.games.Data.MyWebViewClient;
import com.shang.games.Interface.JSFinish;
import com.shang.games.Interface.PPSSlidebarShare;
import com.shang.games.Interface.onWebviewLoadFinish;
import com.shang.games.Utils.BrightUtils;
import com.shang.games.Utils.FileUtils;
import com.shang.games.Utils.GlobelData;
import com.shang.games.Utils.LocalStorageProvider;
import com.shang.games.Utils.LogUtil;
import com.shang.games.Utils.PrefUtil;
import com.shang.games.Utils.Utils;
import com.shang.games.Views.BaseWebView;
import com.shang.games.Views.SegmentBar;
import com.shang.games.Views.SettingPopWindow;

public class ContentActivity extends Activity {
	
    /**v 2.0
     * 顶部标题栏
     */
    RelativeLayout titleLayout;
    BaseWebView mWebView;
    ImageView mImageView;
    TextView mTextView;
    TextView mTitle;
    String url;
    /**
     * 底部标签栏 轻触屏幕时出现
     */
    RelativeLayout bottomLayout;
    /**
     * 底部标签栏 出现时间 默认5秒 5秒后消失
     */
    final int showBottomBarTime = 5000;

    mHandler handler;// = new mHandler();

    /**
     * 计时器
     */
    Timer timer = new Timer();

    /**
     * 手指按下屏幕时间
     */
    long downTouchTime = 0l;
    /**
     * 手指弹起来时间
     */
    long upTouchTime = 0l;

    /**
     * 设置对话框
     */
    SettingPopWindow settingPopWindow;

    PrefUtil prefUtil;

    /**
     * 等待 旋转
     */
    ProgressBar mProgressBar;
    public static ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        getMyIntent();

        initViews();

        handler = new mHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null && Globle.isNeedShowBottomBar){
            Utils.setWebViewSize(prefUtil.getInt(PrefUtil.TextSizePref, 1), mWebView.getSettings());
        }
    }

    private void getMyIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            url = intent.getStringExtra("homeUrl");
        }
    }

    private void initViews() {
        titleLayout = (RelativeLayout) findViewById(R.id.content_layout);
        mWebView = (BaseWebView) findViewById(R.id.content_webView);
        mImageView = (ImageView) findViewById(R.id.content_imageView);
        mTextView = (TextView) findViewById(R.id.content_textView);
        mTitle = (TextView) findViewById(R.id.content_title);
        bottomLayout = (RelativeLayout) findViewById(R.id.content_bottom_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.content_progress);
        
        mImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgressBar.setVisibility(View.VISIBLE);
				mTextView.setVisibility(View.GONE);
				if (Utils.isOnline(ContentActivity.this)) {
					mWebView.reload();
				}
			}
		});

        String isNeedShowTitleBar = getResources().getString(R.string.isNeedShowTitleBar);
        titleLayout.setVisibility(isNeedShowTitleBar.equals("true") ? View.VISIBLE : View.GONE);

        if (Globle.isNeedShowBottomBar){
            mWebView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downTouchTime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            break;
                        case MotionEvent.ACTION_UP:
                            upTouchTime = System.currentTimeMillis();
                            if ((upTouchTime - downTouchTime) < 200) {
                                bottomLayout.setVisibility(View.VISIBLE);
                                Message msg = new Message();
                                msg.arg1 = 1;
                                handler.sendMessageDelayed(msg, showBottomBarTime);
                            }
                            break;
                    }
                    return false;
                }
            });
        }

        findViewById(R.id.content_webview_forword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Globle.webHistory.size() > 0) {
                    Intent intent = new Intent();
                    intent.setClass(ContentActivity.this, ContentActivity.class);
                    intent.putExtra("homeUrl", Globle.webHistory.getLast());
                    Globle.webHistory.removeLast();
                    startActivity(intent);
//                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        findViewById(R.id.content_webview_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyBackPressed();
            }
        });

        findViewById(R.id.content_webview_refresh_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView != null) mWebView.reload();
            }
        });

        findViewById(R.id.content_slidebar_gift_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyBackPressed();
            }
        });

        findViewById(R.id.content_slidebar_gift_detail_back_to_game).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.shareText(ContentActivity.this,
                        getResources().getString(R.string.share_title),
                        getResources().getString(R.string.share_content));
            }
        });

        findViewById(R.id.content_webview_setting_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSettingBtnClick();
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new PPSSlidebarShare(this, mJSFinish),"history");
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + Globle.APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
        //设置  Application Caches 缓存目录
        webSettings.setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        mWebView.setBackgroundColor(Color.WHITE);

        CookieManager cm = CookieManager.getInstance();
        
        prefUtil = new PrefUtil(ContentActivity.this);
        cm.setCookie(prefUtil.getString(PrefUtil.CacheUrl, ""),prefUtil.getString(PrefUtil.Cache, ""));
        Utils.setWebViewSize(prefUtil.getInt(PrefUtil.TextSizePref, 1), webSettings);

        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.setWebViewClient(new MyWebViewClient(this, onLoadFinish));
        mWebView.setWebChromeClient(new WebChromeClient() {
    		// For Android 3.0+
    		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
    			if (mUploadMessage != null)
    				return;
    			mUploadMessage = uploadMsg;
    			choosePic();
    		}

    		// For Android < 3.0
    		public void openFileChooser(ValueCallback<Uri> uploadMsg) {
    			openFileChooser(uploadMsg, "");
    		}

    		// For Android > 4.1.1
    		public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
    			openFileChooser(uploadMsg, acceptType);
    		}
                                        @Override
                                        public void onReceivedTitle(WebView view, String title) {
                                            super.onReceivedTitle(view, title);
                                            mTitle.setText(title);
                                        }

                                        public void onProgressChanged(WebView view, int progress) {
                                            setProgress(progress * 100);
                                            if (progress == 100) {
                                                mProgressBar.setVisibility(View.GONE);
                                                mTextView.setVisibility(View.GONE);
                                                view.setVisibility(View.VISIBLE);
                                            } else {
                                                view.setVisibility(View.INVISIBLE);
//                                                mTextView.setText("Loading..." + progress + "%");
//                                                mTextView.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
        );

        mWebView.loadUrl(url);
    }

	private void choosePic() {
		Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); // "android.intent.action.GET_CONTENT"
		String IMAGE_UNSPECIFIED = "image/*";
		innerIntent.setType(IMAGE_UNSPECIFIED); // 查看类型
		Intent wrapperIntent = Intent.createChooser(innerIntent, "上传方式选择");
		if (Build.VERSION.SDK_INT < 19) {
			startActivityForResult(wrapperIntent, 2);
		} else {
			startActivityForResult(wrapperIntent, 3);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Uri uri = data == null || resultCode != Activity.RESULT_OK ? null : data.getData();
		if (uri != null) {
			String imagePaths = "";
			try {
				if (requestCode == 3){
					imagePaths = FileUtils.getPath(ContentActivity.this, uri); // android
					LogUtil.e(" < 4.4 path = " + imagePaths);
					// 4.4
					// 以下
				}
				if (requestCode == 2){
					imagePaths = LocalStorageProvider.uri2Path(ContentActivity.this, uri); // android
					LogUtil.e("4.4 path = " + imagePaths);
					// 4.4
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			File vFile = new File(imagePaths);
			if (FileUtils.isLarge5M(vFile)) {
				Toast.makeText(ContentActivity.this, "您所上传的图片容量过大，请将其瘦瘦身吧",
						Toast.LENGTH_LONG).show();
			} else {
				uri = Uri.fromFile(vFile);
				if (mUploadMessage != null) {
					mUploadMessage.onReceiveValue(uri);
					mUploadMessage = null;
				}
			}
		} else {
			/**
			 * 加这段代码就是告诉网页，我们什么照片都没有选 如果不加，网页就不知道该干什么，下次再次点击就不会弹出选择照片的界面了
			 */
			Intent intent = new Intent();
			data = intent;
			File file = new File("");
			uri = Uri.fromFile(file);
			if (mUploadMessage != null) {
				mUploadMessage.onReceiveValue(uri);
				mUploadMessage = null;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
    JSFinish mJSFinish = new JSFinish() {
        @Override
        public void onFinish() {
            finish();
        }
    };

    onWebviewLoadFinish onLoadFinish = new onWebviewLoadFinish() {
        @Override
        public void isPass(boolean isPass) {
            if (isPass) {
            	mImageView.setVisibility(View.GONE);
//                mTextView.setVisibility(View.GONE);
            } else {
            	mImageView.setImageResource(R.drawable.load_fail);
            	mImageView.setVisibility(View.VISIBLE);
//                mTextView.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void launchActivity(String url) {
            Intent intent = new Intent();
            intent.setClass(ContentActivity.this, ContentActivity.class);
            intent.putExtra("homeUrl", url);
            startActivity(intent);
        }
    };

    class mHandler extends Handler {

        public mHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg != null && msg.arg1 == 1) {
                bottomLayout.setVisibility(View.GONE);
            }
        }
    }

    private void onMyBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            Globle.webHistory.add(url);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //亮度 监听器
    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BrightUtils.SetLightness(ContentActivity.this,progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //夜间模式 监听器
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            prefUtil.saveBoolean(PrefUtil.BrightPref, isChecked);
            if (isChecked){//night mode
            }else {
            }
        }
    };

    //字体大小 监听器
    SegmentBar.OnSegmentBarChangedListener onSegmentBarChangedListener = new SegmentBar.OnSegmentBarChangedListener() {
        @Override
        public void onBarItemChanged(int segmentItemIndex) {
            prefUtil.saveInt(PrefUtil.TextSizePref,segmentItemIndex);
            Utils.setWebViewSize(segmentItemIndex, mWebView.getSettings());
        }
    };

    private void onSettingBtnClick() {
        settingPopWindow = new SettingPopWindow(this, seekBarChangeListener, onCheckedChangeListener,onSegmentBarChangedListener);
        //显示窗口
        settingPopWindow.showAtLocation(ContentActivity.this.findViewById(R.id.content_bottom_layout),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

}
