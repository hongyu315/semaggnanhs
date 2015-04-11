package com.shang.games.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class PPSHttpClient {
	private static int timeoutConnection = 30000; // 网络连接超时

	/**
	 * post 请求
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(url);
		try {
			if (params != null) {
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			}
			HttpParams httpParameters = new BasicHttpParams();// Set the timeout
																// in
																// milliseconds
																// until a
																// connection is
																// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set
																							// the
																							// default
																							// socket
																							// timeout
																							// (SO_TIMEOUT)
																							// //
																							// in
																							// milliseconds
																							// which
																							// is
																							// the
																							// timeout
																							// for
																							// waiting
																							// for
																							// data.
			HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				return strResult;
			} else {
				httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * get 请求
	 */
	public static String get(String url) {
		HttpGet httpRequest = new HttpGet(url);
		try {
			HttpParams httpParameters = new BasicHttpParams();// Set the timeout
																// in
																// milliseconds
																// until a
																// connection is
																// established.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);// Set
																							// the
																							// default
																							// socket
																							// timeout
																							// (SO_TIMEOUT)
																							// //
																							// in
																							// milliseconds
																							// which
																							// is
																							// the
																							// timeout
																							// for
																							// waiting
																							// for
																							// data.
			HttpResponse httpResponse = new DefaultHttpClient(httpParameters).execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(httpResponse.getEntity());
				return strResult;
			} else {
				httpResponse.getStatusLine().toString();
				return "";
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 创建get请求的参数
	 * 
	 * @param params
	 * @return
	 */
	public static String buildParam(Map<String, String> params) {
		StringBuffer buffer = new StringBuffer();
		Set<String> keys = params.keySet();
		for (String key : keys) {
			buffer.append(key).append("=").append(params.get(key)).append("&");
		}
		String result = buffer.toString();
		return result.substring(0, result.length() - 1);
	}
}
