package cn.altira.android.rongu2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import org.litepal.LitePal;
import org.litepal.exceptions.DataSupportException;

import java.util.List;

import cn.altira.android.rongu2.adapter.MsgAdapter;
import cn.altira.android.rongu2.pojo.UserMessage;
import cn.altira.android.rongu2.util.ResultUtil;

public class ChateActivity extends AppCompatActivity {

    private Integer accountNumber;
    private String friendName;
    private MsgAdapter adapter;
    private RecyclerView recyclerView;
    private EditText msg;
    private Button send;
    private List<UserMessage> messages;
    private TextView friendNameView;
    private Integer msgSize = 20;
    ChatMessageReceiver chatMessageReceiver;

    private class ChatMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Integer code = intent.getIntExtra("code",0);
            if(code.equals(2)){

                UserMessage userMessage = new UserMessage();
                userMessage.setFrom(intent.getIntExtra("from",0));
                userMessage.setType(intent.getIntExtra("type",0));
                userMessage.setTo(intent.getIntExtra("to",0));
                userMessage.setMsg(intent.getStringExtra("msg"));

                messages.add(userMessage);

                adapter.notifyItemInserted(messages.size()-1);
                recyclerView.scrollToPosition(messages.size()-1);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chate);
        getSupportActionBar().hide();

        doRegisterReceiver();

        Intent intent = getIntent();
        accountNumber = intent.getIntExtra("accountNumber",0);
        friendName = intent.getStringExtra("name");

        if(accountNumber == 0){
            new AlertDialog.Builder(ChateActivity.this)
                    .setTitle("错误的账号")
                    .setMessage("你的朋友账号出现了一点问题")
                    .show();
        }


        friendNameView = findViewById(R.id.chate_name);
        recyclerView = findViewById(R.id.chate_view);
        msg = findViewById(R.id.chate_msg);
        send= findViewById(R.id.chate_send);

        friendNameView.setText(friendName);

        messages = LitePal.where("from = ? or to = ?",accountNumber.toString(),accountNumber.toString()).order("id asc").find(UserMessage.class);

        Log.d("code2",""+(messages.size()-1));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(messages);
        recyclerView.setAdapter(adapter);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String conten = msg.getText().toString();
                if(!"".equals(conten)&&conten!=null){
                    UserMessage userMessage = new UserMessage(accountNumber,6,MainActivity.nowUser.getAccountNumber(),conten);
                    userMessage.save();
                    messages.add(userMessage);
                    UserMessage sendmsg = new UserMessage(accountNumber,2,MainActivity.nowUser.getAccountNumber(),conten);
                    MainActivity.client.send(JSON.toJSONString(ResultUtil.success(223,sendmsg)));
                    adapter.notifyItemInserted(messages.size()-1);
                    recyclerView.scrollToPosition(messages.size()-1);
                    msg.setText("");
                }
            }
        });
    }

    //注册广播
    private void doRegisterReceiver(){
        chatMessageReceiver = new ChatMessageReceiver();
        IntentFilter filter = new IntentFilter("cn.altira.android.rongu2.content");
        registerReceiver(chatMessageReceiver,filter);
    }
}