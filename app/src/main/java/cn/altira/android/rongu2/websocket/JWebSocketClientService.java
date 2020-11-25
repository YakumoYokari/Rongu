package cn.altira.android.rongu2.websocket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import cn.altira.android.rongu2.MainActivity;
import cn.altira.android.rongu2.pojo.FriendRequest;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.Md5Util;
import cn.altira.android.rongu2.util.ResultUtil;

public class JWebSocketClientService extends Service {
    private URI uri;
    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        mHandler.removeCallbacks(heartBeatRunnable);
        gHandler.removeCallbacks(getMessage);
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);
        gHandler.postDelayed(getMessage,GET_MESSAGE_RATE);
        final Integer usernumb = MainActivity.nowUser.getAccountNumber();
        final String password = MainActivity.nowUser.getPassWord();
        if(usernumb!=null&&password!=null){
            new Thread(){
                @Override
                public void run() {
                    uri = URI.create(MainActivity.webSocket_uri+ usernumb.toString() +"/"+ Md5Util.getDigest(password));
                    client = new JWebSocketClient(uri){
                        @Override
                        public void onMessage(String message) {
                            try {
                                Log.e("msg",message);
                                JSONObject jsonObject = new JSONObject(message);
                                Integer code = jsonObject.getInt("code");
                                if(code == 205) {
                                    JSONArray getRequests;
                                    try {
                                         getRequests = jsonObject.getJSONArray("data");
                                    }catch (JSONException e){
                                        getRequests = new JSONArray();
                                        JSONObject j = jsonObject.getJSONObject("data");
                                        getRequests.put(j);
                                    }


                                    for (int i = 0; i < getRequests.length(); i++) {
                                        JSONObject getRequest = (JSONObject) getRequests.get(i);
                                        UserMessage userMessage = new UserMessage();
                                        userMessage.setFrom(getRequest.getInt("from"));
                                        userMessage.setType(getRequest.getInt("type"));
                                        userMessage.setTo(getRequest.getInt("to"));
                                        userMessage.setMsg(getRequest.getString("msg"));

                                        Log.e("205",userMessage.getType().toString());
                                        if(userMessage.getType()==2){
                                            userMessage.save();
                                            Intent intent = new Intent();
                                            intent.setAction("cn.altira.android.rongu2.content");
                                            intent.putExtra("code", 2);
                                            intent.putExtra("from", userMessage.getFrom());
                                            intent.putExtra("to", userMessage.getTo());
                                            intent.putExtra("type", userMessage.getType());
                                            intent.putExtra("msg", userMessage.getMsg());
                                            sendBroadcast(intent);
                                        }else if(userMessage.getType()==1){
                                            FriendRequest friendRequest = new FriendRequest();
                                            friendRequest.setType(1);
                                            friendRequest.setMsg(userMessage.getMsg());
                                            friendRequest.setFrom(userMessage.getFrom());

                                            boolean isexite = false;
                                            if(LitePal.isExist(FriendRequest.class)){
                                                List<FriendRequest> friendRequests = LitePal.findAll(FriendRequest.class);
                                                for (FriendRequest f:
                                                        friendRequests) {
                                                    if(f.getFrom().equals(friendRequest.getFrom())){
                                                        isexite = true;
                                                    }
                                                }
                                            }

                                            if(!isexite){
                                                friendRequest.save();
                                            }

                                        }else if(userMessage.getType()==3){

                                        }
                                    }
                                    send(JSON.toJSONString(ResultUtil.success(221)));
                                }else if(code == 201){
                                    Intent intent = new Intent();
                                    intent.setAction("cn.altira.android.rongu2.content");
                                    intent.putExtra("code", code);
                                    sendBroadcast(intent);
                                } else {
                                    Intent intent = new Intent();
                                    intent.setAction("cn.altira.android.rongu2.content");
                                    intent.putExtra("code", code);
                                    sendBroadcast(intent);
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    try {
                        client.connectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Intent intent = new Intent();
            intent.setAction("cn.altira.android.rongu2.content");
            intent.putExtra("message", JSON.toJSONString(ResultUtil.error(601,"未登录")));
            sendBroadcast(intent);
        }

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(heartBeatRunnable);
        gHandler.removeCallbacks(getMessage);
        client.close();
        super.onDestroy();
    }


    /**
     *心跳检测
     */
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Integer RECONNECT_COUNT = 0;
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (client != null) {
                if (client.isClosed()) {
                    reconnectWs();
                }
            } else {
                //如果client已为空，重新初始化websocket
                initSocketClient();
            }
            //定时对长连接进行心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private static final long GET_MESSAGE_RATE = 15 * 1000;//每隔15秒请求一次消息
    private Handler gHandler = new Handler();
    private Runnable getMessage = new Runnable() {
        @Override
        public void run() {
            if(client != null){
                if(!client.isClosed()){
                    client.send(JSON.toJSONString(ResultUtil.success(225,"android")));
                }
            }

            gHandler.postDelayed(this,GET_MESSAGE_RATE);
        }
    };


    //开启重连
    private void reconnectWs() {
        if(RECONNECT_COUNT<3){
            RECONNECT_COUNT++;
            mHandler.removeCallbacks(heartBeatRunnable);
            new Thread() {
                @Override
                public void run() {
                    try {
                        //重连
                        client.reconnectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else {
            Intent intent = new Intent();
            intent.setAction("cn.altira.android.rongu2.content");
            intent.putExtra("message", JSON.toJSONString(ResultUtil.error(601,"登录失败")));
            sendBroadcast(intent);
        }
    }


     //重新初始化websocket
    private void initSocketClient(){
        new Thread(){
            @Override
            public void run() {
                uri = URI.create(MainActivity.webSocket_uri);
                client = new JWebSocketClient(uri){
                    @Override
                    public void onMessage(String message) {
                        Intent intent = new Intent();
                        intent.setAction("cn.altira.android.rongu2.content");
                        intent.putExtra("message", message);
                        sendBroadcast(intent);
                    }
                };
                try {
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }


     //重置计数
    public void reSetCount(){
        RECONNECT_COUNT = 0;
    }
}
