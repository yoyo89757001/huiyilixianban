package megvii.testfacepass.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.sdsmdg.tastytoast.TastyToast;
import com.sunfusheng.marqueeview.MarqueeView;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import megvii.facepass.FacePassException;
import megvii.facepass.FacePassHandler;
import megvii.facepass.types.FacePassAddFaceResult;
import megvii.facepass.types.FacePassDetectionResult;
import megvii.facepass.types.FacePassFace;
import megvii.facepass.types.FacePassImage;
import megvii.facepass.types.FacePassImageRotation;
import megvii.facepass.types.FacePassImageType;
import megvii.facepass.types.FacePassRecognitionResult;
import megvii.facepass.types.FacePassRecognitionResultType;
import megvii.testfacepass.MyApplication;
import megvii.testfacepass.R;
import megvii.testfacepass.beans.BaoCunBean;
import megvii.testfacepass.beans.BenDiJiLuBean;
import megvii.testfacepass.beans.MSRBean;
import megvii.testfacepass.beans.Subject;
import megvii.testfacepass.beans.Subject_;
import megvii.testfacepass.beans.TianQiBean;
import megvii.testfacepass.beans.TodayBean;
import megvii.testfacepass.camera.CameraManager;
import megvii.testfacepass.camera.CameraPreview;
import megvii.testfacepass.camera.CameraPreviewData;
import megvii.testfacepass.dialog.XiuGaiGaoKuanDialog;
import megvii.testfacepass.dialogall.XiuGaiListener;
import megvii.testfacepass.ljkplay.widget.media.IjkVideoView;
import megvii.testfacepass.tts.control.InitConfig;
import megvii.testfacepass.tts.control.MySyntherizer;
import megvii.testfacepass.tts.control.NonBlockSyntherizer;
import megvii.testfacepass.tts.listener.UiMessageListener;
import megvii.testfacepass.tts.util.OfflineResource;
import megvii.testfacepass.utils.DateUtils;
import megvii.testfacepass.utils.FacePassUtil;
import megvii.testfacepass.utils.FileUtil;
import megvii.testfacepass.utils.GlideUtils;
import megvii.testfacepass.utils.GsonUtil;
import megvii.testfacepass.utils.SettingVar;
import megvii.testfacepass.view.GlideCircleTransform;
import megvii.testfacepass.view.GlideRoundTransform;
import megvii.testfacepass.view.SlowScrollView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tv.danmaku.ijk.media.player.IMediaPlayer;


public class MainActivity202 extends Activity implements CameraManager.CameraListener, XiuGaiListener {
    protected Handler mainHandler;
    @BindView(R.id.xiaoshi)
    TextView xiaoshi;
    @BindView(R.id.wendu)
    TextView wendu;
    @BindView(R.id.tianqi)
    TextView tianqi;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.lunboview)
    RollPagerView lunboview;
    @BindView(R.id.da_bg)
    ImageView daBg;
    @BindView(R.id.xingqi)
    TextView xingqi;
    @BindView(R.id.riqi)
    TextView riqi;
    @BindView(R.id.shu_liebiao)
    LinearLayout shuLiebiao;
    @BindView(R.id.scrollview)
    SlowScrollView scrollView;
    @BindView(R.id.yinying)
    ImageView yinying;


    private MarqueeView marqueeView;
    private Box<Subject> subjectBox = null;
    private static boolean isSC = true;
    private String appId = "11644783";
    private String appKey = "knGksRFLoFZ2fsjZaMC8OoC7";
    private String secretKey = "IXn1yrFezEo55LMkzHBGuTs1zOkXr9P4";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_speech_female.data为离线男声模型；bd_etts_speech_female.data为离线女声模型
    private String offlineVoice = OfflineResource.VOICE_FEMALE;
    // 主控制类，所有合成控制方法从这个类开始
    private MySyntherizer synthesizer;
    private static Vector<Subject> dibuList = new Vector<>();//下面的弹窗
//    private RequestOptions myOptions = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    // .transform(new GlideRoundTransform(MainActivity.this,10));

    private RequestOptions myOptions2 = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
            .transform(new GlideRoundTransform(MainActivity202.this, 20));


    private LinkedBlockingQueue<Subject> linkedBlockingQueue;
    /* 人脸识别Group */
    private static final String group_name = "face-pass-test-x";
    /* 程序所需权限 ：相机 文件存储 网络访问 */
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};
    //  private WindowManager wm;
    /* SDK 实例对象 */
    public FacePassHandler mFacePassHandler;
    /* 相机实例 */
    private CameraManager manager;
    /* 显示人脸位置角度信息 */
    private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
    private boolean isAnXia = true;
    private ConcurrentHashMap<Long,Integer> concurrentHashMap=new ConcurrentHashMap<Long, Integer>();
    private static boolean cameraFacingFront = true;
    private int cameraRotation;
    private static final int cameraWidth = 1280;
    private static final int cameraHeight = 720;
    private IjkVideoView shipingView;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    /* 网络请求队列*/
    /*Toast 队列*/
    LinkedBlockingQueue<Toast> mToastBlockQueue;
    private static boolean isLink = true;
    private long tID=-1;
    /*DetectResult queue*/
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    /*recognize thread*/
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    TanChuangThread tanChuangThread;
    private int dw, dh;
    private Box<BaoCunBean> baoCunBeanDao = null;
    private Box<TodayBean> todayBeanBox = null;
    private Box<BenDiJiLuBean> benDiJiLuBeanBox = null;
    private BaoCunBean baoCunBean = null;
    private TodayBean todayBean = null;
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private WeakHandler mHandler;
    private static final String authIP = "https://api-cn.faceplusplus.com";
    private static final String apiKey = "zIvtfbe_qPHpLZzmRAE-zVg7-EaVhKX2";
    private static final String apiSecret = "-H4Ik0iZ_5YTyw5NPT8LfnJREz_NCbo7";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mImageCache = new FaceImageCache();
        mToastBlockQueue = new LinkedBlockingQueue<>();
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        todayBeanBox = MyApplication.myApplication.getBoxStore().boxFor(TodayBean.class);
        todayBean = todayBeanBox.get(123456L);
        benDiJiLuBeanBox = MyApplication.myApplication.getBoxStore().boxFor(BenDiJiLuBean.class);
        // initAndroidHandler();
        //  isOne = true;
        baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }

        };
        baoCunBean = baoCunBeanDao.get(123456L);
        subjectBox = MyApplication.myApplication.getBoxStore().boxFor(Subject.class);

        //每分钟的广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>();

        EventBus.getDefault().register(this);//订阅
        /* 初始化界面 */
        initView();
//        dibuliebiao.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//                    @Override
//                    public void onGlobalLayout(){
//                        //只需要获取一次高度，获取后移除监听器
//                        dibuliebiao.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    }
//
//                });

        /* 申请程序所需权限 */
        if (!hasPermission()) {
            requestPermission();
        } else {
            //初始化
          //  FacePassHandler.getAuth(authIP, apiKey, apiSecret);
            FacePassHandler.initSDK(getApplicationContext());
            Log.d("MainActivity201", FacePassHandler.getVersion());
        }

        if (baoCunBean != null)
            initialTts();

        if (baoCunBean != null) {
            FacePassUtil util = new FacePassUtil();
            util.init(MainActivity202.this, getApplicationContext(), cameraRotation, baoCunBean);
        } else {
            Toast tastyToast = TastyToast.makeText(MainActivity202.this, "获取本地设置失败,请进入设置界面设置基本信息", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        }

        /* 初始化网络请求库 */
        //   requestQueue = Volley.newRequestQueue(getApplicationContext());

        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();

        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;


        mHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 111:{
                        //vip
                        final Subject bean2 = (Subject) msg.obj;
                        yinying.setVisibility(View.VISIBLE);
                        final View view_dk = View.inflate(MainActivity202.this, R.layout.vipfangke_item_jj, null);
                        ScreenAdapterTools.getInstance().loadView(view_dk);
                        TextView name2 = (TextView) view_dk.findViewById(R.id.name);
                        TextView bumen = (TextView) view_dk.findViewById(R.id.bumen);
                        ImageView touxiang2 = (ImageView) view_dk.findViewById(R.id.touxiang);
                        LinearLayout xiaoxi_ll = (LinearLayout) view_dk.findViewById(R.id.xiaoxi_ll);
                        name2.setText(bean2.getName() + "");
                        bumen.setText("欢迎贵宾VIP来访");
                        synthesizer.speak("欢迎贵宾VIP来访");
                        try {
                            if (bean2.getDisplayPhoto() != null) {
                                Glide.with(MainActivity202.this)
                                        .load(new File(bean2.getDisplayPhoto()))
                                        .apply(myOptions2)
                                        .into(touxiang2);
                            } else {
                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                Glide.with(MainActivity202.this)
                                        .load(drawable)
                                        .apply(myOptions2)
                                        .into(touxiang2);
                            }

                        } catch (FacePassException e) {
                            e.printStackTrace();
                        }

                        rootLayout.addView(view_dk);

                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang2.getLayoutParams();
                        layoutParams2.width = dw / 5;
                        layoutParams2.height = dw / 4;
                        touxiang2.setLayoutParams(layoutParams2);
                        touxiang2.invalidate();

                        //动画
                        SpringSystem springSystem3 = SpringSystem.create();
                        final Spring spring3 = springSystem3.createSpring();
                        //两个参数分别是弹力系数和阻力系数
                        spring3.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 6));
                        // 添加弹簧监听器
                        spring3.addListener(new SimpleSpringListener() {
                            @Override
                            public void onSpringUpdate(Spring spring) {
                                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
                                float value = (float) spring.getCurrentValue();
                                //  Log.d("kkkk", "value:" + value);
                                //基于Y轴的弹簧阻尼动画
                                //	helper.itemView.setTranslationY(value);
                                // 对图片的伸缩动画
                                //float scale = 1f - (value * 0.5f);
                                view_dk.setScaleX(value);
                                view_dk.setScaleY(value);
                                if (value == 1) {

                                    boolean cv = true;
                                    for (int i = 0; i < dibuList.size(); i++) {
                                        if (dibuList.get(i).getId() == bean2.getId()) {
                                            cv = false;
                                            break;
                                        }
                                    }
                                    if (cv) {

                                        final View view_dk = View.inflate(MainActivity202.this, R.layout.shulianbiao_jj, null);
                                        ScreenAdapterTools.getInstance().loadView(view_dk);
                                        TextView name = view_dk.findViewById(R.id.name);
                                        ImageView touxiang = view_dk.findViewById(R.id.touxiang);
                                        name.setText(bean2.getName());
                                        try {
                                        if (bean2.getDisplayPhoto() != null) {
                                                Glide.with(MainActivity202.this)
                                                        .load(new File(bean2.getDisplayPhoto()))
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            } else {
                                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                                Glide.with(MainActivity202.this)
                                                        .load(drawable)
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            }

                                        } catch (FacePassException e) {
                                            e.printStackTrace();
                                        }

                                        shuLiebiao.addView(view_dk, 0);

                                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang.getLayoutParams();
                                        layoutParams2.width = dw / 9;
                                        layoutParams2.topMargin = 30;
                                        layoutParams2.height = dw / 9;
                                        touxiang.setLayoutParams(layoutParams2);
                                        touxiang.invalidate();

                                        dibuList.add(0, bean2);
                                    }
                                    //消失
                                    Message message = Message.obtain();
                                    message.what = 999;
                                    mHandler.sendMessageDelayed(message, 9000);

                                }
                            }
                        });
                        // 设置动画结束值
                        spring3.setEndValue(1f);

                        break;
                    }

                    case 444: {
                        //普通打卡
                        final Subject bean2 = (Subject) msg.obj;
                        yinying.setVisibility(View.VISIBLE);
                        final View view_dk = View.inflate(MainActivity202.this, R.layout.putongyuangong_item_jj, null);
                        ScreenAdapterTools.getInstance().loadView(view_dk);
                        TextView name2 = (TextView) view_dk.findViewById(R.id.name);
                        TextView bumen = (TextView) view_dk.findViewById(R.id.bumen);
                        ImageView touxiang2 = (ImageView) view_dk.findViewById(R.id.touxiang);
                        LinearLayout xiaoxi_ll = (LinearLayout) view_dk.findViewById(R.id.xiaoxi_ll);

                        name2.setText(bean2.getName() + "");
                        bumen.setText(bean2.getDepartmentName() + "");

                        try {
                            if (bean2.getDisplayPhoto() != null) {
                                Glide.with(MainActivity202.this)
                                        .load(new File(bean2.getDisplayPhoto()))
                                        //  .apply(myOptions2)
                                        .into(touxiang2);
                            } else {
                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                Glide.with(MainActivity202.this)
                                        .load(drawable)
                                        // .apply(myOptions2)
                                        .into(touxiang2);
                            }

                        } catch (FacePassException e) {
                            e.printStackTrace();
                        }

                        rootLayout.addView(view_dk);

                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang2.getLayoutParams();
                        layoutParams2.topMargin = -60;
                        layoutParams2.width = dw / 5;
                        layoutParams2.height = dw / 4;
                        touxiang2.setLayoutParams(layoutParams2);
                        touxiang2.invalidate();

                        //动画
                        SpringSystem springSystem3 = SpringSystem.create();
                        final Spring spring3 = springSystem3.createSpring();
                        //两个参数分别是弹力系数和阻力系数
                        spring3.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 6));
                        // 添加弹簧监听器
                        spring3.addListener(new SimpleSpringListener() {
                            @Override
                            public void onSpringUpdate(Spring spring) {
                                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
                                float value = (float) spring.getCurrentValue();
                                //  Log.d("kkkk", "value:" + value);
                                //基于Y轴的弹簧阻尼动画
                                //	helper.itemView.setTranslationY(value);
                                // 对图片的伸缩动画
                                //float scale = 1f - (value * 0.5f);
                                view_dk.setScaleX(value);
                                view_dk.setScaleY(value);
                                if (value == 1) {
                                    boolean cv = true;
                                    for (int i = 0; i < dibuList.size(); i++) {
                                        if (dibuList.get(i).getId() == bean2.getId()) {
                                            cv = false;
                                            break;
                                        }
                                    }
                                    if (cv) {

                                        final View view_dk = View.inflate(MainActivity202.this, R.layout.shulianbiao_jj, null);
                                        ScreenAdapterTools.getInstance().loadView(view_dk);
                                        TextView name = view_dk.findViewById(R.id.name);
                                        ImageView touxiang = view_dk.findViewById(R.id.touxiang);
                                        name.setText(bean2.getName());
                                        try {
                                            if (bean2.getDisplayPhoto() != null) {
                                                Glide.with(MainActivity202.this)
                                                        .load(new File(bean2.getDisplayPhoto()))
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            } else {
                                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                                Glide.with(MainActivity202.this)
                                                        .load(drawable)
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            }

                                        } catch (FacePassException e) {
                                            e.printStackTrace();
                                        }

                                        shuLiebiao.addView(view_dk, 0);

                                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang.getLayoutParams();
                                        layoutParams2.width = dw / 9;
                                        layoutParams2.topMargin = 30;
                                        layoutParams2.height = dw / 9;
                                        touxiang.setLayoutParams(layoutParams2);
                                        touxiang.invalidate();

                                        dibuList.add(0, bean2);

                                    }
                                    //消失
                                    Message message = Message.obtain();
                                    message.what = 999;
                                    mHandler.sendMessageDelayed(message, 9000);

                                }
                            }
                        });
                        // 设置动画结束值
                        spring3.setEndValue(1f);

                        break;
                    }
//                    case 555: {
//                        //打卡下班
//                        final Subject bean2 = (Subject) msg.obj;
//                        final View view_dk = View.inflate(MainActivity201.this, R.layout.dakashangban_item, null);
//                        ScreenAdapterTools.getInstance().loadView(view_dk);
//                        TextView name2 = (TextView) view_dk.findViewById(R.id.name);
//                        TextView daka = (TextView) view_dk.findViewById(R.id.daka);
//                        ImageView touxiang2 = (ImageView) view_dk.findViewById(R.id.touxiang);
//                        RelativeLayout root_rl2 = (RelativeLayout) view_dk.findViewById(R.id.root_rl);
//                        root_rl2.setBackgroundResource(R.drawable.xiaban_bg);
//                        name2.setText(bean2.getName() + "");
//                        daka.setText("下班打卡成功");
//
//                        try {
//                            Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa());
//                            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                            Glide.with(MainActivity201.this)
//                                    .load(drawable)
//                                    .apply(myOptions)
//                                    .into(touxiang2);
//
//                        } catch (FacePassException e) {
//                            e.printStackTrace();
//                        }
//
//                        shuLiebiao.addView(view_dk);
//                        toumingbeijing.setVisibility(View.VISIBLE);
//
//                        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) root_rl2.getLayoutParams();
//                        layoutParams2.width = dw - 160;
//                        layoutParams2.topMargin = 20;
//                        layoutParams2.leftMargin = 80;
//                        layoutParams2.height = (dh - 200) / 4 - 52;
//                        root_rl2.setLayoutParams(layoutParams2);
//                        root_rl2.invalidate();
//
//                        //动画
//                        SpringSystem springSystem3 = SpringSystem.create();
//                        final Spring spring3 = springSystem3.createSpring();
//                        //两个参数分别是弹力系数和阻力系数
//                        spring3.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 6));
//                        // 添加弹簧监听器
//                        spring3.addListener(new SimpleSpringListener() {
//                            @Override
//                            public void onSpringUpdate(Spring spring) {
//                                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
//                                float value = (float) spring.getCurrentValue();
//                                //  Log.d("kkkk", "value:" + value);
//                                //基于Y轴的弹簧阻尼动画
//                                //	helper.itemView.setTranslationY(value);
//                                // 对图片的伸缩动画
//                                //float scale = 1f - (value * 0.5f);
//                                view_dk.setScaleX(value);
//                                view_dk.setScaleY(value);
//                                if (value == 1) {
//                                    scrollView.smoothScrollBy(0, 600);
//                                    bean2.setShijian(DateUtils.timesTwodian(System.currentTimeMillis() + "") + "-" + DateUtils.timeMinute(System.currentTimeMillis() + ""));
//                                    dibuList.add(0, bean2);
//                                    diBuAdapter.notifyDataSetChanged();
//                                    if (dibuList.size() > 8) {
//                                        int si = dibuList.size() - 1;
//                                        dibuList.remove(si);
//                                        diBuAdapter.notifyItemRemoved(si);
//                                        //adapter.notifyItemChanged(1);
//                                        //adapter.notifyItemRangeChanged(1,tanchuangList.size());
//                                        //adapter.notifyDataSetChanged();
//                                        gridLayoutManager.scrollToPosition(0);
//                                    }
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            SystemClock.sleep(9000);
//                                            if (shuLiebiao.getChildCount() > 0) {
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        shuLiebiao.removeViewAt(0);
//                                                        if (shuLiebiao.getChildCount() == 0) {
//                                                            toumingbeijing.setVisibility(View.GONE);
//                                                        }
//                                                    }
//                                                });
//
//                                            }
//                                        }
//                                    }).start();
//
//                                }
//                            }
//                        });
//                        // 设置动画结束值
//                        spring3.setEndValue(1f);
//
//                        break;
//                    }
                    case 666: {
                        //访客
                        final Subject bean2 = (Subject) msg.obj;
                        yinying.setVisibility(View.VISIBLE);
                        final View view_dk = View.inflate(MainActivity202.this, R.layout.putongfangke_item_jj, null);
                        ScreenAdapterTools.getInstance().loadView(view_dk);
                        TextView name2 = (TextView) view_dk.findViewById(R.id.name);
                        TextView daka = (TextView) view_dk.findViewById(R.id.bumen);
                        ImageView touxiang2 = (ImageView) view_dk.findViewById(R.id.touxiang);
                        LinearLayout xiaoxi_ll = (LinearLayout) view_dk.findViewById(R.id.xiaoxi_ll);
                        name2.setText(bean2.getName() + "");
                        daka.setText("欢迎你的来访");

                        try {
                            if (bean2.getDisplayPhoto() != null) {
                                Glide.with(MainActivity202.this)
                                        .load(new File(bean2.getDisplayPhoto()))
                                       // .apply(myOptions2)
                                        .into(touxiang2);
                            } else {
                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                Glide.with(MainActivity202.this)
                                        .load(drawable)
                                       // .apply(myOptions2)
                                        .into(touxiang2);
                            }

                        } catch (FacePassException e) {
                            e.printStackTrace();
                        }

                        rootLayout.addView(view_dk);

                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang2.getLayoutParams();
                        layoutParams2.width = dw / 5;
                        layoutParams2.height = dw / 4;
                        touxiang2.setLayoutParams(layoutParams2);
                        touxiang2.invalidate();

                        //动画
                        SpringSystem springSystem3 = SpringSystem.create();
                        final Spring spring3 = springSystem3.createSpring();
                        //两个参数分别是弹力系数和阻力系数
                        spring3.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 6));
                        // 添加弹簧监听器
                        spring3.addListener(new SimpleSpringListener() {
                            @Override
                            public void onSpringUpdate(Spring spring) {
                                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
                                float value = (float) spring.getCurrentValue();
                                //  Log.d("kkkk", "value:" + value);
                                //基于Y轴的弹簧阻尼动画
                                //	helper.itemView.setTranslationY(value);
                                // 对图片的伸缩动画
                                //float scale = 1f - (value * 0.5f);
                                view_dk.setScaleX(value);
                                view_dk.setScaleY(value);
                                if (value == 1) {
                                    boolean cv = true;
                                    for (int i = 0; i < dibuList.size(); i++) {
                                        if (dibuList.get(i).getId() == bean2.getId()) {
                                            cv = false;
                                            break;
                                        }
                                    }
                                    if (cv) {
                                        final View view_dk = View.inflate(MainActivity202.this, R.layout.shulianbiao_jj, null);
                                        ScreenAdapterTools.getInstance().loadView(view_dk);
                                        TextView name = view_dk.findViewById(R.id.name);
                                        ImageView touxiang = view_dk.findViewById(R.id.touxiang);

                                        name.setText(bean2.getName());
                                        try {
                                            if (bean2.getDisplayPhoto() != null) {
                                                Glide.with(MainActivity202.this)
                                                        .load(new File(bean2.getDisplayPhoto()))
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            } else {
                                                Bitmap bitmap = mFacePassHandler.getFaceImage(bean2.getTeZhengMa().getBytes());
                                                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                                                Glide.with(MainActivity202.this)
                                                        .load(drawable)
                                                        .apply(GlideUtils.getRequestOptions())
                                                        .into(touxiang);
                                            }

                                        } catch (FacePassException e) {
                                            e.printStackTrace();
                                        }

                                        shuLiebiao.addView(view_dk, 0);

                                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang.getLayoutParams();
                                        layoutParams2.width = dw / 9;
                                        layoutParams2.topMargin = 30;
                                        layoutParams2.height = dw / 9;
                                        touxiang.setLayoutParams(layoutParams2);
                                        touxiang.invalidate();

                                        dibuList.add(0, bean2);

                                    }
                                    //消失
                                    Message message = Message.obtain();
                                    message.what = 999;
                                    mHandler.sendMessageDelayed(message, 9000);

                                }
                            }
                        });
                        // 设置动画结束值
                        spring3.setEndValue(1f);

                        break;
                    }
                    case 999: {

                        if (rootLayout.getChildCount() > 0) {
                            rootLayout.removeViewAt(0);
                            if (dibuList.size() > 9) {
                                dibuList.remove(9);
                            }
                            if (shuLiebiao.getChildCount()>9){
                                shuLiebiao.removeViewAt(shuLiebiao.getChildCount()-1);
                            }
                        }
                        if (rootLayout.getChildCount()<=0){
                            yinying.setVisibility(View.GONE);
                        }

                        break;
                    }
                    case -100:{
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Glide.get(MainActivity202.this).clearDiskCache();
                            }
                        }).start();
                        //陌生人
                        final MSRBean bean2 = (MSRBean) msg.obj;
                        yinying.setVisibility(View.VISIBLE);
                        final View view_dk = View.inflate(MainActivity202.this, R.layout.moshengren_item_jj, null);
                        ScreenAdapterTools.getInstance().loadView(view_dk);
                        TextView name2 = (TextView) view_dk.findViewById(R.id.name);
                        ImageView touxiang2 = (ImageView) view_dk.findViewById(R.id.touxiang);
                        TextView xingbie = (TextView) view_dk.findViewById(R.id.xingbie);
                        TextView nianling = (TextView) view_dk.findViewById(R.id.nianling);
                        name2.setText("您好陌生人");
                        xingbie.setText("性别:"+bean2.getSex());
                        nianling.setText("年龄:"+bean2.getAge());


                        Glide.get(MainActivity202.this).clearMemory();
                        Glide.with(MainActivity202.this)
                                .load(bean2.getBitmap())
                                 .apply(myOptions2)
                                .into(touxiang2);

                        rootLayout.addView(view_dk);

                        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang2.getLayoutParams();
                        layoutParams2.width = dw / 5;
                        layoutParams2.height = dw / 4;
                        touxiang2.setLayoutParams(layoutParams2);
                        touxiang2.invalidate();

                        //动画
                        SpringSystem springSystem3 = SpringSystem.create();
                        final Spring spring3 = springSystem3.createSpring();
                        //两个参数分别是弹力系数和阻力系数
                        spring3.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(80, 6));
                        // 添加弹簧监听器
                        spring3.addListener(new SimpleSpringListener() {
                            @Override
                            public void onSpringUpdate(Spring spring) {
                                // value是一个符合弹力变化的一个数，我们根据value可以做出弹簧动画
                                float value = (float) spring.getCurrentValue();
                                //  Log.d("kkkk", "value:" + value);
                                //基于Y轴的弹簧阻尼动画
                                //	helper.itemView.setTranslationY(value);
                                // 对图片的伸缩动画
                                //float scale = 1f - (value * 0.5f);
                                view_dk.setScaleX(value);
                                view_dk.setScaleY(value);
                                if (value == 1) {

                                    dibuList.add(0,new Subject(System.currentTimeMillis()));
                                    final View view_dk = View.inflate(MainActivity202.this, R.layout.shulianbiao_jj, null);
                                    ScreenAdapterTools.getInstance().loadView(view_dk);
                                    TextView name = view_dk.findViewById(R.id.name);
                                    ImageView touxiang = view_dk.findViewById(R.id.touxiang);
                                    name.setText("陌生人");
                                    Glide.with(MainActivity202.this)
                                            .load(bean2.getBitmap())
                                            .apply(GlideUtils.getRequestOptions())
                                            .into(touxiang);

                                    shuLiebiao.addView(view_dk, 0);

                                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) touxiang.getLayoutParams();
                                    layoutParams2.width = dw / 9;
                                    layoutParams2.topMargin = 30;
                                    layoutParams2.height = dw / 9;
                                    touxiang.setLayoutParams(layoutParams2);
                                    touxiang.invalidate();

                                    //消失
                                    Message message = Message.obtain();
                                    message.what = 999;
                                    mHandler.sendMessageDelayed(message, 9000);

                                }
                            }
                        });
                        // 设置动画结束值
                        spring3.setEndValue(1f);

                        break;
                    }


                }
                return false;
            }
        });

        isSC = true;
    }


    @Override
    protected void onResume() {
        initToast();
        /* 打开相机 */
        if (hasPermission()) {
            manager.open(getWindowManager(), cameraFacingFront, cameraWidth, cameraHeight);
        }
        adaptFrameLayout();
        super.onResume();
    }


    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (mFacePassHandler == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, cameraRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);

    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {

                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    FacePassDetectionResult detectionResult = null;
                    detectionResult = mFacePassHandler.feedFrame(image);

                    if (detectionResult != null && detectionResult.faceList.length > 0) {
                        showFacePassFace(detectionResult.faceList,image);
                    }
                    /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                    if (detectionResult != null && detectionResult.message.length != 0) {
                        mDetectResultQueue.offer(detectionResult);
                        // Log.d(DEBUG_TAG, "1 mDetectResultQueue.size = " + mDetectResultQueue.size());
                    }
                    //     }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isIterrupt = true;
            super.interrupt();
        }
    }

    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {

                    FacePassDetectionResult detectionResult = mDetectResultQueue.take();

                    FacePassRecognitionResult[] recognizeResult = mFacePassHandler.recognize(group_name, detectionResult.message);
                 //   Log.d("RecognizeThread", "识别线程");
                    if (recognizeResult != null && recognizeResult.length > 0) {
                        for (FacePassRecognitionResult result : recognizeResult) {
                            //String faceToken = new String(result.faceToken);
                            if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                //识别的
                                //  getFaceImageByFaceToken(result.trackId, faceToken);

                                Subject subject = subjectBox.query().equal(Subject_.teZhengMa, new String(result.faceToken)).build().findUnique();
                                if (subject != null) {
                                    subject.setPeopleType("员工");
                                    linkedBlockingQueue.offer(subject);
                                    link_shangchuanjilu(subject);
//                                    Subject subject1 = new Subject();
//                                    subject1.setId(12345);
//                                    subject1.setName("测试");
//                                    subject1.setTeZhengMa(result.faceToken);
//                                    subject1.setPeopleType("员工");
//                                    subjectBox.put(subject1);

//                                    if (isOne) {
//                                        isOne = false;
//                                        try {
//                                            Subject beanSB = linkedBlockingQueue.poll(10, TimeUnit.MILLISECONDS);
//                                            Log.d("WebsocketPushMsg", "消费掉第一个:" + beanSB.getName());
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//
//                                        Message message2 = Message.obtain();
//                                        message2.what = 111;
//                                        message2.obj = subject;
//                                        mHandler.sendMessage(message2);
//
//
//                                    }
                                } else {
                                    EventBus.getDefault().post("没有查询到人员信息");
                                    Subject subject1 = new Subject();
                                    subject1.setId(12345);
                                    subject1.setName("测试");
                                    subject1.setTeZhengMa(new String(result.faceToken));
                                    subject1.setPeopleType("白名单");
                                    subjectBox.put(subject1);
                                }

                            } else {

                                //未识别的
                                // 防止concurrentHashMap 数据过多 ,超过一定数据 删除没用的

                                if (concurrentHashMap.size()>10){
                                    concurrentHashMap.clear();
                                }
                                if (concurrentHashMap.get(result.trackId)==null){
                                    //找不到新增
                                    concurrentHashMap.put(result.trackId,1);
                                }else {
                                    //找到了 把value 加1
                                    concurrentHashMap.put(result.trackId,(concurrentHashMap.get(result.trackId))+1);
                                }
                                //判断次数超过3次
                                if (concurrentHashMap.get(result.trackId)==3){
                                    tID=result.trackId;
                                    isLink=true;

                                 //   Log.d("RecognizeThread", "入库"+tID);
                                }

                            }

                        }
                    }

                } catch (InterruptedException | FacePassException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }

    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    //有动画 ，延迟到一秒一次
                    SystemClock.sleep(1100);
                    Subject subject = linkedBlockingQueue.take();
                    if (subject.getPeopleType() != null) {
                        switch (subject.getPeopleType()) {
                            case "员工":
//                                switch (subject.getDaka()) {
//                                    case 0: {
//                                        //未打卡过 //弹打卡的窗
//                                        Message message2 = Message.obtain();
//                                        message2.what = 333;
//                                        message2.obj = subject;
//                                        mHandler.sendMessage(message2);
//
//                                        //更新一次打卡
//                                        subject.setDaka(1);
//                                        subjectBox.put(subject);
//
//                                        break;
//                                    }
//                                    case 1: {
//                                        //打卡了一次,如果当前时间大于晚上6点 //弹普通的窗
//                                        String xs[] = DateUtils.xiaoshi(System.currentTimeMillis() + "").split("-");
//                                        if (Integer.valueOf(xs[0]) >= 18 && Integer.valueOf(xs[1]) >= 0) {
//                                            //大于下午6点就是 下班
//                                            Message message2 = Message.obtain();
//                                            message2.what = 555;
//                                            message2.obj = subject;
//                                            mHandler.sendMessage(message2);
//                                            //下班可以一直打卡 ，后台取最后一次的
//
//                                        } else {
                                //普通打卡
                                Message messagey = Message.obtain();
                                messagey.what = 444;
                                messagey.obj = subject;
                                mHandler.sendMessage(messagey);
//                                        }
//
//                                        break;
//                                    }
//                                    case 2:
//                                        //第二次 下班(或者中午下班)
//
//
//                                        break;
//                                    case 3:
//                                        //第三次 下午上班
//
//
//                                        break;
//                                    case 4:
//                                        //第四次 下午下班
//

                                break;

                            case "普通访客": {
                                //普通访客
                                Message message2 = Message.obtain();
                                message2.what = 666;
                                message2.obj = subject;
                                mHandler.sendMessage(message2);

                                break;
                            }
                            case "白名单":
                                //vip

                                Message message2 = Message.obtain();
                                message2.what = 111;
                                message2.obj = subject;
                                mHandler.sendMessage(message2);

                                break;
                            case "黑名单":

                                break;
                            default:
                                EventBus.getDefault().post("没有对应身份类型,无法弹窗");

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isRing = true;
            super.interrupt();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        shipingView.pause();
    }


//    private void showRecognizeResult(final long trackId, final float searchScore, final float livenessScore, final boolean isRecognizeOK) {
//        mAndroidHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                faceEndTextView.append("ID = " + trackId + (isRecognizeOK ? "识别成功" : "识别失败") + "\n");
//                faceEndTextView.append("识别分 = " + searchScore + "\n");
//                faceEndTextView.append("活体分 = " + livenessScore + "\n");
//                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
//
//    }

    /* 判断程序是否有所需权限 android22以上需要自申请权限 */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /* 请求程序所需权限 */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(Permission, PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    granted = false;
            }
            if (!granted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (!shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
                            || !shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
                            || !shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
                        Toast.makeText(getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
                    }
            } else {

            //    FacePassHandler.getAuth(authIP, apiKey, apiSecret);
                FacePassHandler.initSDK(getApplicationContext());
                Log.d("MainActivity2013", FacePassHandler.getVersion());
            }
        }
    }

    private void adaptFrameLayout() {
        SettingVar.isButtonInvisible = false;
        SettingVar.iscameraNeedConfig = false;
    }

    private void initToast() {
        SettingVar.isButtonInvisible = false;
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        int windowRotation = ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
        if (windowRotation == 0) {
            cameraRotation = FacePassImageRotation.DEG90;
        } else if (windowRotation == 90) {
            cameraRotation = FacePassImageRotation.DEG0;
        } else if (windowRotation == 270) {
            cameraRotation = FacePassImageRotation.DEG180;
        } else {
            cameraRotation = FacePassImageRotation.DEG270;
        }
//        Log.i(DEBUG_TAG, "cameraRation: " + cameraRotation);
        cameraFacingFront = true;
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.isCross = preferences.getBoolean("isCross", SettingVar.isCross);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        if (SettingVar.isSettingAvailable) {
            cameraRotation = SettingVar.faceRotation;
            cameraFacingFront = SettingVar.cameraFacingFront;
        }


        //  Log.i("orientation", String.valueOf(windowRotation));
        final int mCurrentOrientation = getResources().getConfiguration().orientation;

        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = 1;
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = 0;
        }
        setContentView(R.layout.activity_main202);

        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ButterKnife.bind(this);
        AssetManager mgr = getAssets();
        //Univers LT 57 Condensed
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
        Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        Typeface tf3 = Typeface.createFromAsset(mgr, "fonts/kai.ttf");
        // String riqi2 = DateUtils.timesTwo(System.currentTimeMillis() + "") + "   " + DateUtils.getWeek(System.currentTimeMillis());
        //  riqi.setTypeface(tf);
        //    riqi.setText(riqi2);
        xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
        riqi.setText(DateUtils.timesTwodian(System.currentTimeMillis() + ""));
        xiaoshi.setTypeface(tf);
        xiaoshi.setText(DateUtils.timeMinute(System.currentTimeMillis() + ""));
        TableLayout mHudView = findViewById(R.id.hud_view);
        shipingView = findViewById(R.id.ijkplayview);
        shipingView.setVisibility(View.GONE);
        //轮播图
        lunboview.setAdapter(new TestNomalAdapter());
        //背景
        daBg.setBackgroundResource(R.color.dabg);

        scrollView.setSmoothScrollingEnabled(true);
        marqueeView = (MarqueeView) findViewById(R.id.marqueeView);
        List<String> info = new ArrayList<>();
        info.add("大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦" +
                "大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦大家好我是孙福生哦哦");
        //70个字
        info.add("明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会" +
                "明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会明天上午12点开会");
        marqueeView.startWithList(info);

        // 在代码里设置自己的动画
        marqueeView.startWithList(info, R.anim.anim_bottom_in, R.anim.anim_top_out);

//        shipingView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                long curTime = System.currentTimeMillis();
//                long durTime = curTime - mLastClickTime;
//                mLastClickTime = curTime;
//                if (durTime < CLICK_INTERVAL) {
//                    ++mSecretNumber;
//                    if (mSecretNumber == 3) {
//                        dialog=new XiuGaiGaoKuanDialog(MainActivity.this,MainActivity.this);
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.setContents("修改视频的宽高",(shipingView.getRight()-shipingView.getLeft())+"",(shipingView.getBottom()-shipingView.getTop())+"",2);
//                        dialog.setOnQueRenListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                } else {
//                    mSecretNumber = 0;
//                }
//            }
//        });
//
//

        shipingView.setHudView(mHudView); //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
        shipingView.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "laowang.mp4");
        // shipingView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
        shipingView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                //  shipingView.start();
            }
        });


//        shipingView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//                TastyToast.makeText(MainActivity201.this, "播放视频失败", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
//                return false;
//            }
//        });

        //  shipingView.start();


//        dbg_view=findViewById(R.id.dabg);
//        dbg_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                long curTime = System.currentTimeMillis();
//                long durTime = curTime - mLastClickTime;
//                mLastClickTime = curTime;
//                if (durTime < CLICK_INTERVAL) {
//                    ++mSecretNumber;
//                    if (mSecretNumber == 3) {
//                        dialog=new XiuGaiGaoKuanDialog(MainActivity.this,MainActivity.this);
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.setContents("修改背景宽高",(dbg_view.getRight()-dbg_view.getLeft())+"",(dbg_view.getBottom()-dbg_view.getTop())+"",1);
//                        dialog.setOnQueRenListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                } else {
//                    mSecretNumber = 0;
//                }
//            }
//        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;


        /* 初始化界面 */
        //  faceView = (FaceView) this.findViewById(R.id.fcview);
        SettingVar.cameraSettingOk = false;

        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);


        if (todayBean != null) {
            //更新天气界面
            //    wendu.setTypeface(tf2);
            //  tianqi.setTypeface(tf2);
            //  fengli.setTypeface(tf2);
            //  ziwaixian.setTypeface(tf2);
            //  shidu.setTypeface(tf2);
            //  jianyi.setTypeface(tf3);

            wendu.setText(todayBean.getTemperature());
            tianqi.setText(todayBean.getWeather());


        }

    }


    //修改监听
    @Override
    public void setKG(final int k, final int g, int type) {
        switch (type) {
            case 1:
                //大背景的
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //     RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) dbg_view.getLayoutParams();
//                                int[] location = new  int[2] ;
//                                dbg_view.getLocationOnScreen(location);
                        //   int L = dbg_view.getLeft();
                        //  int R = dbg_view.getRight();
                        //  int T = dbg_view.getTop();
                        //  int B = dbg_view.getBottom();
                        //   params.topMargin = T;
                        //   params.leftMargin = L;
                        //   params.height = ((B - T) + g) < 0 ? 0 : (B - T) + g;
                        //  params.width = ((R - L) + k) < 0 ? 0 : (R - L) + k;
                        //  dbg_view.setLayoutParams(params);//将设置好的布局参数应用到控件中
                        //  dialog.setContents("修改背景宽高", (dbg_view.getRight() - dbg_view.getLeft()) + "", (dbg_view.getBottom() - dbg_view.getTop()) + "", 1);

                    }
                });


                break;
            case 2:
                //视频的
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) shipingView.getLayoutParams();
//                                int[] location = new  int[2] ;
//                                dbg_view.getLocationOnScreen(location);
                        int L = shipingView.getLeft();
                        int R = shipingView.getRight();
                        int T = shipingView.getTop();
                        int B = shipingView.getBottom();
                        params.topMargin = T;
                        params.leftMargin = L;
                        params.height = ((B - T) + g) < 0 ? 0 : (B - T) + g;
                        params.width = ((R - L) + k) < 0 ? 0 : (R - L) + k;
                        shipingView.setLayoutParams(params);//将设置好的布局参数应用到控件中
                        dialog.setContents("修改视频宽高", (shipingView.getRight() - shipingView.getLeft()) + "", (shipingView.getBottom() - shipingView.getTop()) + "", 2);

                    }
                });

                break;
            case 3:

                break;
            case 4:

                break;
        }

    }


    @Override
    protected void onStop() {
        SettingVar.isButtonInvisible = false;
        shuLiebiao.removeAllViews();
        rootLayout.removeAllViews();
        mToastBlockQueue.clear();
        mDetectResultQueue.clear();
        mFeedFrameQueue.clear();
        linkedBlockingQueue.clear();
        if (manager != null) {
            manager.release();
        }
        marqueeView.stopFlipping();
        dibuList.clear();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        //faceView.clear();
        // faceView.invalidate();
        //  if (shipingView!=null)
        // shipingView.start();
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        marqueeView.startFlipping();
    }

    @Override
    protected void onDestroy() {
        lunboview.removeAllViews();
        if (mToastBlockQueue != null) {
            mToastBlockQueue.clear();
        }
        if (linkedBlockingQueue != null) {
            linkedBlockingQueue.clear();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (mFeedFrameThread != null) {
            mFeedFrameThread.isIterrupt = true;
            mFeedFrameThread.interrupt();
        }

        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }

        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }

        shipingView.release(true);
        unregisterReceiver(timeChangeReceiver);
        EventBus.getDefault().unregister(this);//解除订阅
        if (manager != null) {
            manager.release();
        }
        if (mFacePassHandler != null) {
            mFacePassHandler.release();
        }
        if (synthesizer != null)
            synthesizer.release();


        super.onDestroy();
    }


    private void showFacePassFace(FacePassFace[] detectResult, final FacePassImage image) {

        for (FacePassFace face : detectResult) {
            boolean mirror = cameraFacingFront; /* 前摄像头时mirror为true */
            Matrix mat = new Matrix();
            int w = cameraView.getMeasuredWidth();
            int h = cameraView.getMeasuredHeight();

            int cameraHeight = manager.getCameraheight();
            int cameraWidth = manager.getCameraWidth();

            float left = 0;
            float top = 0;
            float right = 0;
            float bottom = 0;
            switch (cameraRotation) {
                case 0:
                    left = face.rect.left;
                    top = face.rect.top;
                    right = face.rect.right;
                    bottom = face.rect.bottom;
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraWidth : 0f, 0f);
                    mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                    break;
                case 90:
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                    mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                    left = face.rect.top;
                    top = cameraWidth - face.rect.right;
                    right = face.rect.bottom;
                    bottom = cameraWidth - face.rect.left;
                    break;
                case 180:
                    mat.setScale(1, mirror ? -1 : 1);
                    mat.postTranslate(0f, mirror ? (float) cameraHeight : 0f);
                    mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                    left = face.rect.right;
                    top = face.rect.bottom;
                    right = face.rect.left;
                    bottom = face.rect.top;
                    break;
                case 270:
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                    mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                    left = cameraHeight - face.rect.bottom;
                    top = face.rect.left;
                    right = cameraHeight - face.rect.top;
                    bottom = face.rect.right;
            }

            RectF drect = new RectF();
            RectF srect = new RectF(left, top, right, bottom);
            mat.mapRect(drect, srect);

            //头像加宽加高点
            RectF srect2 = new RectF(face.rect.left - 40 < 0 ? 0 : face.rect.left - 40, face.rect.top - 100 < 0 ? 0 : face.rect.top - 100,
                    face.rect.right + 40 > image.width ? image.width : face.rect.right + 40, face.rect.bottom + 100 > image.height ? image.height : face.rect.bottom + 100);


            float pitch = face.pose.pitch;
            float roll = face.pose.roll;
            float yaw = face.pose.yaw;

            if (pitch < 25 && pitch > -25 && roll < 25 && roll > -25 && yaw < 25 && yaw > -25 && face.blur < 0.4) {
                try {
                    if (tID==face.trackId && isLink) {  //入库成功后将 tID=-1;
                        isLink = false;
                        tID=-1;
                        //获取图片
                        YuvImage image2 = new YuvImage(image.image, ImageFormat.NV21, image.width, image.height, null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        image2.compressToJpeg(new Rect(0, 0, image.width, image.height), 100, stream);
                        final Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                        stream.close();

                        int x1, y1, x2, y2 = 0;
                        x1 = (int) srect2.left;
                        y1 = (int) srect2.top;
                        //是宽高，不是坐标
                        x2 = (srect2.left + (srect2.right - srect2.left)) > image.width ? (int) (image.width - srect2.left) : (int) (srect2.right - srect2.left);
                        y2 = (srect2.top + (srect2.bottom - srect2.top)) > image.height ? (int) (image.height - srect2.top) : (int) (srect2.bottom - srect2.top);
                        //截取单个人头像
                        final Bitmap bitmap = Bitmap.createBitmap(bmp, x1, y1, x2, y2);

                        MSRBean b=new MSRBean();
                        b.setAge(face.age);
                        String sex="未知";

                        switch (face.gender) {
                            case 0:
                                sex="男";
                                break;
                            case 1:
                                sex="女";
                                break;
                            default:
                                sex="未知";
                        }
                        b.setSex(sex);
                        b.setBitmap(bitmabToBytes2(bitmap));
                        Message message= Message.obtain();
                        message.obj=b;
                        message.what=-100;
                        mHandler.sendMessage(message);

                    }


                } catch (Exception ex) {
                    isLink = true;
                    Log.e("Sys", "Error:" + ex.getMessage());
                }

            }
        }


    }
    private static final int REQUEST_CODE_CHOOSE_PICK = 1;


    //图片转为二进制数据
    public byte[] bitmabToBytes2(Bitmap bitmap) {
        //将图片转化为位图
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            return baos.toByteArray();
        } catch (Exception ignored) {
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //从相册选取照片后读取地址
            case REQUEST_CODE_CHOOSE_PICK:
                if (resultCode == RESULT_OK) {
                    String path = "";
                    Uri uri = data.getData();
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    if (cursor != null) {
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                    }
                    if (!TextUtils.isEmpty(path) && "file".equalsIgnoreCase(uri.getScheme())) {
                        path = uri.getPath();
                    }
                    if (TextUtils.isEmpty(path)) {
                        try {
                            path = FileUtil.getPath(getApplicationContext(), uri);
                        } catch (Exception e) {
                        }
                    }
                    if (TextUtils.isEmpty(path)) {
                        toast("图片选取失败！");
                        return;
                    }

                }
                break;
        }
    }



    private void toast(String msg) {
        Toast.makeText(MainActivity202.this, msg, Toast.LENGTH_SHORT).show();
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                startActivity(new Intent(MainActivity202.this, SheZhiActivity.class));
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Log.d("MainActivity", "ev.getPointerCount()1:" + ev.getPointerCount());
        Log.d("MainActivity", "ev.getAction()1:" + ev.getAction());

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isAnXia = true;
        }
        if (isAnXia) {
            if (ev.getPointerCount() == 4) {
                isAnXia = false;
                startActivity(new Intent(MainActivity202.this, SheZhiActivity.class));
                finish();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        // 设置初始化参数
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler); // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        Map<String, String> params = getParams();
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程

    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        params.put(SpeechSynthesizer.PARAM_SPEAKER, baoCunBean.getBoyingren() + ""); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_VOLUME, "8"); // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, baoCunBean.getYusu() + "");// 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, baoCunBean.getYudiao() + "");// 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);         // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());

        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            // toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if (event.equals("mFacePassHandler")) {
            mFacePassHandler = MyApplication.myApplication.getFacePassHandler();
            // diBuAdapter = new DiBuAdapter(dibuList, MainActivity202.this, dibuliebiao.getWidth(), dibuliebiao.getHeight(), mFacePassHandler);
            //  dibuliebiao.setLayoutManager(gridLayoutManager);
            //  dibuliebiao.setAdapter(diBuAdapter);
            return;
        }
        Toast tastyToast = TastyToast.makeText(MainActivity202.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:

                    AssetManager mgr = getAssets();
                    //Univers LT 57 Condensed
                    Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
                    Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
                    Typeface tf3 = Typeface.createFromAsset(mgr, "fonts/kai.ttf");
                    String riqi2 = DateUtils.timesTwo(System.currentTimeMillis() + "") + "   " + DateUtils.getWeek(System.currentTimeMillis());
                    //  riqi.setTypeface(tf);
                    //  riqi.setText(riqi2);
                    xiaoshi.setTypeface(tf);

                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("06") && xiaoshiss.split(":")[1].equals("30")) {

                        final List<BenDiJiLuBean> benDiJiLuBeans = benDiJiLuBeanBox.getAll();
                        final int size = benDiJiLuBeans.size();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < size; i++) {
                                    while (isSC) {
                                        isSC = false;
                                        link_shangchuanjilu2(benDiJiLuBeans.get(i));
                                    }

                                }

                            }
                        }).start();

                    }
                    xiaoshi.setText(xiaoshiss);

                    Date date = new Date();
                    date.setTime(System.currentTimeMillis());
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int t = calendar.get(Calendar.HOUR_OF_DAY);

                    //每过一分钟 触发
                    if (baoCunBean != null && baoCunBean.getDangqianShiJian() != null && !baoCunBean.getDangqianShiJian().equals(DateUtils.timesTwo(System.currentTimeMillis() + "")) && t >= 6) {

                        //一天请求一次
                        try {
                            if (baoCunBean.getDangqianChengShi2() == null) {
                                Toast tastyToast = TastyToast.makeText(MainActivity202.this, "获取天气失败,没有设置当前城市", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                                tastyToast.show();
                                return;
                            }
                            Log.d("TimeChangeReceiver", baoCunBean.getDangqianChengShi());
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Request.Builder requestBuilder = new Request.Builder()
                                    .get()
                                    .url("http://v.juhe.cn/weather/index?format=1&cityname=" + baoCunBean.getDangqianChengShi() + "&key=356bf690a50036a5cfc37d54dc6e8319");
                            // step 3：创建 Call 对象
                            Call call = okHttpClient.newCall(requestBuilder.build());
                            //step 4: 开始异步请求
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.d("AllConnects", "请求失败" + e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    Log.d("AllConnects", "请求成功" + call.request().toString());
                                    //获得返回体
                                    try {

                                        ResponseBody body = response.body();
                                        String ss = body.string().trim();
                                        Log.d("AllConnects", "天气" + ss);
                                        JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
                                        Gson gson = new Gson();
                                        final TianQiBean renShu = gson.fromJson(jsonObject, TianQiBean.class);
                                        final TodayBean todayBean = new TodayBean();
                                        todayBean.setId(123456L);
                                        todayBean.setTemperature(renShu.getResult().getToday().getTemperature());//温度
                                        todayBean.setWeather(renShu.getResult().getToday().getWeather()); //天气
                                        todayBean.setWind(renShu.getResult().getToday().getWind()); //风力
                                        todayBean.setUv_index(renShu.getResult().getToday().getUv_index()); //紫外线
                                        todayBean.setHumidity(renShu.getResult().getSk().getHumidity());//湿度
                                        todayBean.setDressing_advice(renShu.getResult().getToday().getDressing_advice());

                                        todayBeanBox.put(todayBean);
                                        baoCunBean.setDangqianShiJian(DateUtils.timesTwo(System.currentTimeMillis() + ""));
                                        baoCunBeanDao.put(baoCunBean);
                                        //更新界面
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                AssetManager mgr = getAssets();
                                                //Univers LT 57 Condensed
                                                Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
                                                Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
                                                String riqi2 = DateUtils.timesTwo(System.currentTimeMillis() + "") + "   " + DateUtils.getWeek(System.currentTimeMillis());

                                                //   wendu.setTypeface(tf2);
                                                //  tianqi.setTypeface(tf2);
                                                //  fengli.setTypeface(tf2);
                                                //  ziwaixian.setTypeface(tf2);
                                                // shidu.setTypeface(tf2);
//                                                jianyi.setTypeface(tf2);

                                                xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
                                                riqi.setText(DateUtils.timesTwodian(System.currentTimeMillis() + ""));

                                                wendu.setText(todayBean.getTemperature());
                                                tianqi.setText(todayBean.getWeather());


                                            }
                                        });
                                        //把所有人员的打卡信息重置
                                        List<Subject> subjectList = subjectBox.getAll();
                                        for (Subject s : subjectList) {
                                            s.setDaka(0);
                                            subjectBox.put(s);
                                        }

                                    } catch (Exception e) {
                                        Log.d("WebsocketPushMsg", e.getMessage() + "ttttt");
                                    }

                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast tastyToast = TastyToast.makeText(MainActivity202.this, "获取天气失败,没有设置当前城市", TastyToast.LENGTH_LONG, TastyToast.INFO);
                            tastyToast.setGravity(Gravity.CENTER, 0, 0);
                            tastyToast.show();
                            return;
                        }

                    }
                    //  Toast.makeText(context, "1 min passed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    //轮播适配器
    private class TestNomalAdapter extends StaticPagerAdapter {
        private int[] imgs = {
                R.drawable.dbg_1,
                R.drawable.ceshi,
                R.drawable.ceshi3,
        };

        @Override
        public View getView(ViewGroup container, int position) {
            ImageView view = new ImageView(container.getContext());
            view.setImageResource(imgs[position]);
            view.setScaleType(ImageView.ScaleType.FIT_XY);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            return view;
        }

        @Override
        public int getCount() {
            return imgs.length;
        }
    }


    //上传识别记录
    private void link_shangchuanjilu(final Subject subject) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(100000, TimeUnit.MILLISECONDS)
                .connectTimeout(100000, TimeUnit.MILLISECONDS)
                .readTimeout(100000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                .retryOnConnectionFailure(true)
                .build();
        RequestBody body = null;

        body = new FormBody.Builder()
                //.add("name", subject.getName()) //
                //.add("companyId", subject.getCompanyId()+"") //公司di
                //.add("companyName",subject.getCompanyName()+"") //公司名称
                //.add("storeId", subject.getStoreId()+"") //门店id
                //.add("storeName", subject.getStoreName()+"") //门店名称
                .add("subjectId", subject.getId() + "") //员工ID
                .add("subjectType", subject.getPeopleType()) //人员类型
                // .add("department", subject.getPosition()+"") //部门
                .add("discernPlace", FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this))//识别地点
                // .add("discernAvatar",  "") //头像
                .add("identificationTime", DateUtils.time(System.currentTimeMillis() + ""))//时间
                .build();


        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/historySave");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                BenDiJiLuBean bean = new BenDiJiLuBean();
                bean.setSubjectId(subject.getId());
                bean.setDiscernPlace(FileUtil.getSerialNumber(MainActivity202.this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(MainActivity202.this));
                bean.setSubjectType(subject.getPeopleType());
                bean.setIdentificationTime(DateUtils.time(System.currentTimeMillis() + ""));
                benDiJiLuBeanBox.put(bean);


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传识别记录" + ss);


                } catch (Exception e) {
                    BenDiJiLuBean bean = new BenDiJiLuBean();
                    bean.setSubjectId(subject.getId());
                    bean.setDiscernPlace(FileUtil.getSerialNumber(MainActivity202.this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(MainActivity202.this));
                    bean.setSubjectType(subject.getPeopleType());
                    bean.setIdentificationTime(DateUtils.time(System.currentTimeMillis() + ""));
                    benDiJiLuBeanBox.put(bean);

                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }

    //上传识别记录2
    private void link_shangchuanjilu2(final BenDiJiLuBean subject) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(100000, TimeUnit.MILLISECONDS)
                .connectTimeout(100000, TimeUnit.MILLISECONDS)
                .readTimeout(100000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                .retryOnConnectionFailure(true)
                .build();
        RequestBody body = null;

        body = new FormBody.Builder()
                //.add("name", subject.getName()) //
                //.add("companyId", subject.getCompanyId()+"") //公司di
                //.add("companyName",subject.getCompanyName()+"") //公司名称
                //.add("storeId", subject.getStoreId()+"") //门店id
                //.add("storeName", subject.getStoreName()+"") //门店名称
                .add("subjectId", subject.getSubjectId() + "") //员工ID
                .add("subjectType", subject.getSubjectType() + "") //人员类型
                // .add("department", subject.getPosition()+"") //部门
                .add("discernPlace", subject.getDiscernPlace() + "")//识别地点
                // .add("discernAvatar",  "") //头像
                .add("identificationTime", subject.getIdentificationTime() + "")//时间
                .build();


        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/historySave");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                isSC = true;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传识别记录" + ss);
                    //成功的话 删掉本地保存的记录
                    benDiJiLuBeanBox.remove(subject.getId());

                } catch (Exception e) {

                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                } finally {
                    isSC = true;
                }
            }
        });
    }

}
