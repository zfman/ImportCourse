package com.zhuangfei.importcourse;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuangfei.adapterlib.AdapterLibManager;
import com.zhuangfei.adapterlib.activity.AdapterHomeActivity;
import com.zhuangfei.adapterlib.activity.SearchSchoolActivity;
import com.zhuangfei.adapterlib.cppinterface.CppInterface;

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
        AdapterLibManager.register("1a0dd2a43b83597e93961e42ab0e57b2","");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, SearchSchoolActivity.class);
                intent.putExtra(SearchSchoolActivity.EXTRA_SEARCH_KEY,"河南理工大学");
                startActivity(intent);

//                try {
//                    PackageManager packageManager = getPackageManager();
//                    Intent intent= new Intent();
//                    intent = packageManager.getLaunchIntentForPackage("com.zhuangfei.thirdapp");
//                    intent.putExtra("result","helloworld");
//                    startActivity(intent);
//                    finish();
//                } catch (Exception e) {
//                    Toast.makeText(MainActivity.this, "请到应用市场下载该应用", Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
            }
        });
    }
}
