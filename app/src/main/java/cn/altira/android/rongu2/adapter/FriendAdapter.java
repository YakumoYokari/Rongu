package cn.altira.android.rongu2.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import cn.altira.android.rongu2.AddressBookFragment;
import cn.altira.android.rongu2.ChateActivity;
import cn.altira.android.rongu2.R;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.pojo.UserMessage;

public class FriendAdapter extends ArrayAdapter<User> {
    private int resourceid;

    public FriendAdapter(Context context, int textViewResourceId, List<User> objects){
        super(context,textViewResourceId,objects);
        resourceid = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final User user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceid,parent,false);

        TextView friendName = view.findViewById(R.id.friend_list_name);
        LinearLayout layout = view.findViewById(R.id.friend_list);

        friendName.setText(user.getUserName());

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ChateActivity.class);
                intent.putExtra("name",user.getUserName());
                intent.putExtra("accountNumber",user.getAccountNumber());
                getContext().startActivity(intent);
            }
        });

        return view;
    }
}
