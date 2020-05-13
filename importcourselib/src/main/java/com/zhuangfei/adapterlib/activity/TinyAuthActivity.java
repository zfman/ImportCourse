package com.zhuangfei.adapterlib.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.AuthAgent;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.BaseResult;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.recodeevent.LoginRecordData;
import com.zhuangfei.adapterlib.station.TinyUserManager;
import com.zhuangfei.adapterlib.station.UserManager;
import com.zhuangfei.adapterlib.station.model.TinyUserInfo;
import com.zhuangfei.adapterlib.thirdlogin.ThirdLoginContants;
import com.zhuangfei.adapterlib.uikit.NetworkUtil;
import com.zhuangfei.adapterlib.utils.GsonUtils;
import com.zhuangfei.adapterlib.utils.ViewUtils;
import com.zhuangfei.toolkit.tools.ToastTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;

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
    LinearLayout qqLoginLayout;
    LinearLayout wechatLoginLayout;
    LinearLayout gsLoginLayout;
    LinearLayout gsLoginContainer;

    Tencent mTencent;
    private static String user_openId, accessToken, refreshToken, scope;
    private IWXAPI api;
    private UserInfo mInfo = null;
    String qqOpenId=null;

    static MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.setTransparent(this);
        setContentView(R.layout.activity_tiny_login);
        mTencent=Tencent.createInstance(ThirdLoginContants.APPID_QQ,this);
        api = WXAPIFactory.createWXAPI(this, ThirdLoginContants.APPID_WECHAT,true);
        mInfo = new UserInfo(this, mTencent.getQQToken());
        handler=new MyHandler(this);
        initView();
        initEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        user_openId = intent.getStringExtra("openId");
        accessToken = intent.getStringExtra("accessToken");
        refreshToken = intent.getStringExtra("refreshToken");
        scope = intent.getStringExtra("scope");
        Log.d("ZFMANTest",user_openId);
        Log.d("ZFMANTest",accessToken);
        Log.d("ZFMANTest",refreshToken);
        if(!TextUtils.isEmpty(user_openId)){
//            login(null,null,"3",user_openId);
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<TinyAuthActivity> userInfoActivityWR;

        public MyHandler(TinyAuthActivity userInfoActivity){
            userInfoActivityWR = new WeakReference<TinyAuthActivity>(userInfoActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            int tag = msg.what;
            Bundle data = msg.getData();
            JSONObject json = null;
            switch (tag) {
                case NetworkUtil.CHECK_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        int errcode = json.getInt("errcode");
                        if (errcode == 0) {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                    "access_token=%s&openid=%s", accessToken, user_openId), NetworkUtil.GET_INFO);
                        } else {
                            NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
                                            "appid=%s&grant_type=refresh_token&refresh_token=%s", "wxd930ea5d5a258f4f", refreshToken),
                                    NetworkUtil.REFRESH_TOKEN);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case NetworkUtil.REFRESH_TOKEN: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        user_openId = json.getString("openid");
                        accessToken = json.getString("access_token");
                        refreshToken = json.getString("refresh_token");
                        scope = json.getString("scope");
                        NetworkUtil.sendWxAPI(handler, String.format("https://api.weixin.qq.com/sns/userinfo?" +
                                "access_token=%s&openid=%s", accessToken, user_openId), NetworkUtil.GET_INFO);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case NetworkUtil.GET_INFO: {
                    try {
                        json = new JSONObject(data.getString("result"));
                        final String nickname, sex, province, city, country, headimgurl;
                        headimgurl = json.getString("headimgurl");
//                        NetworkUtil.getImage(handler, headimgurl, NetworkUtil.GET_IMG);
                        String encode;
                        encode = getcode(json.getString("nickname"));
                        nickname = "nickname: " + new String(json.getString("nickname").getBytes(encode), "utf-8");
                        sex = "sex: " + json.getString("sex");
                        province = "province: " + json.getString("province");
                        city = "city: " + json.getString("city");
                        country = "country: " + json.getString("country");
                        ToastTools.show(userInfoActivityWR.get(),data.getString("result"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case NetworkUtil.GET_IMG: {
                    byte[] imgdata = data.getByteArray("imgdata");
                    final Bitmap bitmap;
                    if (imgdata != null) {
                        bitmap = BitmapFactory.decodeByteArray(imgdata, 0, imgdata.length);
                    } else {
                        bitmap = null;
                        ToastTools.show(userInfoActivityWR.get(), "头像图片获取失败");
                    }
                    break;
                }
            }
        }
    }


    private static String getcode (String str) {
        String[] encodelist ={"GB2312","ISO-8859-1","UTF-8","GBK","Big5","UTF-16LE","Shift_JIS","EUC-JP"};
        for(int i =0;i<encodelist.length;i++){
            try {
                if (str.equals(new String(str.getBytes(encodelist[i]),encodelist[i]))) {
                    return encodelist[i];
                }
            } catch (Exception e) {

            } finally{

            }
        } return "";
    }

    public void setLoadingViewVisible(boolean viewVisible){
        if(!viewVisible){
            loadingLayout.setVisibility(View.GONE);
        }else{
            loadingLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loginQQ() {
        setLoadingViewVisible(true);
        this.getIntent().putExtra(AuthAgent.KEY_FORCE_QR_LOGIN, false);
        mTencent.login(this, "all",loginListener);
    }
    public void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch(Exception e) {
            ToastTools.show(getContext(),"exception:"+e.getMessage());
        }
    }


    IUiListener loginListener=new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject object= (JSONObject) o;
            if(object!=null){
                initOpenidAndToken(object);
                qqOpenId = object.optString(Constants.PARAM_OPEN_ID);
                if(!TextUtils.isEmpty(qqOpenId)){
                    setLoadingViewVisible(true);
                    login(null,null,"2",qqOpenId);
                }else{
                    setLoadingViewVisible(false);
                }
            }else{
                setLoadingViewVisible(false);
            }
        }

        @Override
        public void onError(UiError uiError) {
            ToastTools.show(getContext(),"error login:"+uiError.errorMessage);
        }

        @Override
        public void onCancel() {
        }
    };

    IUiListener getUserInfoListener=new IUiListener() {
        @Override
        public void onComplete(Object o) {
            JSONObject object= (JSONObject) o;
            if(object!=null){
                int ret=object.optInt("ret");
                String msg=object.optString("msg");
                if(ret!=0){
                    ToastTools.show(getContext(),"error:"+msg);
                }else {
                    String nickName=object.optString("nickname");
                    String gender=object.optString("gender");
                    String province=object.optString("province");
                    String city=object.optString("city");
                    String year=object.optString("year");
                    String figureUrl=object.optString("figureurl_qq_2");
                    login(nickName,null,"2",qqOpenId,gender,province,city,year,figureUrl);
                }
            }
        }

        @Override
        public void onError(UiError uiError) {
            ToastTools.show(getContext(),"error getUserInfo:"+uiError.errorMessage);
        }

        @Override
        public void onCancel() {
        }
    };

    public void loginWechat(){
        setLoadingViewVisible(true);
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo,snsapi_friend,snsapi_message,snsapi_contact";
        req.state = "none";
        api.sendReq(req);
    }

    private void initEvent() {
        loginButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode,resultCode,data,loginListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
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

        qqLoginLayout=findViewById(R.id.ll_qq_login);
        wechatLoginLayout=findViewById(R.id.ll_wechat_login);
        gsLoginLayout=findViewById(R.id.ll_gs_login);
        gsLoginContainer=findViewById(R.id.ll_gs_container);
        qqLoginLayout.setOnClickListener(this);
        wechatLoginLayout.setOnClickListener(this);
        gsLoginLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        if(arg0.getId()==R.id.login){
            loginWechat();
//            onClickLogin();
//            login(true);
        }
        if(arg0.getId()==R.id.register){
            if(loginButton.getVisibility()==View.VISIBLE){
                loginButton.setVisibility(View.GONE);
                passwordLayout2.setVisibility(View.VISIBLE);
                registerButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_style));
            }else{
                login(false);
            }
        }
        if(arg0.getId()==R.id.ll_qq_login){
            loginQQ();
        }
        if(arg0.getId()==R.id.ll_wechat_login){
            loginWechat();
        }
        if(arg0.getId()==R.id.ll_gs_login){
            gsLoginContainer.setVisibility(View.VISIBLE);
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
        login(name,pwd,type,null,null,null,null,null,null);
    }
    public void login(String name,String pwd,String type,String openid){
        login(name,pwd,type,openid,null,null,null,null,null);
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
                        finish();
                    }else if(result.getCode()==338){
                        mInfo.getUserInfo(getUserInfoListener);
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

//    public void sendCode(Context context) {
//        RegisterPage page = new RegisterPage();
//        //如果使用我们的ui，没有申请模板编号的情况下需传null
//        page.setTempCode(null);
//        page.setRegisterCallback(new EventHandler() {
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) {
//                    // 处理成功的结果
//                    HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
//                    // 国家代码，如“86”
//                    String country = (String) phoneMap.get("country");
//                    // 手机号码，如“13800138000”
//                    String phone = (String) phoneMap.get("phone");
//                    // TODO 利用国家代码和手机号码进行后续的操作
//                    ToastTools.show(getContext(), GsonUtils.getGson().toJson(phoneMap));
//                } else{
//                    // TODO 处理错误的结果
//                }
//            }
//        });
//        page.show(context);
//    }

    public void register(final String name, final String pw){
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

//    private void submitPrivacyGrantResult(boolean granted) {
//        MobSDK.submitPolicyGrantResult(granted, new OperationCallback<Void>() {
//            @Override
//            public void onComplete(Void data) {
//                sendCode(getContext());
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                ToastTools.show(getContext(),"error:"+t.getMessage());
//            }
//        });
//    }
}