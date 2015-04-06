package com.shang.games.Interface;

import com.shang.games.Utils.LogUtil;
import com.shang.games.Utils.Utils;

import android.content.Context;
import android.webkit.JavascriptInterface;

public class PPSSlidebarShare {
	Context mcontext;
	JSFinish jsFinish;

	public PPSSlidebarShare(Context context, JSFinish mJsfinish) {
		mcontext = context;
		jsFinish = mJsfinish;
	}

	@JavascriptInterface
	public void go(int step){
		jsFinish.onFinish();
	}

	@JavascriptInterface
	public void back(int step){
		jsFinish.onFinish();
	}

	@JavascriptInterface
	public void sharePage(String title, String content) {
		sharePage(title, content, null);
	}

	@JavascriptInterface
	public void sharePage(final String title, final String content, final String imageUrl) {
		LogUtil.e("on share click");
		Utils.shareText(mcontext, title, content);
		new Thread(new Runnable() {
			@Override
			public void run() {
//				try {
//					final String mtitle = new String(title.getBytes(), "utf-8");
//					final String mcontent = new String(content.getBytes(), "utf-8");
//					if (StringUtil.isEmpty(imageUrl)) {
//						DataUtils.shareTo(mcontext, mtitle, mcontent, null);
//					} else {
//						Bitmap bitmap = AsyncImageLoader.drawableToBitmap(AsyncImageLoader.loadImageFromUrl(imageUrl));
//						if (bitmap != null) {
//							DataUtils.shareTo(mcontext, mtitle, mcontent, bitmap);
//						} else {
//							DataUtils.shareTo(mcontext, mtitle, mcontent, null);
//						}
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					try {
//						DataUtils.shareTo(mcontext, new String(title.getBytes(), "utf-8"), new String(content.getBytes(), "utf-8"), null);
//					} catch (UnsupportedEncodingException e1) {
//						e1.printStackTrace();
//					}
//				}
			}
		}).start();
	}
}
