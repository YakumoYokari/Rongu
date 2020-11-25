package cn.altira.android.rongu2.util;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static final String ws = "ws://10.4.65.146:55555/websocket/" + "10000/" + Md5Util.getDigest("123456");

    public static void showToast(Context ctx, String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }
}
