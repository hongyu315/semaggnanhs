package com.shang.games.Views;

import com.shang.games.Interface.JSFinish;
import com.shang.games.Interface.PPSSlidebarShare;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class BaseWebView extends WebView {

	JSFinish js;

	public BaseWebView(Context context, JSFinish jsFinish) {
		// TODO Auto-generated constructor stub
		super(context);
		js = jsFinish;
		addJavascriptInterface(new PPSSlidebarShare(context, jsFinish), "sharePage");
		addJavascriptInterface(new PPSSlidebarShare(context,jsFinish), "go(-1)");
		addJavascriptInterface(new PPSSlidebarShare(context,jsFinish), "back(-1)");
	}

	public BaseWebView(Context context, AttributeSet attrs) {
		super(context,attrs);
		// TODO Auto-generated constructor stub
		addJavascriptInterface(new PPSSlidebarShare(context, js), "sharePage");
		addJavascriptInterface(new PPSSlidebarShare(context, js), "go(-1)");
		addJavascriptInterface(new PPSSlidebarShare(context, js), "back(-1)");
	}
}
