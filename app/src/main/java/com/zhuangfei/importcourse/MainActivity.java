package com.zhuangfei.importcourse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zhuangfei.adapterlib.AdapterLibManager;
import com.zhuangfei.adapterlib.ParseManager;
import com.zhuangfei.adapterlib.activity.AutoImportActivity;
import com.zhuangfei.adapterlib.activity.NewSearchSchoolActivity;
import com.zhuangfei.adapterlib.core.ParseResult;
import com.zhuangfei.adapterlib.utils.GsonUtils;

import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        Button btn = (Button) findViewById(R.id.btn_goto_searchschool);

        Intent intent=getIntent();
        String appkey=intent.getStringExtra("appkey");
        if(!TextUtils.isEmpty(appkey)){
            Toast.makeText(this,"appkey="+appkey,Toast.LENGTH_SHORT).show();
        }
        AdapterLibManager.register("1a0dd2a43b83597e93961e42ab0e57b2","gstest");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AutoImportActivity.class);
                startActivityForResult(intent,1);
                App.enter = true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== AutoImportActivity.RESULT_CODE){
            if(ParseManager.isSuccess()&&ParseManager.getData()!=null){
                List<ParseResult> result=ParseManager.getData();
                AlertDialog.Builder builder=new AlertDialog.Builder(this)
                        .setTitle("Timetable")
                        .setMessage(GsonUtils.getGson().toJson(result));
                builder.create().show();
            }
        }
    }
}
