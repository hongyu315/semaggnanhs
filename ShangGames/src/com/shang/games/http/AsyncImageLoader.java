package com.shang.games.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * 对于图片异步处理类
 * 
 * @author liuxr
 * 
 */
public class AsyncImageLoader {
	static ImageView singImageView; // 针对于单张图片异步加载
	private static HashMap<String, SoftReference<Drawable>> singleImageCache = null;

	/**
	 * 通过图片地址,返回drawable
	 * 
	 * @param url
	 * @return
	 */
	public static Drawable loadImageFromUrl(String url) {
		ByteArrayOutputStream out = null;
		Drawable drawable = null;
		int BUFFER_SIZE = 1024 * 16;
		InputStream inputStream = null;
		try {
			URL murl = new URL(url);
			HttpURLConnection url_con = null;
			url_con = (HttpURLConnection) murl.openConnection();
			inputStream = url_con.getInputStream();
			// 获取session
			String cookieVal = url_con.getHeaderField("Set-Cookie");
			if (cookieVal != null) {
				String mSESSIONID = cookieVal.substring(0, cookieVal.indexOf(";"));
//				SlidebarGlobleData.imageSession = mSESSIONID.substring(mSESSIONID.indexOf("=") + 1, mSESSIONID.length());
			}
			BufferedInputStream in = new BufferedInputStream(inputStream, BUFFER_SIZE);
			out = new ByteArrayOutputStream(BUFFER_SIZE);
			int length = 0;
			byte[] tem = new byte[BUFFER_SIZE];
			length = in.read(tem);
			while (length != -1) {
				out.write(tem, 0, length);
				length = in.read(tem);
			}
			in.close();
			drawable = Drawable.createFromStream(new ByteArrayInputStream(out.toByteArray()), "src");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Exception e) {
				}
			}
		}
		return drawable;
	}

	/**
	 * 异步设置单张imageview图片,采取软引用
	 * 
	 * @param url
	 *            网络图片地址
	 * @param imageView
	 *            需要设置的imageview
	 */
	public static void setImageViewFromUrl(final String url, final ImageView imageView) {
		if (url == null)
			return;
		singImageView = imageView;
		// 如果软引用为空,就新建一个
		if (singleImageCache == null) {
			singleImageCache = new HashMap<String, SoftReference<Drawable>>();
		}
		// 如果软引用中已经有了相同的地址,就从软引用中获取
		if (singleImageCache.containsKey(url)) {
			SoftReference<Drawable> soft = singleImageCache.get(url);
			Drawable draw = soft.get();
			singImageView.setImageDrawable(draw);
			return;
		}
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				singImageView.setImageDrawable((Drawable) msg.obj);
			}
		};
		// 子线程不能更新UI,又不然会报错
		new Thread() {
			@Override
			public void run() {
				Drawable drawable = loadImageFromUrl(url);
				if (drawable == null) {
					Log.e("single imageview", "single imageview of drawable is null");
				} else {
					// Bitmap bitmap = drawableToBitmap(drawable);
					// bitmap = toRoundCorner(bitmap, 5);
					// drawable = new BitmapDrawable(bitmap);
					// 把已经读取到的图片放入软引用
					singleImageCache.put(url, new SoftReference<Drawable>(drawable));
				}
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			};
		}.start();
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	public static void clearImageCache() {
		if (singleImageCache != null) {
			singleImageCache = null;
		}
	}
}
