package cn.altira.android.rongu2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.alibaba.fastjson.JSON;

import java.util.List;

import cn.altira.android.rongu2.LoginActivity;
import cn.altira.android.rongu2.MainActivity;
import cn.altira.android.rongu2.R;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.ResultUtil;

public class SearchUserAdapter extends ArrayAdapter<User> {
    private int resourceid;

    public SearchUserAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context,textViewResourceId,objects);
        resourceid = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
        TextView name = (TextView)view.findViewById(R.id.search_list_name);
        TextView id = (TextView)view.findViewById(R.id.search_list_id);
        Button add = (Button)view.findViewById(R.id.search_add);
        name.setText(user.getUserName());
        id.setText(user.getAccountNumber().toString());

        final String userName = name.getText().toString();
        final String userId = id.getText().toString();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer from = MainActivity.nowUser.getAccountNumber();
                UserMessage userMessage = new UserMessage(Integer.parseInt(userId),1,from,"");
                MainActivity.client.send(JSON.toJSONString(ResultUtil.success(223,userMessage)));
                new AlertDialog.Builder(getContext())
                        .setTitle("发送成功")
                        .setMessage("请等待对方同意")
                        .setPositiveButton("确定",null)
                        .show();
            }
        });

        return view;
    }
}
