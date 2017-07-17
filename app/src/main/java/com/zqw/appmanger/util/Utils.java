package com.zqw.appmanger.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.zqw.appmanger.entity.AppInfo;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 启文 on 2017/2/4.
 * 工具类
 */
public class Utils {

    //泛型集合
    public static List<AppInfo> getAppList(Context context){
        //返回值集合
        List<AppInfo> list = new ArrayList<AppInfo>();  //new一个泛型集合的对象list，用list来存储数据，然后返回
        //实例化包管理器
        PackageManager pm = context.getPackageManager();
        //获取所有已经安装的应用信息
        List<PackageInfo> pList = pm.getInstalledPackages(0);
        //遍历集合
        for(int i = 0; i<pList.size(); i++){
                PackageInfo packageInfo = pList.get(i); //拿到元素

            if(isThirdParrtyApp(packageInfo.applicationInfo)
                    && !packageInfo.packageName.equals(context.getPackageName())) { //排除自己
                //填充实体类
                AppInfo app = new AppInfo();
                app.packageName = packageInfo.packageName;  //包名
                app.versionName = packageInfo.versionName;  //版本名
                app.versionCode = packageInfo.versionCode;  //版本号
                app.insTime = packageInfo.firstInstallTime; //第一次安装时间
                app.updTime = packageInfo.lastUpdateTime;   //上次更新时间

                app.appName = (String) packageInfo.applicationInfo.loadLabel(pm);   //获取应用名
                app.icon = packageInfo.applicationInfo.loadIcon(pm);    //获取图标
                //计算程序的大小
                String dir = packageInfo.applicationInfo.publicSourceDir;   //获取程序的路径
                long byteSize = new File(dir).length(); //通过路径，取得该文件的大小（字节）
                app.byteSize = byteSize; //实际大小（字节）
                app.size = getSize(byteSize);   //格式化好的大小（MB）

                list.add(app);
            }
        }
        return list;
    }
    //判断应用是否是第三方应用
    public static boolean isThirdParrtyApp(ApplicationInfo appInfo){
        boolean flag = false;
        if((appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            //可更新的系统应用
            flag =true;
        }else if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
            //非系统应用
            flag = true;
        }
        return flag;
    }
    //字节转MB
    public static String getSize(long size){
        return new DecimalFormat("0.##").format(size*0.1/(1024*1024));
    }
    //毫秒时间转年月日时分秒
    public static String getTiem(long millis){
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        return  sdf.format(date);
    }
    //打开应用
    public static void openPackage(Context context,String packageName){
        Intent intent =
                context.getPackageManager().getLaunchIntentForPackage(packageName);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//旗标，在另一个任务栈启动一个任务
        context.startActivity(intent);//启动intent
    }
    //卸载应用
    public static void uninstallApk(Activity context, String packageName,int requestCode){

        Uri uri = Uri.parse("package:"+packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE,uri);
        context.startActivityForResult(intent,requestCode);
    }
    //搜索
    public static List<AppInfo> geSearchResult(List<AppInfo> list,String key){
        List<AppInfo> result = new ArrayList<AppInfo>();

        for(int i = 0; i<list.size(); i++){
            AppInfo app = list.get(i);          //都转换为小写字母比较
            if(app.appName.toLowerCase().contains(key.toLowerCase())){
                result.add(app);
            }
        }
        return result;
    }
    //搜索高亮
    public static SpannableStringBuilder highLightText(String str,String key){
        int start = str.toLowerCase().indexOf(key.toLowerCase());   //都用小写
        int end = start +key.length();

        SpannableStringBuilder sb = new SpannableStringBuilder(str);
        sb.setSpan(
                new ForegroundColorSpan(Color.RED),//前景色
                start,//起始坐标
                end, //终止坐标
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        );
        return sb;

    }

}
