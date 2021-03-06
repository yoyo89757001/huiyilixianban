package megvii.testfacepass;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;

import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tencent.bugly.Bugly;
import com.yatoooon.screenadaptation.ScreenAdapterTools;


import java.io.IOException;
import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import megvii.facepass.FacePassHandler;
import megvii.testfacepass.beans.ChengShiIDBean;
import megvii.testfacepass.beans.MyObjectBox;
import megvii.testfacepass.beans.ZhiChiChengShi;
import megvii.testfacepass.dialogall.CommonData;
import megvii.testfacepass.dialogall.CommonDialogService;
import megvii.testfacepass.dialogall.ToastUtils;
import megvii.testfacepass.utils.FileUtil;
import megvii.testfacepass.utils.GsonUtil;
import megvii.testfacepass.utils.MyService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/8/3.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public  FacePassHandler facePassHandler=null;
    private static BoxStore mBoxStore;
    public static MyApplication myApplication;
    private Box<ChengShiIDBean> chengShiIDBeanBox;
    private MyService myService=null;

    static {
        System.loadLibrary("ruitongnative");
    }

    private static int sDens = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("MyApplication", "MyApplication启动");
        myApplication = this;
        mBoxStore = MyObjectBox.builder().androidContext(this).build();

        Bugly.init(getApplicationContext(), "b3082246d6", false);

        //适配
        ScreenAdapterTools.init(this);
      //  JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
      //  JPushInterface.init(getApplicationContext());
      //  JPushInterface.setAlias(getApplicationContext(),1, FileUtil.getSerialNumber(this)==null?FileUtil.getIMSI():FileUtil.getSerialNumber(this));
        Log.d("MyApplication","机器码"+ FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this));
        //全局dialog
        this.registerActivityLifecycleCallbacks(this);//注册
        CommonData.applicationContext = this;
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager mWindowManager  = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        CommonData.ScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
        Intent dialogservice = new Intent(this, CommonDialogService.class);
        startService(dialogservice);

        chengShiIDBeanBox=mBoxStore.boxFor(ChengShiIDBean.class);
        if(chengShiIDBeanBox.getAll().size()==0){
            OkHttpClient okHttpClient= new OkHttpClient();
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                    .get()
                    .url("http://v.juhe.cn/weather/citys?key=356bf690a50036a5cfc37d54dc6e8319");
            // .url("http://v.juhe.cn/weather/index?format=2&cityname="+text1+"&key=356bf690a50036a5cfc37d54dc6e8319");
            // step 3：创建 Call 对象
            Call call = okHttpClient.newCall(requestBuilder.build());
            //step 4: 开始异步请求
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("AllConnects", "请求失败"+e.getMessage());
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    Log.d("AllConnects", "请求成功"+call.request().toString());
                    //获得返回体
                    try{

                        ResponseBody body = response.body();
                        String ss=body.string().trim();
                        Log.d("AllConnects", "天气"+ss);

                        JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
                        Gson gson=new Gson();
                        final ZhiChiChengShi renShu=gson.fromJson(jsonObject,ZhiChiChengShi.class);
                        int size=renShu.getResult().size();
                        //  chengShiIDBeanBox.removeAll();

                        for (int i=0;i<size;i++){
                            ChengShiIDBean bean=new ChengShiIDBean();
                            bean.setId(renShu.getResult().get(i).getId());
                            bean.setCity(renShu.getResult().get(i).getCity());
                            bean.setDistrict(renShu.getResult().get(i).getDistrict());
                            bean.setProvince(renShu.getResult().get(i).getProvince());
                            chengShiIDBeanBox.put(bean);
                        }

                    }catch (Exception e){
                        Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
                    }

                }
            });
        }

        DisplayMetrics dm = getResources().getDisplayMetrics();
        sDens = dm.densityDpi;

        if (isAppProcess()) {
            Intent intent = new Intent(getApplicationContext(), MyService.class);
            bindService(intent, serviceConnection,  Context.BIND_AUTO_CREATE);
            Log.d("MyService", "开启APP服务....");
        }

    }

    public static int getDens(){
        return sDens;
    }


    public BoxStore getBoxStore(){
        return mBoxStore;
    }

    public FacePassHandler getFacePassHandler() {

        return facePassHandler;
    }

    public void setFacePassHandler(FacePassHandler facePassHandler1){
        facePassHandler=facePassHandler1;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if(activity.getParent()!=null){
            CommonData.mNowContext = activity.getParent();
        }else{
            CommonData.mNowContext = activity;
        }

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity.getParent()!=null){
            CommonData.mNowContext = activity.getParent();
        }else
            CommonData.mNowContext = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ToastUtils.getInstances().cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
     //   if (serviceConnection!=null)
      //  unbindService(serviceConnection);
    }

    // 在Activity中，我们通过ServiceConnection接口来取得建立连接与连接意外丢失的回调
   private  ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        // 建立连接
        // 获取服务的操作对象
            MyService.MyBinder binder = (MyService.MyBinder) service;
            myService= binder.getService();// 获取到的Service即MyService
            Log.d("MyService", "myService:" + myService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("MyService", "name:" + name);
        // 连接断开
        }
    };


    /**
     * 判断该进程是否是app进程
     * @return
     */
    public boolean isAppProcess() {
        String processName = getProcessName();
        if (processName == null || !processName.equalsIgnoreCase(this.getPackageName())) {
            return false;
        }else {
            return true;
        }
    }

    /**
     * 获取运行该方法的进程的进程名
     * @return 进程名称
     */
    public String getProcessName() {
        int processId = android.os.Process.myPid();
        String processName = null;
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        Iterator iterator = manager != null ? manager.getRunningAppProcesses().iterator() : null;
        if (iterator != null) {
            while (iterator.hasNext()) {
                ActivityManager.RunningAppProcessInfo processInfo = (ActivityManager.RunningAppProcessInfo) (iterator.next());
                try {
                    if (processInfo.pid == processId) {
                        processName = processInfo.processName;
                        return processName;
                    }
                } catch (Exception e) {
    //                LogD(e.getMessage())
                }
            }
        }
        return null;
    }

}
