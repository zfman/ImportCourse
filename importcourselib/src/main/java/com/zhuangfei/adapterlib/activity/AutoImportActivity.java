package com.zhuangfei.adapterlib.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhuangfei.adapterlib.AdapterLibManager;
import com.zhuangfei.adapterlib.ParseManager;
import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.activity.adapter.QuestionAdapter;
import com.zhuangfei.adapterlib.activity.qingguo.XiquerLoginActivity;
import com.zhuangfei.adapterlib.activity.scan.ScanImportActivity;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.ListResult;
import com.zhuangfei.adapterlib.apis.model.QuestionModel;
import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.adapterlib.station.TinyUserManager;
import com.zhuangfei.adapterlib.station.model.TinyUserInfo;
import com.zhuangfei.adapterlib.utils.ClipUtils;
import com.zhuangfei.adapterlib.utils.JumpCmdUtils;
import com.zhuangfei.adapterlib.utils.PackageUtils;
import com.zhuangfei.adapterlib.utils.SchoolDaoUtils;
import com.zhuangfei.adapterlib.utils.ViewUtils;
import com.zhuangfei.toolkit.tools.ToastTools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoImportActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView0;
    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;

    RelativeLayout rlJwImport;
    RelativeLayout rlCommonImport;
    LinearLayout lluser;
    LinearLayout llScanImport;
    LinearLayout llXiquerImport;
    LinearLayout llSearch;

    TextView schoolTextView;
    ListView listView;
    QuestionAdapter questionAdapter;
    List<QuestionModel> questionModels;

    public static final int RESULT_CODE=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_import);
        ViewUtils.setStatusBarColor(this, Color.WHITE);
        ViewUtils.setStatusTextGrayColor(this);
        inits();
        request();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"sy.init",
                "libVersionName=?,libVersionNumber=?,",
                AdapterLibManager.getLibVersionName(),
                AdapterLibManager.getLibVersionNumber()+"");
    }

    private void inits() {
        ParseManager.clearCache();
        imageView0 = findViewById(R.id.iv_img0);
        imageView1 = findViewById(R.id.iv_img1);
        imageView2 = findViewById(R.id.iv_img2);
        imageView3 = findViewById(R.id.iv_img3);
        imageView4 = findViewById(R.id.iv_img4);
        rlJwImport = findViewById(R.id.rl_jw_import);
        rlCommonImport = findViewById(R.id.rl_common_import);
        lluser = findViewById(R.id.ll_user);
        llScanImport = findViewById(R.id.ll_scan_import);
        llXiquerImport = findViewById(R.id.ll_xiquer_import);
        llSearch = findViewById(R.id.ll_search);
        schoolTextView = findViewById(R.id.tv_school_name);

        int color = Color.parseColor("#A561F7");
        imageView0.setColorFilter(color);
        imageView1.setColorFilter(color);
        imageView2.setColorFilter(color);
        imageView3.setColorFilter(color);
        imageView4.setColorFilter(color);

        llSearch.setOnClickListener(this);
        rlJwImport.setOnClickListener(this);
        rlCommonImport.setOnClickListener(this);
        llXiquerImport.setOnClickListener(this);
        llScanImport.setOnClickListener(this);
        lluser.setOnClickListener(this);
        updateSchoolText();

        listView = findViewById(R.id.listview);
        questionModels = new ArrayList<>();
        questionAdapter = new QuestionAdapter(this,questionModels);
        listView.setAdapter(questionAdapter);

        if(!TinyUserManager.get(this).isLogin()){
            Intent intent = new Intent(this,TinyAuthActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void request(){
        TimetableRequest.getQuestions(this, new Callback<ListResult<QuestionModel>>() {
            @Override
            public void onResponse(Call<ListResult<QuestionModel>> call, Response<ListResult<QuestionModel>> response) {
                if(response==null) return;
                ListResult<QuestionModel> result = response.body();
                if(result==null) return;

                if(result.getCode()==200){
                    questionModels.clear();
                    questionModels.addAll(result.getData());
                    questionAdapter.notifyDataSetChanged();
                }else{
                    ToastTools.show(AutoImportActivity.this,""+result.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ListResult<QuestionModel>> call, Throwable t) {
                ToastTools.show(AutoImportActivity.this,"请求失败");
            }
        });
    }
    private void updateSchoolText(){
        School school = SchoolDaoUtils.getSchool(this);
        if(school!=null){
            schoolTextView.setText(school.getSchoolName());
        }else{
            schoolTextView.setText("请先选择学校");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            updateSchoolText();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ll_search){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.xzxx");//选择学校
            Intent intent = new Intent(this, NewSearchSchoolActivity.class);
            startActivityForResult(intent,100);
        }
        if(v.getId() == R.id.ll_xiquer_import){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.qgdr");//青果导入
            Intent intent=new Intent(this, XiquerLoginActivity.class);
            startActivity(intent);
        }

        if(v.getId() == R.id.ll_scan_import){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.smdr");//扫码导入
            School school = SchoolDaoUtils.getSchool(this);
            if(school!=null){
                checkScanImportPermission();
            }else{
                ToastTools.show(this,"请先在顶部选择学校!");
            }
        }

        if(v.getId() == R.id.ll_user){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.tczh");//用户管理
            TinyUserManager.get(AutoImportActivity.this).saveUserInfo(null);
            finish();
        }

        if(v.getId() == R.id.rl_common_import){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.tydr");//通用导入
            JumpCmdUtils.jumpCommonImportPage(this);
        }

        if(v.getId() == R.id.rl_jw_import){
            RecordEventManager.recordClickEvent(getApplicationContext(),"sy.jwdr");//教务导入
            School school = SchoolDaoUtils.getSchool(this);
            if(school!=null){
                JumpCmdUtils.jumpJwImportPage(this,school,false);
            }else{
                ToastTools.show(this,"请先在顶部选择学校!");
            }
        }
    }

    private void checkScanImportPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            School school = SchoolDaoUtils.getSchool(this);
            if(school!=null){
                JumpCmdUtils.jumpJwImportPage(this,school,true);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,Manifest.permission.VIBRATE},
                    10);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "请允许相机权限后再试", Toast.LENGTH_SHORT).show();
                } else {
                    School school = SchoolDaoUtils.getSchool(this);
                    if(school!=null){
                        JumpCmdUtils.jumpJwImportPage(this,school,true);
                    }
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ParseManager.isSuccess()){
            setResult(RESULT_CODE);
            finish();
        }
        updateSchoolText();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JumpCmdUtils.clearCache();
        RecordEventManager.upload(AdapterLibManager.applicationContext);
    }
}
