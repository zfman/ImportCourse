package com.zhuangfei.adapterlib.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.fragment.AdapterSecondFragment;
import com.zhuangfei.adapterlib.fragment.AdapterThirdFragment;
import com.zhuangfei.adapterlib.fragment.HomeFragment;

public class AdapterHomeActivity extends AppCompatActivity implements View.OnClickListener {

    HomeFragment homeFragment;
    AdapterSecondFragment adapterSecondFragment;
    AdapterThirdFragment adapterThirdFragment;

    LinearLayout tabLayout1;
    LinearLayout tabLayout2;
    LinearLayout tabLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter_home);
        initViews();
        select(0);
    }

    private void initViews() {
        tabLayout1=findViewById(R.id.ll_tab1);
        tabLayout2=findViewById(R.id.ll_tab2);
        tabLayout3=findViewById(R.id.ll_tab3);

        tabLayout1.setOnClickListener(this);
        tabLayout2.setOnClickListener(this);
        tabLayout3.setOnClickListener(this);
    }

    private void select(int index){
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        switch (index){
            case 0:
                if(homeFragment==null){
                    homeFragment=new HomeFragment();
                }
                transaction.replace(R.id.id_fragment,homeFragment);
                break;
            case 1:
                if(adapterSecondFragment==null){
                    adapterSecondFragment=new AdapterSecondFragment();
                }
                transaction.replace(R.id.id_fragment,adapterSecondFragment);
                break;
            case 2:
                if(adapterThirdFragment==null){
                    adapterThirdFragment=new AdapterThirdFragment();
                }
                transaction.replace(R.id.id_fragment,adapterThirdFragment);
                break;
                default: break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ll_tab1){
            select(0);
        }
        if(view.getId()==R.id.ll_tab2){
            select(1);
        }
        if(view.getId()==R.id.ll_tab3){
            select(2);
        }
    }
}
