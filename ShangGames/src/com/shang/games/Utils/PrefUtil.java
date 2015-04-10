package com.shang.games.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author rainhong
 */
public class PrefUtil {

    /**
     * prefence文件名
     */
    public static final String SliderBarPref = "StorePref";
    public static final String isFirstLaunch = "isFirstLaunch";

    public static final String TextSizePref = "TextSize";
    public static final String BrightPref = "Bright";
    
    public static final String VersionCode = "VersionCode";

    

    private SharedPreferences mSharedPreferences;

    private Context mContext;
    
    /**
     * 初始化 *
     */
    public PrefUtil(Context ctx) {
        mContext = ctx;
        mSharedPreferences = mContext.getSharedPreferences(SliderBarPref, Context.MODE_PRIVATE);
    }

    /**
     * 保存配置信息
     */
    public void saveString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).commit();
    }

    public void saveBoolean(String key, boolean boo) {
        mSharedPreferences.edit().putBoolean(key, boo).commit();
    }

    public void saveInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).commit();
    }

    /**
     * 获得配置信息
     */
    public String getString(String key, String... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getString(key, defValue[0]);
        else
            return mSharedPreferences.getString(key, "");
    }

    public boolean getBoolean(String key, Boolean... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getBoolean(key, defValue[0]);
        else
            return mSharedPreferences.getBoolean(key, true);
    }

    public int getInt(String key, int... defValue) {
        if (defValue.length > 0)
            return mSharedPreferences.getInt(key, defValue[0]);
        else
            return mSharedPreferences.getInt(key, 0);
    }
}
