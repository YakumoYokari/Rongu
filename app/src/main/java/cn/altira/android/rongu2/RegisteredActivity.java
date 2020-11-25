package cn.altira.android.rongu2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.litepal.LitePal;

import java.io.IOException;

import cn.altira.android.rongu2.pojo.User;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisteredActivity extends AppCompatActivity {

    private EditText userName;
    private EditText passWord;
    private EditText rePassWord;

    private Button reg;
    private TextView login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        getSupportActionBar().hide();

        userName = (EditText)findViewById(R.id.reg_user_name);
        passWord = (EditText)findViewById(R.id.reg_pass_word);
        rePassWord = (EditText)findViewById(R.id.reg_re_pass_word);

        reg = (Button) findViewById(R.id.reg_reg);
        login = (TextView) findViewById(R.id.reg_login);

        //注册按钮
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = userName.getText().toString();
                final String password = passWord.getText().toString();
                String repassword = rePassWord.getText().toString();
                if(username!=null){
                    if(password!=null){
                        if(repassword!=null){
                            if(password.equals(repassword)){
                                new Thread(){
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        try {
                                            OkHttpClient client = new OkHttpClient();
                                            Request request = new Request.Builder().url(MainActivity.url + "registered?userName=" + username + "&passWord=" + password).build();
                                            Response response = client.newCall(request).execute();
                                            String responseData = response.body().string();
                                            JSONObject jsonObject = JSON.parseObject(responseData);
                                            Integer code = jsonObject.getInteger("code");
                                            if(code == 200){
                                                final Integer numb = jsonObject.getInteger("data");
                                                new AlertDialog.Builder(RegisteredActivity.this)
                                                        .setTitle("注册成功！")
                                                        .setMessage("欢迎加入Rongu\n请记住您的账号:\n" + numb)
                                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                User user = LitePal.find(User.class,1);
                                                                if(user == null){
                                                                    user = new User();
                                                                }
                                                                user.setUserName(username);
                                                                user.setPassWord(password);
                                                                user.setAccountNumber(numb);
                                                                user.save();
                                                                Intent intent = new Intent(RegisteredActivity.this,MainActivity.class);
                                                                startActivity(intent);
                                                                RegisteredActivity.this.finish();
                                                            }
                                                        })
                                                        .show();
                                            }else if(code == 602){
                                                new AlertDialog.Builder(RegisteredActivity.this)
                                                        .setTitle("注册失败")
                                                        .setMessage("遇到了预期之外的错误")
                                                        .setPositiveButton("确定",null)
                                                        .show();
                                            }else {
                                                new AlertDialog.Builder(RegisteredActivity.this)
                                                        .setTitle("注册失败")
                                                        .setMessage("错误代码："+code)
                                                        .setPositiveButton("确定",null)
                                                        .show();
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        Looper.loop();
                                    }
                                }.start();

                            }else {
                                new AlertDialog.Builder(RegisteredActivity.this)
                                        .setTitle("两次输入的密码不一致")
                                        .setMessage("请保证两次输入的密码相同")
                                        .setPositiveButton("确定",null)
                                        .show();
                            }
                        }else {
                            new AlertDialog.Builder(RegisteredActivity.this)
                                    .setTitle("请确认密码")
                                    .setMessage("请再次输入密码")
                                    .setPositiveButton("确定",null)
                                    .show();
                        }
                    }else {
                        new AlertDialog.Builder(RegisteredActivity.this)
                                .setTitle("请输入密码")
                                .setMessage("请设置登录密码")
                                .setPositiveButton("确定",null)
                                .show();
                    }
                }else {
                    new AlertDialog.Builder(RegisteredActivity.this)
                            .setTitle("请输入昵称")
                            .setMessage("请设置聊天时的昵称")
                            .setPositiveButton("确定",null)
                            .show();
                }

            }
        });

        //返回登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisteredActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }
}