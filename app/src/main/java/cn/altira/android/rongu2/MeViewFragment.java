package cn.altira.android.rongu2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.litepal.LitePal;

import java.util.List;

import cn.altira.android.rongu2.pojo.FriendList;
import cn.altira.android.rongu2.pojo.FriendRequest;
import cn.altira.android.rongu2.pojo.User;
import cn.altira.android.rongu2.pojo.UserMessage;

public class MeViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.me_view,container,false);

        //绑定控件
        Button meLogout = view.findViewById(R.id.me_logout);
        TextView userName = (TextView)view.findViewById(R.id.me_user_name);
        TextView accountNumber= (TextView)view.findViewById(R.id.me_account_numb);

        //载入账号、用户名
        List<User> nowUsers = LitePal.findAll(User.class);
        User nowUser;
        if(null != nowUsers && nowUsers.size() > 0){
            nowUser = nowUsers.get(0);
            if(nowUser.getAccountNumber()!=null){
                userName.setText(nowUser.getUserName());
            }
            if(nowUser.getAccountNumber()!=null){
                accountNumber.setText(nowUser.getAccountNumber().toString());
            }
        }



        //添加登出事件
        meLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("确认退出")
                        .setMessage("即将退出登录")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                List<User> nowUsers = LitePal.findAll(User.class);
                                User nowUser;
                                if(null != nowUsers && nowUsers.size() > 0){
                                    nowUser = nowUsers.get(0);
                                    LitePal.delete(User.class,nowUser.getId());

                                }

                                LitePal.deleteAll(FriendRequest.class);
                                LitePal.deleteAll(UserMessage.class);

                                Intent intent = new Intent(getActivity(),LoginActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                            }
                        })
                        .show();

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}
