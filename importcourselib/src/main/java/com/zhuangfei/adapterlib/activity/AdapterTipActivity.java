package com.zhuangfei.adapterlib.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.utils.ViewUtils;

public class AdapterTipActivity extends AppCompatActivity {

    public EditText schoolEdit;
    public EditText urlEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter_tip);
        ViewUtils.setStatusTextGrayColor(this);
        inits();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"sqsptip");//申请适配tip
    }

    private void inits() {
        schoolEdit=findViewById(R.id.id_school_edittext);
        urlEdit=findViewById(R.id.id_url_edittext);
        findViewById(R.id.tv_to_adapter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAdapterBtnClicked();
            }
        });
        findViewById(R.id.ib_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecordEventManager.recordClickEvent(getApplicationContext(),"sqsptip.fh");
                finish();
            }
        });
    }

    public void onAdapterBtnClicked() {
        final String school = schoolEdit.getText().toString();
        final String url = urlEdit.getText().toString();
        if (TextUtils.isEmpty(school) || TextUtils.isEmpty(url)) {
            Toast.makeText(this, "不允许为空，请填充完整!",Toast.LENGTH_SHORT).show();
        } else if(!url.startsWith("http://")&&!url.startsWith("https://")){
            Toast.makeText(this, "请填写正确的url，以http://或https://开头",Toast.LENGTH_SHORT).show();
            return;
        }else{
            if (!school.endsWith("学校")&&!school.endsWith("学院")&&!school.endsWith("大学")) {
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setTitle("校名不太对哟")
                        .setMessage("你的校名好像不太对哟，务必填写全称,若校名不全，本次申请将被忽略!")
                        .setPositiveButton("确定是对的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                toUploadHtmlActivity(url,school);
                            }
                        })
                        .setNegativeButton("取消",null);
                builder.create().show();
            } else{
                toUploadHtmlActivity(url,school);
            }
        }
    }

    public void toUploadHtmlActivity(String url,String school){
        RecordEventManager.recordClickEvent(getApplicationContext(),"sqsptip.qwsp","school=?,url=",school,url);//前往适配
        Intent intent=new Intent(this,UploadHtmlActivity.class);
        intent.putExtra(UploadHtmlActivity.EXTRA_URL,url);
        intent.putExtra(UploadHtmlActivity.EXTRA_SCHOOL,school);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        RecordEventManager.recordClickEvent(getApplicationContext(),"sqsptip.fh");
        finish();
    }
}
