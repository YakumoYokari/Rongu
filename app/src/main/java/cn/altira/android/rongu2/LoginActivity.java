package cn.altira.android.rongu2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.altira.android.rongu2.pojo.FriendGroup;
import cn.altira.android.rongu2.pojo.FriendList;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.Md5Util;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText accountNumber;
    private EditText passWord;
    private Button login;
    private TextView registered;
    private TextView aboutUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        //加载控件
        accountNumber = (EditText)findViewById(R.id.login_account_number);
        passWord = (EditText)findViewById(R.id.login_pass_word);
        login = (Button)findViewById(R.id.login_login);
        registered = (TextView)findViewById(R.id.login_reg);
        aboutUs = (TextView) findViewById(R.id.login_about_us);

        //登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String number = accountNumber.getText().toString();
                final String password = passWord.getText().toString();
                if(number!=null){
                    if(password != null){
                        new Thread(){
                            @Override
                            public void run() {
                                Looper.prepare();
                                try {
                                    OkHttpClient client = new OkHttpClient();
                                    Request request = new Request.Builder().url(MainActivity.url+"login?accountNumber="+number+"&passWord="+ Md5Util.getDigest(password)).build();
                                    Response response = client.newCall(request).execute();
                                    String responseData = response.body().string();
                                    Log.d("login",responseData);
                                    JSONObject jsonObject = JSON.parseObject(responseData);
                                    Integer code = jsonObject.getInteger("code");
                                    User loginUser = (User)jsonObject.getObject("data",User.class);
                                    //'code':200表示密码正确
                                    if(code == 200){
                                        List<User> nowUsers = LitePal.findAll(User.class);
                                        User user;
                                        if(null != nowUsers && nowUsers.size() > 0) {
                                            user = nowUsers.get(0);
                                        }else {
                                            user = new User();
                                        }
                                        UserMessage newmessone = new UserMessage();
                                        newmessone.save();
                                        user.setAccountNumber(Integer.parseInt(number));
                                        user.setPassWord(password);
                                        user.setUserName(loginUser.getUserName());

                                        if(user.getFriendCount() != loginUser.getFriendCount()||true){
                                            user.setFriendCount(loginUser.getFriendCount());
                                            Request friendRequest = new Request.Builder().url(MainActivity.url+"findfriend?accountNumber="+number+"&passWord="+ Md5Util.getDigest(password)).build();
                                            Response friendResponse = client.newCall(friendRequest).execute();
                                            String friendResponseData = friendResponse.body().string();
                                            JSONObject friendjsonObject = JSON.parseObject(friendResponseData);
                                            int friendCode = friendjsonObject.getInteger("code");
                                            if(friendCode == 200){
                                                FriendList newList = (FriendList)friendjsonObject.getObject("data", FriendList.class);
                                                if(newList!=null&newList.getGroups()!=null){
                                                    if(MainActivity.friendList == null){
                                                        MainActivity.friendList = new FriendList();
                                                    }
                                                    MainActivity.friendList.setGroups(newList.getGroups());
                                                }else {
                                                    MainActivity.friendList = new FriendList();
                                                }

                                            }else {
                                                new AlertDialog.Builder(LoginActivity.this)
                                                        .setTitle("网络出现了一些问题")
                                                        .setMessage("正在加载……")
                                                        .show();
                                            }
                                        }

                                        user.save();

                                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else if(code == 601) {
                                        new AlertDialog.Builder(LoginActivity.this)
                                                .setTitle("密码错误")
                                                .setMessage("请确认账号和密码正确")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Looper.loop();
                            }
                        }.start();
                    }else {
                        new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("请输入密码")
                                .setMessage("密码不能为空")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                }else {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("请输入账号")
                            .setMessage("账号不能为空")
                            .setPositiveButton("确定",null)
                            .show();
                }

            }
        });

        //注册事件
        registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisteredActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //关于我们事件
        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Rongu")
                        .setMessage("移动互联网开发大作业\n版本：1.0\n作者：李昊")
                        .setPositiveButton("确定",null)
                        .show();
            }
        });
    }
}