package com.zhuangfei.adapterlib.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhuangfei.adapterlib.R;
import com.zhuangfei.adapterlib.RecordEventManager;
import com.zhuangfei.adapterlib.activity.OnCommonFunctionClickListener;
import com.zhuangfei.adapterlib.activity.custom.TokenImportPopWindow;
import com.zhuangfei.adapterlib.apis.model.QuestionModel;
import com.zhuangfei.adapterlib.apis.model.School;
import com.zhuangfei.adapterlib.apis.model.SearchResultModel;
import com.zhuangfei.adapterlib.apis.model.StationModel;
import com.zhuangfei.adapterlib.station.model.GreenFruitSchool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liu ZhuangFei on 2018/8/15.
 */

public class QuestionAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;

    List<QuestionModel> list;
    Activity context;

    public QuestionAdapter(Activity context, List<QuestionModel> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_questions, null);
            holder.layout = convertView.findViewById(R.id.id_search_school_layout);
            holder.title = convertView.findViewById(R.id.item_school_val);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final QuestionModel model = list.get(position);
        if(model!=null){
            holder.title.setText(model.getTitle());
            holder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RecordEventManager.recordClickEvent(context,"sy.cjwt."+position,
                            "title=?,url=?",model.getTitle(),model.getUrl());//常见问题
                    try {
                        Intent intent= new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(model.getUrl());
                        intent.setData(content_url);
                        context.startActivity(intent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
        return convertView;
    }

    //ViewHolder静态类
    static class ViewHolder {
        public TextView title;
        public LinearLayout layout;
    }
}
