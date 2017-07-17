package com.zqw.appmanger;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zqw.appmanger.adapter.IUninstall;
import com.zqw.appmanger.adapter.MyAdapter;
import com.zqw.appmanger.entity.AppInfo;
import com.zqw.appmanger.util.Utils;
import com.zqw.appmanger.view.RefreshView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,IUninstall, SearchView.OnQueryTextListener {

    ListView lv;
    List<AppInfo> list;
    MyAdapter adapter;
    SearchView sv;
    RefreshView refreshView;

    public static final int SORT_NAME = 0;
    public static final int SORT_DATE = 1;
    public static final int SORT_SIZE = 2;
    public static final String[] arr_sort={"按名称","按日期","按大小"};
    int currSort = SORT_NAME;
    TextView tv_sort;
    TextView tv_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("应用管理器");
        setSupportActionBar(toolbar);


        toolbar.setOnMenuItemClickListener(onMenuItemClick);

        tv_sort = (TextView) findViewById(R.id.tv_sort);
        tv_size = (TextView) findViewById(R.id.tv_size);
        refreshView = (RefreshView) findViewById(R.id.headerView);
        //拿到ListView
        lv = (ListView) findViewById(R.id.lv_main);
        //数据源

        //适配器
        adapter = new MyAdapter(this);

        //关联
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
        adapter.setiUninstall(this);

        refreshView.setOnRefreshListener(new RefreshView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            list = Utils.getAppList(MainActivity.this);
                            KEYWORD = null; //清空
                            handler.sendEmptyMessage(1);
                        }
                    }.start();

                    refreshView.finishRefreshing();
            }
        },0);

        updateData();
    }
    //顶部信息栏信息更新
    private void update_top(){
        tv_sort.setText("排序："+arr_sort[currSort]);
        tv_size.setText("应用数："+list.size());
    }



    //进度条对话框
    ProgressDialog pd;
    public void showProgrssDialog(){
        pd = new ProgressDialog(this);
        pd.setMessage("请稍等...");
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);//风格，旋转的
        pd.show();
    }

    Handler handler = new Handler(){    //内部类写法
        @Override
        public void handleMessage(Message msg) {
            updateDate_sort(currSort);//通知数据更新
            pd.dismiss();
        }
    };
    //耗时线程
    private  void updateData(){     //刷新列表
        new Thread(){
            @Override
            public void run() {
                list = Utils.getAppList(MainActivity.this);

                KEYWORD = null; //清空
                handler.sendEmptyMessage(1);
            }
        }.start();     //子线程
        showProgrssDialog();        //UI线程

    }
    //调用菜单项
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

        //搜索功能
        MenuItem search = menu.findItem(R.id.action_search);

        //监听搜索框展开与收起
        MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        updateData();
                        return true;
                    }
                });



        sv = (SearchView) search.getActionView();   //搜索框对象

        sv.setSubmitButtonEnabled(true);    //提交按钮
        sv.setQueryHint("查找应用名");

        sv.setOnQueryTextListener(this);    //接口，

        return super.onCreateOptionsMenu(menu);
    }

    /*
    item项点击事件
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AppInfo app = (AppInfo) adapterView.getItemAtPosition(i);   //通过点击的行，反向获取该行的应用信息
        //Utils.openPackage(this,app.packageName);    //调用工具类的打开应用方法
        Intent intent = new Intent();
        intent.setClass(this,AppDetailed.class);

        //HashMap<AppInfo,Object>itemMap
        //        = (HashMap<AppInfo, Object>) adapterView.getItemAtPosition(i);

        //intent.putExtra("key", (Serializable) app);
        startActivity(intent);
        //overridePendingTransition(R.anim.translate,R.anim.translate_out);
    }

    /**
     *卸载按钮点击事件
     */
    public static final int CODE_UNINSTALL = 0; //常量，uninstallApk方法的第三个参数：请求码
    @Override
    public void btnOnClick(int pos, String packageName) {
        Utils.uninstallApk(this,packageName,CODE_UNINSTALL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if(requestCode == CODE_UNINSTALL){
           //更新
           updateData();
       }

        super.onActivityResult(requestCode, resultCode, data);
    }


    Comparator<AppInfo> currComparator = null;

    //日期比较器（到序）
    Comparator<AppInfo> dateComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if(lhs.updTime > rhs.updTime){
                return -1;
            }else if(lhs.updTime == rhs.updTime){
                return 0;
            }else
                return 1;
        }
    };

    //大小比较器（到序）
    Comparator<AppInfo> sizeComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            if(lhs.byteSize > rhs.byteSize){
                return -1;
            }else if(lhs.byteSize == rhs.byteSize){
                return 0;
            }else
                return 1;
        }
    };

    //名称比较器
    Comparator<AppInfo> nameComparator = new Comparator<AppInfo>() {
        @Override
        public int compare(AppInfo lhs, AppInfo rhs) {
            return lhs.appName.toLowerCase().compareTo(rhs.appName.toLowerCase());  //compareTo是比较字符串大小的方法
        }       //toLowerCase()方法是将大写字母都变成小写
    };





    //菜单项点击判断
    private  Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener(){

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.sort_name){
                currSort = SORT_NAME;  //给一个常量，来区分比较器
            }
            if(id == R.id.sort_date){
                currSort = SORT_DATE;
            }
            if(id == R.id.sort_size){
                currSort = SORT_SIZE;
            }
            if(id == R.id.about){
                final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.about_dlg, null);
                Dialog selectorDialog = new Dialog(MainActivity.this, R.style.selectorDialog);
                selectorDialog.setContentView(view);
                selectorDialog.show();
            }
            updateDate_sort(currSort);
            return  true;
        }
    };


    //排序之后刷新列表
    private void updateDate_sort(int sort){
        if(sort == SORT_NAME){
            currComparator = nameComparator;    //用不同的比较器来排序
        }
        if(sort == SORT_DATE){
            currComparator = dateComparator;
        }
        if(sort == SORT_SIZE){
            currComparator = sizeComparator;
        }
        Collections.sort(list,currComparator);  //排序
        adapter.setList(list);
        adapter.notifyDataSetChanged();
        update_top();
    }
    public static String KEYWORD = null; //搜索关键字
    //搜索框提交信息
    @Override
    public boolean onQueryTextSubmit(String query) {
        KEYWORD = query;
        //Toast.makeText(this,""+query,Toast.LENGTH_SHORT).show();
        list =Utils.geSearchResult(list,query);
        updateDate_sort(currSort);  //将搜索后的结果排序
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }

    /**
     * 点击两下返回，才退出
     */
    private long mExitTime;
    public void onBackPressed(){
        if((System.currentTimeMillis() - mExitTime) > 2000){
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        }else{
            finish();
        }

    }

}