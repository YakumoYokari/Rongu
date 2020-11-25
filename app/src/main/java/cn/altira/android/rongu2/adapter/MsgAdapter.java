package cn.altira.android.rongu2.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.altira.android.rongu2.R;
import cn.altira.android.rongu2.pojo.UserMessage;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private List<UserMessage> messageList;

    public MsgAdapter(List<UserMessage> userMessages){
        messageList = userMessages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chate_message_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserMessage userMessage = messageList.get(position);
        if(userMessage.getType().equals(2)){
            holder.getLayout.setVisibility(View.VISIBLE);
            holder.sendLayout.setVisibility(View.GONE);
            holder.getMsg.setText(userMessage.getMsg());
            Log.d("getmessage",userMessage.getMsg());
        }else if(userMessage.getType().equals(6)){
            holder.getLayout.setVisibility(View.GONE);
            holder.sendLayout.setVisibility(View.VISIBLE);
            holder.sendMsg.setText(userMessage.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout getLayout;
        LinearLayout sendLayout;

        TextView getMsg;
        TextView sendMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            getLayout = itemView.findViewById(R.id.chate_message_get);
            sendLayout = itemView.findViewById(R.id.chate_message_send);
            getMsg = itemView.findViewById(R.id.chate_message_get_msg);
            sendMsg = itemView.findViewById(R.id.chate_message_send_msg);
        }
    }
}
