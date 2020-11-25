package cn.altira.android.rongu2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.util.List;

import cn.altira.android.rongu2.AddressBookFragment;
import cn.altira.android.rongu2.MainActivity;
import cn.altira.android.rongu2.R;
import cn.altira.android.rongu2.pojo.FriendGroup;
import cn.altira.android.rongu2.pojo.FriendList;
import cn.altira.android.rongu2.pojo.User;

public class AddressBookViewAdapter extends ArrayAdapter<FriendGroup> {
    private int resourceid;

    public  AddressBookViewAdapter(Context context, int textViewResourceId, List<FriendGroup> objects){
        super(context,textViewResourceId,objects);
        resourceid = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendGroup friendGroup = getItem(position);

        MainActivity.getFriends();

        View view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);
        TextView name = (TextView)view.findViewById(R.id.addressbook_list_group_name);

        ListView listView = (ListView)view.findViewById(R.id.addressbook_list_group_view);
        LinearLayout list = view.findViewById(R.id.addressbook_list);


        //绑定点击事件
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = (LinearLayout)view.findViewById(R.id.addressbook_list_group);
                ImageView icon = (ImageView)view.findViewById(R.id.addressbook_list_group_icon);

                ViewGroup.LayoutParams layoutParams;
                layoutParams = layout.getLayoutParams();
                if(layoutParams.height == 0){
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    icon.setImageResource(R.drawable.addressbook_list_down);
                }else {
                    layoutParams.height = 0;
                    icon.setImageResource(R.drawable.addressbook_list_right);
                }


                layout.setLayoutParams(layoutParams);
            }
        });

        //载入数据
        name.setText(friendGroup.getGroupName());


        FriendAdapter friendAdapter = new FriendAdapter(getContext(),R.layout.friend_list,friendGroup.getFriends());
        listView.setAdapter(friendAdapter);
        AddressBookFragment.setListViewHeightBasedOnChildren(listView);
        return view;
    }
}
