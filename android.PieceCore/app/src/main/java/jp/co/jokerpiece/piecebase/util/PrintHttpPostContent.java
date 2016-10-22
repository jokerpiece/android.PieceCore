package jp.co.jokerpiece.piecebase.util;

import android.os.Bundle;
import android.util.Log;

/**
 * Created by Antifuture on 2016/9/30.
 */

public class PrintHttpPostContent
{
    private Bundle bundle;
    private String postUrl;

    public PrintHttpPostContent(String postUrl, Bundle bundle)
    {
        this.bundle = bundle;
        this.postUrl = postUrl;
    }

    public void printHttpPostContent()
    {
        Log.d("http_post: ", "url: "+ postUrl);
        for (String key: bundle.keySet())
        {
            Log.d ("http_post: ", key + ": " + bundle.getString(key));
        }
    }
}
