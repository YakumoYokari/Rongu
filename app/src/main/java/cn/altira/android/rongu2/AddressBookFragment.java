package cn.altira.android.rongu2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.List;

import cn.altira.android.rongu2.adapter.AddressBookViewAdapter;
import cn.altira.android.rongu2.pojo.FriendGroup;
import cn.altira.android.rongu2.pojo.FriendList;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.util.Md5Util;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddressBookFragment extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.addressbook_view,container,false);

        //绑定控件
        ImageView add = (ImageView)view.findViewById(R.id.addressbook_add);
        ListView friendListView = (ListView)view.findViewById(R.id.addressbook_list_view);
        LinearLayout newFriend = (LinearLayout)view.findViewById(R.id.addressbook_new_friend);
        ImageView refresh = view.findViewById(R.id.addressbook_reflish);

        //初始化
        List<FriendGroup> friendGroups = MainActivity.friendList.getGroups();
        if(friendGroups != null){
            AddressBookViewAdapter addressBookViewAdapter = new AddressBookViewAdapter(getActivity(),R.layout.addressbook_view_list,friendGroups);
            friendListView.setAdapter(addressBookViewAdapter);
        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("网络出现了一些问题")
                    .setMessage("正在加载……")
                    .show();
        }



        //绑定添加事件
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SearchUserActivity.class);
                startActivity(intent);
            }
        });

        newFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FriendRequestActivity.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reGet();
                rePain();
            }
        });



        return view;
    }

    //重绘
    public void rePain(){
        ListView friendListView = (ListView)view.findViewById(R.id.addressbook_list_view);

        //初始化
        List<FriendGroup> friendGroups = MainActivity.friendList.getGroups();
        if(friendGroups != null){
            AddressBookViewAdapter addressBookViewAdapter = new AddressBookViewAdapter(getActivity(),R.layout.addressbook_view_list,friendGroups);
            friendListView.setAdapter(addressBookViewAdapter);
        }else {
            new AlertDialog.Builder(getActivity())
                    .setTitle("网络出现了一些问题")
                    .setMessage("正在加载……")
                    .show();
        }
    }

    //获取数据
    public void reGet(){
        new Thread(){
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request friendRequest = new Request.Builder().url(MainActivity.url+"findfriend?accountNumber="+MainActivity.nowUser.getAccountNumber()+"&passWord="+ Md5Util.getDigest(MainActivity.nowUser.getPassWord())).build();
                    Response friendResponse = client.newCall(friendRequest).execute();
                    String friendResponseData = friendResponse.body().string();
                    Log.e("address",friendResponseData);
                    JSONObject friendjsonObject = JSON.parseObject(friendResponseData);
                    int friendCode = friendjsonObject.getInteger("code");
                    if(friendCode == 200){
                        FriendList newList = (FriendList)friendjsonObject.getObject("data", FriendList.class);
                        MainActivity.friendList.setGroups(newList.getGroups());
                        for (FriendGroup g:
                             MainActivity.friendList.getGroups()) {
                            for (User u:
                                 g.getFriends()) {
                                Log.e("address",u.getUserName());
                            }
                        }
                        for (FriendGroup g:
                                newList.getGroups()) {
                            for (User u:
                                    g.getFriends()) {
                                Log.e("address",u.getUserName());
                            }
                        }

                    }else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("网络出现了一些问题")
                                .setMessage("正在加载……")
                                .show();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }.start();

    }

    //重新计算高度
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
