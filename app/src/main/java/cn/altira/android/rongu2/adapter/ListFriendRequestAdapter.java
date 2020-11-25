package cn.altira.android.rongu2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;

import org.litepal.LitePal;

import java.util.List;

import cn.altira.android.rongu2.FriendRequestActivity;
import cn.altira.android.rongu2.MainActivity;
import cn.altira.android.rongu2.R;
import cn.altira.android.rongu2.pojo.FriendRequest;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.ResultUtil;

public class ListFriendRequestAdapter extends ArrayAdapter<FriendRequest> {
    private int resourceid;
    private int index;
    List<FriendRequest> userMessages;
    private FriendRequestActivity activity;

    public  ListFriendRequestAdapter(Context context, int textViewResourceId, List<FriendRequest> objects, int index, FriendRequestActivity a){
        super(context,textViewResourceId,objects);
        resourceid = textViewResourceId;
        userMessages = objects;
        activity = a;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendRequest userMessage = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);

        TextView textView = view.findViewById(R.id.list_friend_request_name);
        Button yes = view.findViewById(R.id.list_friend_request_yes);
        Button no = view.findViewById(R.id.list_friend_request_no);
        final ListView listView = view.findViewById(R.id.friend_request);


        textView.setText(userMessage.getFrom()+"想成为你的朋友");

        final Integer from = userMessage.getFrom();
        final Integer to = MainActivity.nowUser.getAccountNumber();
        final String msg = userMessage.getMsg();
        final Integer id = userMessage.getId();

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.client.send(JSON.toJSONString(ResultUtil.success(223,new UserMessage(to,4,from,msg))));
                userMessages.remove(index);
                LitePal.delete(FriendRequest.class,id);
                activity.reflash();
                new AlertDialog.Builder(getContext())
                        .setTitle("添加成功")
                        .setPositiveButton("确定",null)
                        .show();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.client.send(JSON.toJSONString(ResultUtil.success(223,new UserMessage(to,5,from,msg))));
                userMessages.remove(index);
                LitePal.delete(FriendRequest.class,id);
                activity.reflash();
                new AlertDialog.Builder(getContext())
                        .setTitle("已拒绝")
                        .setPositiveButton("确定",null)
                        .show();
            }
        });

        return view;
    }
}
