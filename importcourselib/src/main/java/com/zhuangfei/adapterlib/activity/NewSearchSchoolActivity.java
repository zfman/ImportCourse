package com.zhuangfei.adapterlib.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhuangfei.adapterlib.AdapterLibManager;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.activity.qingguo.XiquerLoginActivity;
import com.zhuangfei.adapterlib.apis.model.ParseJsModel;
import com.zhuangfei.adapterlib.apis.model.TemplateJsV2;
import com.zhuangfei.adapterlib.ParseManager;
import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.apis.model.SearchResultModel;
import com.zhuangfei.adapterlib.activity.adapter.SearchSchoolAdapter;
import com.zhuangfei.adapterlib.core.AssetTools;
import com.zhuangfei.adapterlib.station.IStationOperator;
import com.zhuangfei.adapterlib.station.StationSdk;
import com.zhuangfei.adapterlib.station.model.GreenFruitSchool;
import com.zhuangfei.adapterlib.utils.GsonUtils;
import com.zhuangfei.adapterlib.utils.Md5Security;
import com.zhuangfei.adapterlib.utils.PackageUtils;
import com.zhuangfei.adapterlib.utils.SchoolDaoUtils;
import com.zhuangfei.adapterlib.utils.ViewUtils;
import com.zhuangfei.adapterlib.apis.TimetableRequest;
import com.zhuangfei.adapterlib.apis.model.AdapterResultV2;
import com.zhuangfei.adapterlib.apis.model.ListResult;
import com.zhuangfei.adapterlib.apis.model.ObjResult;
import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.adapterlib.apis.model.StationModel;
import com.zhuangfei.adapterlib.apis.model.TemplateModel;
import com.zhuangfei.toolkit.model.BundleModel;
import com.zhuangfei.toolkit.tools.ActivityTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSearchSchoolActivity extends AppCompatActivity implements OnCommonFunctionClickListener {

    private static final String TAG = "NewSearchSchoolActivity";
    protected Activity context;

    ListView searchListView;
    List<SearchResultModel> models;
    List<SearchResultModel> allDatas;
    SearchSchoolAdapter searchAdapter;
    List<TemplateModel> templateModels;
    String baseJs;

    EditText searchEditText;
    LinearLayout loadLayout;

    boolean firstStatus=true;
    public static final String EXTRA_SEARCH_KEY="key";

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    IStationOperator operator;
    List<GreenFruitSchool> allSchool;

    TemplateJsV2 localTemplateJsV2;
    ParseJsModel parseJsModel;
    int nowTemplateVersion=0;

    LinearLayout emptyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_school);
        ViewUtils.setStatusTextGrayColor(this);
        initView();
        inits();
        loadSchools();
        RecordEventManager.recordDisplayEvent(getApplicationContext(),"xzxx");//选择学校
    }

    protected StationSdk getStationSdk(){
        return new StationSdk();
    }

    private void loadSchools() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String schoolStr= AssetTools.readAssetFile(getContext(),"schools.txt");
                TypeToken<List<GreenFruitSchool>> typeToken=new TypeToken<List<GreenFruitSchool>>(){};
                List<GreenFruitSchool> school=new Gson().fromJson(schoolStr,typeToken.getType());
                Message message=new Message();
                message.obj=school;
                message.what=0x123;
                handler.sendMessage(message);
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.obj!=null){
                allSchool= (List<GreenFruitSchool>) msg.obj;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        sp=getSharedPreferences("station_space_all", Context.MODE_PRIVATE);
        editor=sp.edit();
        searchListView=findViewById(R.id.id_search_listview);
        searchEditText=findViewById(R.id.id_search_edittext);
        loadLayout=findViewById(R.id.id_loadlayout);
        emptyLayout = findViewById(R.id.ll_empty);
    }

    public void setLoadLayout(boolean isShow) {
        if (isShow) {
            loadLayout.setVisibility(View.VISIBLE);
        } else {
            loadLayout.setVisibility(View.GONE);
        }
    }

    private void inits() {
        context = this;
        ParseManager.clearCache();
        String localJson=sp.getString("TemplateJsV2",null);
        if(!TextUtils.isEmpty(localJson)){
            localTemplateJsV2=GsonUtils.getGson().fromJson(localJson,TemplateJsV2.class);
            if(localTemplateJsV2!=null){
                templateModels=localTemplateJsV2.getTemplate();
                baseJs=localTemplateJsV2.getBase();
            }
        }

        models = new ArrayList<>();
        allDatas=new ArrayList<>();
        searchAdapter = new SearchSchoolAdapter(this, allDatas,models,this);
        searchListView.setAdapter(searchAdapter);
        searchEditText.addTextChangedListener(textWatcher);

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onItemClicked(i);
            }
        });
        findViewById(R.id.id_search_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });

        findViewById(R.id.id_copyright).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(Uri.parse("https://github.com/zfman/ImportCourse"));
                    context.startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(context,"跳转失败 https://github.com/zfman/ImportCourse",Toast.LENGTH_SHORT).show();
                }
            }
        });
        String searchKey=getIntent().getStringExtra(EXTRA_SEARCH_KEY);
        if(!TextUtils.isEmpty(searchKey)){
            search("recommend://"+searchKey);
        }

        findViewById(R.id.btn_shenqing).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),AdapterTipActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onItemClicked(int i) {
        final SearchResultModel model=models.get(i);
        if(model==null) return;
        //学校教务导入
        if(model.getType()==SearchResultModel.TYPE_SCHOOL){
            School school = (School) model.getObject();
            RecordEventManager.recordClickEvent(getApplicationContext(),"xzxx.xz","school=?",school.getSchoolName());//选中
            SchoolDaoUtils.saveSchool(context,school);
            finish();
        }
        else if(model.getType()==SearchResultModel.TYPE_XIQUER){
            RecordEventManager.recordClickEvent(getApplicationContext(),"xzxx.xqedr");//喜鹊儿导入
            onXuqerItemClicked(model);
        }
    }

    public void onXuqerItemClicked(SearchResultModel model){

        ActivityTools.toActivityWithout(this, XiquerLoginActivity.class,
                new BundleModel()
                        .put("selectSchool",model.getObject()));
    }

    public void toAdapterSameTypeActivity(String type,String js){
        Intent intent=new Intent(this,AdapterSameTypeActivity.class);
        intent.putExtra(AdapterSameTypeActivity.EXTRA_TYPE,type);
        intent.putExtra(AdapterSameTypeActivity.EXTRA_JS,js);
        startActivity(intent);
    }

    public Activity getContext() {
        return context;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String key = charSequence.toString();
            firstStatus=false;
            if (TextUtils.isEmpty(key)) {
                models.clear();
                allDatas.clear();
                searchAdapter.notifyDataSetChanged();
                emptyLayout.setVisibility(View.GONE);
                search("recommend://");
            } else {
                search(charSequence.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void search() {
        String key = searchEditText.getText().toString();
        search(key);
    }

    public void search(final String key) {
        RecordEventManager.recordClickEvent(getApplicationContext(),"xzxx.ss","key=?",key);//搜索学校
        emptyLayout.setVisibility(View.GONE);
        if(TextUtils.isEmpty(key)) {
            return;
        }

        models.clear();
        allDatas.clear();

        String packageMd5= PackageUtils.getPackageMd5(this);
        String appkey=AdapterLibManager.getAppKey();
        String time=""+System.currentTimeMillis();
        StringBuffer sb=new StringBuffer();
        sb.append("time="+time);
        String sign= Md5Security.encrypBy(sb.toString()+context.getResources().getString(R.string.md5_sign_key));
        if(TextUtils.isEmpty(packageMd5)||TextUtils.isEmpty(appkey)){
            Toast.makeText(context,"未初始化",Toast.LENGTH_SHORT).show();
            return;
        }

//        searchStation(key);
        if (!TextUtils.isEmpty(key)) {
            setLoadLayout(true);
            TimetableRequest.getAdapterSchoolsV2(this, key,packageMd5,appkey, time,sign,new Callback<ObjResult<AdapterResultV2>>() {
                @Override
                public void onResponse(Call<ObjResult<AdapterResultV2>> call, Response<ObjResult<AdapterResultV2>> response) {
                    ObjResult<AdapterResultV2> result = response.body();
                    if (result != null) {
                        if (result.getCode() == 200) {
                            showResult(result.getData(),key);
                        } else if(result.getCode()>=330&&result.getCode()<=400){
                            showDialog(result.getMsg());
                        }
                        else {
                            Toast.makeText(NewSearchSchoolActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewSearchSchoolActivity.this, "school response is null!", Toast.LENGTH_SHORT).show();
                    }
                    setLoadLayout(false);
                }

                @Override
                public void onFailure(Call<ObjResult<AdapterResultV2>> call, Throwable t) {
                    setLoadLayout(false);
                }
            });
        }
    }

    public void showDialog(String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setTitle("校验失败")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        if(dialogInterface!=null){
                            dialogInterface.dismiss();
                        }
                    }
                });
        builder.create().show();
    }

    public void searchStation(final String key) {
        if (!TextUtils.isEmpty(key)) {
            setLoadLayout(true);
            TimetableRequest.getStations(this, key, new Callback<ListResult<StationModel>>() {
                @Override
                public void onResponse(Call<ListResult<StationModel>> call, Response<ListResult<StationModel>> response) {
                    setLoadLayout(false);
                    ListResult<StationModel> result = response.body();
                    if (result != null) {
                        if (result.getCode() == 200) {
                            showStationResult(result.getData(),key);
                        } else {
                            Toast.makeText(NewSearchSchoolActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewSearchSchoolActivity.this, "school response is null!", Toast.LENGTH_SHORT).show();
                    }
                    setLoadLayout(false);
                }

                @Override
                public void onFailure(Call<ListResult<StationModel>> call, Throwable t) {
                    setLoadLayout(false);
                }
            });
        }
    }

    private void showStationResult(List<StationModel> result,String key) {
        if(key!=null&&key.indexOf("recommend://")!=-1){
        }else{
            if(!firstStatus&&searchEditText.getText()!=null&&key!=null&&!searchEditText.getText().toString().equals(key)){
                return;
            }
        }

        if (result == null) return;
        List<SearchResultModel> addList=new ArrayList<>();
        for (int i=0;i<Math.min(result.size(),SearchSchoolAdapter.TYPE_STATION_MAX_SIZE);i++) {
            StationModel model=result.get(i);
            SearchResultModel searchResultModel = new SearchResultModel();
            searchResultModel.setType(SearchResultModel.TYPE_STATION);
            if(result.size()>4){
                searchResultModel.setType(SearchResultModel.TYPE_STATION_MORE);
            }
            searchResultModel.setObject(model);
            addModelToList(searchResultModel);
        }

        for (int i=0;i<result.size();i++) {
            StationModel model=result.get(i);
            SearchResultModel searchResultModel = new SearchResultModel();
            searchResultModel.setType(SearchResultModel.TYPE_STATION);
            searchResultModel.setObject(model);
            addList.add(searchResultModel);
        }

        sortResult();
        addAllDataToList(addList);
        searchAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param result
     * @param key 用于校验输入框是否发生了变化，如果变化，则忽略
     */
    private void showResult(AdapterResultV2 result,String key) {
        if(key!=null&&key.indexOf("recommend://")!=-1){
        }else{
            if(!firstStatus&&searchEditText.getText()!=null&&key!=null){
                if(!searchEditText.getText().toString().equals(key)) return;
            }
        }
        if(result==null) return;

        nowTemplateVersion=result.getTemplateVersion();
        List<School> list=result.getSchoolList();
        if (list == null) {
            return;
        }
        if(list.size()==0){
            emptyLayout.setVisibility(View.VISIBLE);
        }else{
            emptyLayout.setVisibility(View.GONE);
        }
        if(allSchool!=null){
            for (GreenFruitSchool schoolBean : allSchool) {
                if (schoolBean.getXxmc() != null && schoolBean.getXxmc().indexOf(key) != -1) {
                    SearchResultModel searchResultModel2 = new SearchResultModel();
                    searchResultModel2.setType(SearchResultModel.TYPE_XIQUER);
                    searchResultModel2.setObject(schoolBean);
                    addModelToList(searchResultModel2);
                }
            }
        }

        for (School schoolBean : list) {
            SearchResultModel searchResultModel3 = new SearchResultModel();
            searchResultModel3.setType(SearchResultModel.TYPE_SCHOOL);
            searchResultModel3.setObject(schoolBean);
            addModelToList(searchResultModel3);
        }
        sortResult();
        searchAdapter.notifyDataSetChanged();
    }

    public void sortResult() {
        if (models != null) {
            Collections.sort(models);
        }
    }

    public synchronized void addModelToList(SearchResultModel searchResultModel) {
        if (models != null) {
            models.add(searchResultModel);
        }
    }

    public synchronized void addAllDataToList(List<SearchResultModel> searchResultModels) {
        if (allDatas != null) {
            for(SearchResultModel model:searchResultModels){
                allDatas.add(model);
            }
        }
    }

    @Override
    public void onBackPressed() {
        RecordEventManager.recordClickEvent(getApplicationContext(),"xzxx.fh");//返回
        finish();
    }

    @Override
    public void onCommonFunctionClicked(String key) {
        if(TextUtils.isEmpty(key)){
            return;
        }
        if(key.equals("upload")){
            Intent intent=new Intent(getContext(),AdapterTipActivity.class);
            startActivity(intent);
        }
    }
}
