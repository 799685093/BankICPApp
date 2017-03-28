package com.bankicp.app.model.request.util;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * HasMapToString
 * Created by admin on 2015/11/13.
 */
public class HasMapToString {
    private String key;
    private String value;
    private String params = "";
    private HashMap<String, String> mParams;

    public HasMapToString(HashMap<String, String> params) {
        this.mParams = params;
    }

    public String getString(String separator) {
        Iterator iterator = mParams.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (i == 0) {
                key = entry.getKey().toString();
                value = entry.getValue().toString();
            } else {
                key = separator + entry.getKey().toString();
                value = entry.getValue().toString();
            }
            params += key + "=" + value;
            i++;
        }
        Log.i("TAG", params.trim());
        return params.trim();
    }
}
