package cn.altira.android.rongu2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.altira.android.rongu2.adapter.SearchUserAdapter;
import cn.altira.android.rongu2.pojo.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchUserActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        getSupportActionBar().hide();

        //绑定控件
        Button search = (Button)findViewById(R.id.search_search);
        final EditText editUsername = (EditText)findViewById(R.id.search_username);

        //绑定事件
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = editUsername.getText().toString();
                new Thread(){
                    @Override
                    public void run() {
                        Looper.prepare();
                        try {
                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder().url(MainActivity.url+"search?userName="+userName).build();
                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Log.e("responseData",responseData);
                            JSONObject jsonObject = JSON.parseObject(responseData);
                            Integer code = jsonObject.getObject("code",Integer.class);
                            if(code == 200){
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                List<User> users = new ArrayList<User>();
                                if(jsonArray.get(0) != null){
                                    for(int i = 0;i<jsonArray.size();i++){
                                        JSONObject object = (JSONObject)jsonArray.get(i);
                                        User u = new User();
                                        u.setUserName(object.getString("userName"));
                                        u.setAccountNumber(object.getInteger("accountNumber"));
                                        users.add(u);
                                    }
                                    final List<User> userList = users;
                                    Log.e("user",users.get(0).toString());
                                    if(users != null){
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                SearchUserAdapter adapter = new SearchUserAdapter(SearchUserActivity.this,R.layout.search_view_list,userList);
                                                ListView result = (ListView)findViewById(R.id.search_result);
                                                result.setAdapter(adapter);
                                            }
                                        });
                                    }else {
                                        new AlertDialog.Builder(SearchUserActivity.this)
                                                .setTitle("没找到用户")
                                                .setMessage("请确认账号或用户名正确")
                                                .setPositiveButton("确定",null)
                                                .show();
                                    }
                                }else {
                                    new AlertDialog.Builder(SearchUserActivity.this)
                                            .setTitle("没找到用户")
                                            .setMessage("请确认账号或用户名正确")
                                            .setPositiveButton("确定",null)
                                            .show();
                                }

                            }else {
                                new AlertDialog.Builder(SearchUserActivity.this)
                                        .setTitle("没找到用户")
                                        .setMessage("请确认账号或用户名正确")
                                        .setPositiveButton("确定",null)
                                        .show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Looper.loop();
                    }
                }.start();
            }
        });
    }
}