package com.zqw.appmanger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.zqw.appmanger.MainActivity;
import com.zqw.appmanger.R;
import com.zqw.appmanger.entity.AppInfo;
import com.zqw.appmanger.util.Utils;

import java.util.List;

/**
 * Created by uilubo on 2015/9/11.
 */
public class MyAdapter extends BaseAdapter {

    List<AppInfo> list;
    LayoutInflater inflater;
    IUninstall iUninstall;  //定义一个接口类型

    public void setiUninstall(IUninstall iUninstall) {
        this.iUninstall = iUninstall;
    }

    public MyAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(List<AppInfo> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null)?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.logo = (ImageView) convertView.findViewById(R.id.logo);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.version = (TextView) convertView.findViewById(R.id.version);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.btn = (Button) convertView.findViewById(R.id.btn);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        AppInfo app = list.get(position);
        holder.logo.setImageDrawable(app.icon);
        //进行高亮的显示
        if(MainActivity.KEYWORD == null) {
            holder.title.setText((app.appName));
        }else {
            holder.title.setText(Utils.highLightText(app.appName, MainActivity.KEYWORD));
        }
        holder.version.setText("版本："+app.versionName);
        holder.size.setText("大小："+app.size+"MB");

        final int pos = position;
        final String packageName = app.packageName;
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //卸载
                iUninstall.btnOnClick(pos,packageName);
            }
        });
        
        return convertView;
    }

    public class ViewHolder{
        ImageView logo;
        TextView title;
        TextView version;
        TextView size;
        Button btn;
    }
}
