package cn.altira.android.rongu2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.altira.android.rongu2.adapter.ListFriendRequestAdapter;
import cn.altira.android.rongu2.pojo.FriendRequest;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.Md5Util;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        getSupportActionBar().hide();

        MainActivity.friendRequest.clear();
        if(LitePal.isExist(FriendRequest.class)){
            List<FriendRequest> friendRequests = LitePal.findAll(FriendRequest.class);
            for (FriendRequest f:
                    friendRequests) {
                FriendRequest m = new FriendRequest();
                m.setId(f.getId());
                m.setFrom(f.getFrom());
                m.setTo(f.getTo());
                m.setMsg(f.getMsg());
                m.setType(f.getType());
                MainActivity.friendRequest.add(m);
            }
        }


        ListView listView = findViewById(R.id.friend_request);

        int index = 0;
        List<FriendRequest> userMessages = MainActivity.friendRequest;
        if(userMessages != null){
            if(!userMessages.isEmpty()){
                ListFriendRequestAdapter listFriendRequestAdapter = new ListFriendRequestAdapter(FriendRequestActivity.this,R.layout.list_friend_request,userMessages,index,this);
                listView.setAdapter(listFriendRequestAdapter);
                index++;
            }
        }

    }

    public void reflash(){
        MainActivity.friendRequest.clear();
        if(LitePal.isExist(FriendRequest.class)){
            List<FriendRequest> friendRequests = LitePal.findAll(FriendRequest.class);
            for (FriendRequest f:
                    friendRequests) {
                FriendRequest m = new FriendRequest();
                m.setId(f.getId());
                m.setFrom(f.getFrom());
                m.setTo(f.getTo());
                m.setMsg(f.getMsg());
                m.setType(f.getType());
                MainActivity.friendRequest.add(m);
            }
        }

        ListView listView = findViewById(R.id.friend_request);

        List<FriendRequest> emptyuserMessages = new ArrayList<>();
        ListFriendRequestAdapter emptyFriendRequestAdapter = new ListFriendRequestAdapter(FriendRequestActivity.this,R.layout.list_friend_request,emptyuserMessages,0,this);
        listView.setAdapter(emptyFriendRequestAdapter);

        int index = 0;
        List<FriendRequest> userMessages = MainActivity.friendRequest;
        if(userMessages != null){
            if(!userMessages.isEmpty()){
                ListFriendRequestAdapter listFriendRequestAdapter = new ListFriendRequestAdapter(FriendRequestActivity.this,R.layout.list_friend_request,userMessages,index,this);
                listView.setAdapter(listFriendRequestAdapter);
                index++;
            }
        }
    }
}