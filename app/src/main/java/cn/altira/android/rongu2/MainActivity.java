package cn.altira.android.rongu2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import cn.altira.android.rongu2.pojo.FriendList;
import cn.altira.android.rongu2.pojo.FriendRequest;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.Md5Util;
import cn.altira.android.rongu2.websocket.JWebSocketClient;
import cn.altira.android.rongu2.websocket.JWebSocketClientService;
import cn.altira.android.rongu2.websocket.JWebSocketClientService.JWebSocketClientBinder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static String webSocket_uri = "ws://42.192.76.119:55555/websocket/";// + "10000/" + Md5Util.getDigest("123456");
    public static String url = "http://42.192.76.119:55555/";
    public static User nowUser;
    public static FriendList friendList;
    public static List<FriendRequest> friendRequest;

    public static JWebSocketClient client;
    private JWebSocketClientService jWebSClientService;
    private JWebSocketClientService.JWebSocketClientBinder binder;
    private ChatMessageReceiver chatMessageReceiver;
    private Intent bindIntent;

    private LinearLayout message;
    private LinearLayout addresssBook;
    private LinearLayout me;
    private ImageView iconMessage;
    private ImageView iconAddressBook;
    private ImageView iconme;
    private TextView textMessage;
    private TextView textAddressBook;
    private TextView textme;


    /**
     * 当前页面，1：messsage,2:addressBook,3:me
     */
    private Integer nowFragment;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (JWebSocketClientBinder) iBinder;
            jWebSClientService = binder.getService();
            client = jWebSClientService.client;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }

    };

    private class ChatMessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Integer code = intent.getIntExtra("code",0);
            Log.e("onReceive",code.toString());
            //处理密码错误
            if(code == 601){
                stopService(bindIntent);

                Intent loginActivity;
                loginActivity = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(loginActivity);

                finish();
            }else if(code == 205){
                Integer type = intent.getIntExtra("type",0);
                Log.e("type",type.toString());
                if(type == 1){
//                    Integer from = intent.getIntExtra("from",0);
//                    String msg = intent.getStringExtra("msg");
//                    UserMessage userMessage = new UserMessage();
//                    userMessage.setFrom(from);
//                    userMessage.setMsg(msg);
                    }

                    Log.e("friendRequest",friendRequest.get(0).getFrom().toString());
                } else if(code == 0){
                Log.e("Receive","0");
            }
        }
    }


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        //绑定底部导航栏\事件
        bottomTab();
        bottomTabEvent();

        //初始化页面
        textMessage.setTextColor(R.color.selected);
        iconMessage.setImageResource(R.drawable.message_checked);
        replaceFragment(new MessageViewFragment());
        friendRequest = new ArrayList<>();
        nowFragment = 1;



        //尝试自动登入
        List<User> nowUsers = LitePal.findAll(User.class);
        if(null != nowUsers && nowUsers.size() > 0){
            nowUser = nowUsers.get(0);
            bindService();
            doRegisterReceiver();
        }else {
            nowUser = new User();
            Intent loginActivity;
            loginActivity = new Intent(this,LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        //获取好友列表
        friendList = new FriendList();
        getFriends();


    }

    //获取好友列表
    public static void getFriends(){
        if(nowUser != null&&nowUser.getAccountNumber()!=null&&nowUser.getPassWord()!=null){
            final String number = nowUser.getAccountNumber().toString();
            final String password = nowUser.getPassWord();
            new Thread(){
                @Override
                public void run() {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request friendRequest = new Request.Builder().url(MainActivity.url+"findfriend?accountNumber="+number+"&passWord="+ Md5Util.getDigest(password)).build();
                        Response friendResponse = client.newCall(friendRequest).execute();
                        String friendResponseData = friendResponse.body().string();
                        com.alibaba.fastjson.JSONObject friendjsonObject = JSON.parseObject(friendResponseData);
                        int friendCode = friendjsonObject.getInteger("code");
                        if(friendCode == 200){
                            FriendList newList = (FriendList)friendjsonObject.getObject("data", FriendList.class);
                            friendList.setGroups(newList.getGroups());
                        }
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
        }

    }

    //碎片替换
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frame,fragment);
        transaction.commit();
    }

    //绑定底部导航栏
    private void bottomTab(){
        message = (LinearLayout)findViewById(R.id.tab_message);
        addresssBook = (LinearLayout)findViewById(R.id.tab_addressbook);
        me = (LinearLayout)findViewById(R.id.tab_me);
        iconMessage = (ImageView)findViewById(R.id.tab_message_icon);
        iconAddressBook = (ImageView)findViewById(R.id.tab_addressbook_icon);
        iconme = (ImageView)findViewById(R.id.tab_me_icon);
        textMessage = (TextView)findViewById(R.id.tab_message_text);
        textAddressBook = (TextView)findViewById(R.id.tab_addressbook_text);
        textme = (TextView)findViewById(R.id.tab_me_text);
    }

    //底部导航栏事件
    private void bottomTabEvent(){
        message.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(nowFragment != 1){
                    nowFragment = 1;
                    replaceFragment(new MessageViewFragment());
                    textMessage.setTextColor(R.color.selected);
                    iconMessage.setImageResource(R.drawable.message_checked);
                    textAddressBook.setTextColor(R.color.un_selected);
                    iconAddressBook.setImageResource(R.drawable.addressbook_unchecked);
                    textme.setTextColor(R.color.un_selected);
                    iconme.setImageResource(R.drawable.me_uncheck);
                }
            }
        });
        addresssBook.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(nowFragment != 2){
                    nowFragment = 2;
                    replaceFragment(new AddressBookFragment());
                    textMessage.setTextColor(R.color.un_selected);
                    iconMessage.setImageResource(R.drawable.message_unchecked);
                    textAddressBook.setTextColor(R.color.selected);
                    iconAddressBook.setImageResource(R.drawable.addressbook_checked);
                    textme.setTextColor(R.color.un_selected);
                    iconme.setImageResource(R.drawable.me_uncheck);
                }
            }
        });
        me.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(nowFragment != 3){
                    nowFragment = 3;
                    replaceFragment(new MeViewFragment());
                    textMessage.setTextColor(R.color.un_selected);
                    iconMessage.setImageResource(R.drawable.message_unchecked);
                    textAddressBook.setTextColor(R.color.un_selected);
                    iconAddressBook.setImageResource(R.drawable.addressbook_unchecked);
                    textme.setTextColor(R.color.selected);
                    iconme.setImageResource(R.drawable.me_check);


                }
            }
        });
    }


    //绑定服务
    private void  bindService(){
        bindIntent = new Intent(MainActivity.this,JWebSocketClientService.class);
        bindService(bindIntent,serviceConnection,BIND_AUTO_CREATE);
    }


    //注册广播
    private void doRegisterReceiver(){
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("cn.altira.android.rongu2.content");
        registerReceiver(chatMessageReceiver,filter);
    }


}