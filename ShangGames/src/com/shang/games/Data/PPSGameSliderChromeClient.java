package com.shang.games.Data;

import com.shang.games.Utils.Utils;

import android.app.Activity;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

/**
 * Created by rain on 15/3/17.
 */
public class PPSGameSliderChromeClient extends WebChromeClient{
    Activity mActivity;

    public PPSGameSliderChromeClient(Activity act){
        this.mActivity = act;
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
//        if (mUploadMessage != null)
//            return;
//        mUploadMessage = uploadMsg;
        Utils.choosePic(mActivity);
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    // For Android > 4.welcome1.welcome1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }
}
