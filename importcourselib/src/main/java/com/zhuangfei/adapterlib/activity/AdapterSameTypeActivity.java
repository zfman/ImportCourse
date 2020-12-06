package com.zhuangfei.adapterlib.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuangfei.adapterlib.ParseManager;
import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.utils.ViewUtils;

public class AdapterSameTypeActivity extends AppCompatActivity {

    public static final String STRING_EXTRA_NAME = "extra_name";
    public static final String INT_EXTRA_ID = "extra_id";

    EditText nameEdit;
    TextView titleTextView;
    ImageView backImageView;

    String js=null;
    int id = -1;

    public static final String EXTRA_JS="js";
    public static final String EXTRA_TYPE="type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter_same_type);
        ViewUtils.setStatusTextGrayColor(this);
        inits();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"wzdhq");//网址导航器
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ParseManager.isSuccess()){
            finish();
        }
    }

    private void inits() {
        nameEdit=findViewById(R.id.id_school_edittext);
        titleTextView=findViewById(R.id.id_title);
        findViewById(R.id.tv_other).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        js = getIntent().getStringExtra(EXTRA_JS);
        String type = getIntent().getStringExtra(EXTRA_TYPE);

        if(TextUtils.isEmpty(js)||TextUtils.isEmpty(type)){
            Toast.makeText(this,"js或者教务类型未知，结果不可预期！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            titleTextView.setText(type);
        }
        backImageView=findViewById(R.id.id_back);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordEventManager.recordDisplayEvent(getApplicationContext(),"wzdhq.fh");
                finish();
            }
        });

    }

    public void save() {
        final String url = nameEdit.getText().toString();
        if (TextUtils.isEmpty(url)||(!url.startsWith("http")&&!url.startsWith("https"))) {
            Toast.makeText(this, "请输入有效的网址", Toast.LENGTH_SHORT).show();
        } else {
            RecordEventManager.recordDisplayEvent(getApplicationContext(),"wzdhq.qwjwc");//网址导航器-前往教务处
            toAdapterActivity("自定义网址",url,js);
        }
    }

    public void toAdapterActivity(String school,String url,String js){
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"wzdhq.load","school=?,url=",school,url);//网址导航器
        Intent intent=new Intent(this,AdapterSchoolActivity.class);
        intent.putExtra(AdapterSchoolActivity.EXTRA_SCHOOL,school);
        intent.putExtra(AdapterSchoolActivity.EXTRA_URL,url);
        intent.putExtra(AdapterSchoolActivity.EXTRA_PARSEJS,js);
        intent.putExtra(AdapterSchoolActivity.EXTRA_TYPE,"同 "+school);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"wzdhq.fh");
        finish();
    }
}
