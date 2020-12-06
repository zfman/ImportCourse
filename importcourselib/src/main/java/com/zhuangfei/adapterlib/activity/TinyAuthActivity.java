package com.zhuangfei.adapterlib.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.BaseResult;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.callback.ILoginFinishListener;
import com.zhuangfei.adapterlib.station.TinyUserManager;
import com.zhuangfei.adapterlib.station.model.TinyUserInfo;
import com.zhuangfei.adapterlib.utils.ViewUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录页面
 */
public class TinyAuthActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginButton;
    Button registerButton;

    EditText userName;
    EditText userPassword;
    LinearLayout loadingLayout;
    LinearLayout passwordLayout2;
    EditText userPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.setTransparent(this);
        setContentView(R.layout.activity_tiny_login);
        initView();
        initEvent();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"dl");
    }

    private void initEvent() {
        loginButton.setOnClickListener(this);
    }

    private void initView() {
        loginButton = (Button) findViewById(R.id.login);
        userName = (EditText) findViewById(R.id.user_name);
        userPassword = (EditText) findViewById(R.id.user_password);
        loadingLayout=findViewById(R.id.id_loadlayout);
        registerButton=findViewById(R.id.register);
        passwordLayout2=findViewById(R.id.ll_password2);
        userPassword2=findViewById(R.id.user_password2);
        loginButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        findViewById(R.id.wangjimima).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordEventManager.recordClickEvent(getApplicationContext(),"dl.wjmm");
                AlertDialog.Builder builder = new AlertDialog.Builder(TinyAuthActivity.this)
                        .setTitle("忘记密码")
                        .setMessage("如果未充值会员，重新注册账户即可；如果已充值，请将支付记录、账户名发送至邮箱1193600556@qq.com申请重置密码")
                        .setPositiveButton("确定",null);
                builder.create().show();
            }
        });
    }

    @Override
    public void onClick(View arg0) {
        if(arg0.getId()==R.id.register){
            if(loginButton.getVisibility()==View.VISIBLE){
                loginButton.setVisibility(View.GONE);
                passwordLayout2.setVisibility(View.VISIBLE);
                registerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_style));
            }else{
                login(false);
            }
        }
        if(arg0.getId()==R.id.login){
            login(true);
        }
    }

    public Context getContext(){
        return this;
    }

    /**
     * 登录请求服务器
     */
    private void login(boolean isLogin) {
        try{
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }catch (Exception e){}

        final String name = userName.getText().toString();
        final String pw = userPassword.getText().toString();
        final String pw2 = userPassword2.getText().toString();
        if (name.isEmpty() || pw.isEmpty()) {
            if(isLogin){
                Toast.makeText(this, "请输入账号或密码以登录",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "请输入账号或密码以注册",Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if(isLogin){
            loadingLayout.setVisibility(View.VISIBLE);
            login(name,pw,"1");
        }
        else{
            if(TextUtils.isEmpty(pw2)||!pw2.equals(pw)){
                Toast.makeText(this, "两次输入的密码不相同！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.trim().length()<5||pw.trim().length()<6){
                Toast.makeText(this, "账号不得少于5个字符，密码不得少于6个字符",Toast.LENGTH_SHORT).show();
            }else{
                loadingLayout.setVisibility(View.VISIBLE);
                register(name,pw);
            }
        }
    }

    public void login(String name,String pwd,String type){
        RecordEventManager.recordClickEvent(getApplicationContext(),"dl.dl","name=?",name);
        login(name,pwd,type,null,null,null,null,null,null);
    }

    public void login(String name,String pw,String type,String openid,String gender,String province,String city,String year,String figureUrl){
        TimetableRequest.loginUser(getContext(), name, pw, type,openid,gender,province,city,year,figureUrl,new Callback<ObjResult<TinyUserInfo>>() {
            @Override
            public void onResponse(Call<ObjResult<TinyUserInfo>> call, Response<ObjResult<TinyUserInfo>> response) {
                loadingLayout.setVisibility(View.GONE);
                ObjResult<TinyUserInfo> result=response.body();
                if(result!=null){
                    if(result.getCode()==200){
                        Toast.makeText(getContext(),"登录成功",Toast.LENGTH_SHORT).show();
                        TinyUserManager.get(getContext()).saveUserInfo(result.getData());
                        Intent intent = new Intent(TinyAuthActivity.this,AutoImportActivity.class);
                        startActivity(intent);
                        finish();
                    }else if(result.getCode()==338){

                    }else{
                        Toast.makeText(getContext(),result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Error:result is null",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ObjResult<TinyUserInfo>> call, Throwable t) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Error:"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void register(final String name, final String pw){
        RecordEventManager.recordClickEvent(getApplicationContext(),"dl.zc","name=?",name);
        TimetableRequest.registerUser(getContext(), name, pw, new Callback<BaseResult>() {
            @Override
            public void onResponse(Call<BaseResult> call, Response<BaseResult> response) {
                loadingLayout.setVisibility(View.GONE);
                BaseResult result=response.body();
                if(result!=null){
                    if(result.getCode()==200){
                        Toast.makeText(getContext(),"注册成功",Toast.LENGTH_SHORT).show();
                        login(name,pw,"1");
                    }else{
                        Toast.makeText(getContext(),result.getMsg(),Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(),"Error:result is null",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResult> call, Throwable t) {
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(getContext(),"Error:"+t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}