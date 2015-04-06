package com.shang.games.Data;

import java.util.LinkedList;

/**
 * Created by rain on 15/3/17.
 */
public class Globle {
//    public static ValueCallback<Uri> mUploadMessage;
    public static final String APP_CACAHE_DIRNAME = "/webcache";
    public static final LinkedList<String> webHistory = new LinkedList<String>();


    /**
     * app 首页显示的网页地址
     */
    public static String HomeURL = "";

    /**
     * 是否需要显示底部栏
     */
    public static boolean isNeedShowBottomBar;
}
