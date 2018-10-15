package megvii.testfacepass.tuisong_jg;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Xml;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;


import org.xmlpull.v1.XmlPullParser;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URLDecoder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;



import cn.jpush.android.api.JPushInterface;
import io.objectbox.Box;

import megvii.facepass.FacePassException;
import megvii.facepass.FacePassHandler;
import megvii.facepass.types.FacePassAddFaceResult;
import megvii.testfacepass.MyApplication;

import megvii.testfacepass.beans.BaoCunBean;
import megvii.testfacepass.beans.BenDiMBbean;
import megvii.testfacepass.beans.BenDiMBbean_;
import megvii.testfacepass.beans.FangKeBean;
import megvii.testfacepass.beans.GuanHuai;
import megvii.testfacepass.beans.LingShiSubject;
import megvii.testfacepass.beans.MOBan;
import megvii.testfacepass.beans.RenYuanInFo;
import megvii.testfacepass.beans.Subject;
import megvii.testfacepass.beans.TuiSongBean;
import megvii.testfacepass.dialogall.ToastUtils;
import megvii.testfacepass.utils.DateUtils;
import megvii.testfacepass.utils.FileUtil;
import megvii.testfacepass.utils.GsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JIGUANG-Example";
	public  OkHttpClient okHttpClient=null;
	private BaoCunBean baoCunBean=null;
	private Box<BaoCunBean> baoCunBeanDao=null;
	private Box<Subject> subjectBox=null;
	private boolean isA=true;
	private static final String group_name = "face-pass-test-x";
	private Box<BenDiMBbean> benDiMBbeanDao=null;
	private Box<RenYuanInFo> renYuanInFoDao=null;
	private StringBuilder stringBuilder=null;
	private StringBuilder stringBuilder2=null;
	private StringBuilder stringBuilderId=new StringBuilder();
	String path2=null;
	private int TIMEOUT=30*1000;
	private Context context;
	private final String SDPATH = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"ruitongzip";
	public static boolean isDW=true;
	private FacePassHandler  facePassHandler;
	private Box<GuanHuai> guanHuaiBox=null;

	@Override
	public void onReceive(final Context context, Intent intent) {
		this.context=context;

		try {
			stringBuilder=new StringBuilder();
			stringBuilder2=new StringBuilder();
			baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
			subjectBox = MyApplication.myApplication.getBoxStore().boxFor(Subject.class);
			benDiMBbeanDao = MyApplication.myApplication.getBoxStore().boxFor(BenDiMBbean.class);
			guanHuaiBox = MyApplication.myApplication.getBoxStore().boxFor(GuanHuai.class);
			baoCunBean = baoCunBeanDao.get(123456L);
			renYuanInFoDao = MyApplication.myApplication.getBoxStore().boxFor(RenYuanInFo.class);
			facePassHandler=MyApplication.myApplication.getFacePassHandler();
			Bundle bundle = intent.getExtras();
		//	Logger.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

				Logger.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				JsonObject jsonObject= GsonUtil.parse(bundle.getString(JPushInterface.EXTRA_MESSAGE)).getAsJsonObject();
				if (jsonObject.get("title").getAsString().equals("迎宾设置")){
					String p1=null;
					Gson gson=new Gson();
					MOBan renShu=gson.fromJson(jsonObject,MOBan.class);
					String tp[]= renShu.getContent().getSubType().split("-");
					for (String typee : tp) {
						List<BenDiMBbean> bb = benDiMBbeanDao.query().equal(BenDiMBbean_.subType,typee).build().find();
						int size = bb.size();
						for (int i = 0; i < size; i++) {
							//删掉相同身份的，保证只有一种最新的身份
							benDiMBbeanDao.remove(bb.get(i));
						}
						p1=renShu.getContent().getBottemImageUrl().substring(1,renShu.getContent().getBottemImageUrl().length()-1);
						String p2=renShu.getContent().getPopupImageUrl().substring(1,renShu.getContent().getPopupImageUrl().length()-1);

						BenDiMBbean benDiMBbean=new BenDiMBbean();
						benDiMBbean.setId(System.currentTimeMillis());
						benDiMBbean.setBottemImageUrl(p1);
						benDiMBbean.setPopupImageUrl(p2);
						benDiMBbean.setWelcomeSpeak(renShu.getContent().getWelcomeSpeak());
						benDiMBbean.setSubType(typee);
						benDiMBbean.setPhoto_index(renShu.getContent().getPhoto_index());
						benDiMBbeanDao.put(benDiMBbean);
						baoCunBean.setWenzi(p1);
					}

					List<BenDiMBbean> f=  benDiMBbeanDao.getAll();
					for (BenDiMBbean ll:f){
						Log.d(TAG, "ll.getPhoto_index():" + ll.getPhoto_index()+ll.getSubType());
					}

					baoCunBeanDao.put(baoCunBean);
					Intent intent2=new Intent("gxshipingdizhi");
					intent2.putExtra("bgPath",p1);
					context.sendBroadcast(intent2);
					Log.d(TAG, "推送过去");
				}
				if (jsonObject.get("title").getAsString().equals("人员入库") || jsonObject.get("title").getAsString().equals("访客入库")){
					FileDownloader.setup(context);
					isDW=true;
					Thread.sleep(2200);
					//baoCunBean.setZhanhuiId(jsonObject.get("content").getAsJsonObject().get("id").getAsInt()+"");
					//baoCunBean.setGonggao(jsonObject.get("content").getAsJsonObject().get("screenId").getAsInt()+"");
					//baoCunBeanDao.put(baoCunBean);
					//Intent intent2=new Intent("gxshipingdizhi");
					//context.sendBroadcast(intent2);
					if (stringBuilderId.length()>0){
						stringBuilderId.delete(0,stringBuilderId.length());
					}
					path2 =baoCunBean.getHoutaiDiZhi().substring(0,baoCunBean.getHoutaiDiZhi().length()-5)+
							jsonObject.get("url").getAsString();
					Log.d(TAG, path2);
					File file = new File(SDPATH);
					if (!file.exists()) {
						Log.d(TAG, "file.mkdirs():" + file.mkdirs());
					}
					if (isDW) {
						isDW=false;
						Log.d(TAG, "进入下载");
						FileDownloader.getImpl().create(path2)
								.setPath(SDPATH + File.separator + System.currentTimeMillis() + ".zip")
								.setListener(new FileDownloadListener() {
									@Override
									protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
										Log.d(TAG, "pending"+soFarBytes);
									}

									@Override
									protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
										//已经连接上
										Log.d(TAG, "isContinue:" + isContinue);
										showNotifictionIcon(((float)soFarBytes/(float) totalBytes)*100,"下载中","下载人脸库中"+((float)soFarBytes/(float) totalBytes)*100+"%");

									}

									@Override
									protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
										Log.d(TAG, "soFarBytes:" + soFarBytes+task.getUrl());
										//进度
										isDW=false;
										if (task.getUrl().equals(path2)){

											ToastUtils.getInstances().setDate("下载中",((float)soFarBytes/(float) totalBytes)*100,"下载人脸库中"+((float)soFarBytes/(float) totalBytes)*100+"%");
										//	showNotifictionIcon(,,);
										}
									}

									@Override
									protected void blockComplete(BaseDownloadTask task) {
										//完成
									}

									@Override
									protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
									//重试
										Log.d(TAG, ex.getMessage()+"重试 "+retryingTimes);
									}

									@Override
									protected void completed(BaseDownloadTask task) {
										//完成整个下载过程
										if (task.getUrl().equals(path2)){
											isDW=true;
											String ss=SDPATH+ File.separator+(task.getFilename().substring(0,task.getFilename().length()-4));
											File file = new File(ss);
											if (!file.exists()) {
												Log.d(TAG, "创建文件状态:" + file.mkdir());
											}
											showNotifictionIcon(0,"解压中","解压人脸库中");
											jieya(SDPATH+ File.separator+task.getFilename(),ss);

											Log.d(TAG, "task.isRunning():" + task.isRunning()+ task.getFilename());

											if (baoCunBean!=null && baoCunBean.getZhanghuId()!=null)
												link_uplodexiazai();
										}
									}

									@Override
									protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

									}

									@Override
									protected void error(BaseDownloadTask task, Throwable e) {
										//出错
										if (task.getUrl().equals(path2)){
											isDW=true;

											Log.d(TAG, "task.isRunning():" + task.getFilename()+"失败"+e);
										}
										showNotifictionIcon(0,"下载失败",""+e);
									}

									@Override
									protected void warn(BaseDownloadTask task) {
										//在下载队列中(正在等待/正在下载)已经存在相同下载连接与相同存储路径的任务

									}
								}).start();

					}

				}
				Gson gson=new Gson();
				TuiSongBean renShu=gson.fromJson(jsonObject,TuiSongBean.class);
				//1 新增 2修改//3是删除
				switch (renShu.getTitle()){
					case "单个访客入库":
						link_dangeFangke(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;
					case "单个访客删除":
						link_dangeFangke(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;
					case "单个访客修改":
						link_dangeFangke(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;
					case "单个人员入库"://员工
						link_dangeYuanGong(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;

					case "小邮局入库":
						link_youju(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;
					case "关怀入库":
						link_guanhuai(renShu.getId(),renShu.getStatus(),renShu.getUrl());
						break;

//					case "生日关怀入库":
//						link_shengrri(renShu.getId(),renShu.getStatus(),renShu.getUrl());
//						break;
//					case "入职关怀入库":
//						link_ruzhi(renShu.getId(),renShu.getStatus(),renShu.getUrl());
//						break;

					case "":

						Intent intent2=new Intent("gxshipingdizhi");
						context.sendBroadcast(intent2);
						break;

				}
			//	processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Logger.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);


			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 用户点击打开了通知");

				//打开自定义的Activity
//				Intent i = new Intent(context, TestActivity.class);
//				i.putExtras(bundle);
//				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Logger.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Logger.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Logger.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){
			Logger.d(TAG, "[MyReceiver] 抛出了异常"+e.getMessage());
		}
	}


//	// 打印所有的 intent extra 数据
//	private static String printBundle(Bundle bundle) {
//		StringBuilder sb = new StringBuilder();
//		for (String key : bundle.keySet()) {
//			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
//				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
//				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
//					Logger.i(TAG, "This message has no Extra data");
//					continue;
//				}
//
//				try {
//					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
//					Iterator<String> it =  json.keys();
//
//					while (it.hasNext()) {
//						String myKey = it.next();
//						sb.append("\nkey:" + key + ", value: [" +
//								myKey + " - " +json.optString(myKey) + "]");
//					}
//				} catch (JSONException e) {
//					Logger.e(TAG, "Get message extra JSON error!");
//				}
//
//			} else {
//				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
//			}
//		}
//		return sb.toString();
//	}


	public static void showNotifictionIcon(float p, String title, String contextss) {
		//Log.d(TAG, "尽量");

		ToastUtils.getInstances().showDialog(title,contextss, (int) p);

//		NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//		//Intent intent = new Intent(context, XXXActivity.class);//将要跳转的界面
//		Intent intent = new Intent();//只显示通知，无页面跳转
//		builder.setAutoCancel(true);//点击后消失
//		builder.setSmallIcon(R.drawable.huiyi_logo);//设置通知栏消息标题的头像
//		//builder.setDefaults(NotificationCompat.DEFAULT_SOUND);//设置通知铃声
//		builder.setContentTitle(title);
//		builder.setContentText(contextss);
//		builder.setProgress(100, (int) p,false);
//		builder.setDefaults(Notification.DEFAULT_LIGHTS); //设置通知的提醒方式： 呼吸灯
//		builder.setPriority(NotificationCompat.PRIORITY_MAX); //设置通知的优先级：最大
//		//利用PendingIntent来包装我们的intent对象,使其延迟跳转
//		PendingIntent intentPend = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//		builder.setContentIntent(intentPend);
//		NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
//		manager.notify(0, builder.build());
	}

	private void jieya(String pathZip, final String path222){

		ZipFile zipFile=null;
		List fileHeaderList=null;
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			zipFile = new ZipFile(pathZip);
			zipFile.setFileNameCharset("GBK");
			fileHeaderList = zipFile.getFileHeaders();
			// Loop through the file headers
			Log.d(TAG, "fileHeaderList.size():" + fileHeaderList.size());

			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
			//	FileHeader fileHeader2 = (FileHeader) fileHeaderList.get(0);

				//Log.d(TAG, fileHeader2.getFileName());

				if (fileHeader.getFileName().contains(".xml")){
					zipFile.extractFile( fileHeader.getFileName(), path222);
					Log.d(TAG, "找到了"+i+"张照片");
				}


				// Various other properties are available in FileHeader. Please have a look at FileHeader
				// class to see all the properties
			}
		} catch (final ZipException e) {

			showNotifictionIcon(0,"解压失败",e.getMessage()+"");
		}
		//   UnZipfile.getInstance(SheZhiActivity.this).unZip(zz,trg,zipHandler);

		//拿到XML
		showNotifictionIcon(0,"解析XML中","解析XML中。。。。");
		List<String> xmls=new ArrayList<>();
		final List<String> xmlList= FileUtil.getAllFileXml(path222,xmls);
		if (xmlList==null || xmlList.size()==0){
			showNotifictionIcon(0,"没有找到Xml文件","没有找到Xml文件。。。。");
			return;
		}
		//解析XML
		try {
			FileInputStream fin=new FileInputStream(xmlList.get(0));
			//Log.d("SheZhiActivity", "fin:" + fin);
			final List<Subject> subjectList=  pull2xml(fin);
			Log.d(TAG, "XMLList值:" + subjectList);
			if (subjectList!=null && subjectList.size()>0){
				//排序
				Collections.sort(subjectList, new Subject());
				Log.d("SheZhiActivity", "解析成功,文件个数:"+subjectList.size());
				if (zipFile!=null){
					zipFile.setRunInThread(true); // true 在子线程中进行解压 ,
					// false主线程中解压
					zipFile.extractAll(path222); // 将压缩文件解压到filePath中..
				}

				//先登录旷视
				new Thread(new Runnable() {
					@Override
					public void run() {
						getOkHttpClient3(subjectList,path222);
					}
				}).start();



				final int size= subjectList.size();
				Log.d("ffffff", "size:" + size);

			}else {
				showNotifictionIcon(0,"解析失败","人脸库XML解析失败");

			}

		} catch (Exception e) {
			showNotifictionIcon(0,"解析失败","人脸库XML解析异常");
			Log.d("SheZhiActivity", e.getMessage()+"解析XML异常");
		}

}

	//入库
	public void getOkHttpClient3(final List<Subject> subjectList, final String trg){


		        		if (facePassHandler==null){
							showNotifictionIcon(0,"识别模块初始化失败","识别模块初始化失败无法入库");
		        			return;
						}
						final int size=subjectList.size();
						int t=0;
						Log.d(TAG, "size:" + size);
						//循环
						for (int j=0;j<size;j++) {
						//	Log.d(TAG, "i:" + j);
							String filePath=null;
							while (true){
								try {
									Thread.sleep(300);
									t++;
									Log.d(TAG, "2循环");
									// 获取后缀名
									//String sname = name.substring(name.lastIindexOf("."));
									filePath=trg+ File.separator+subjectList.get(j).getId()+(subjectList.get(j).getPhoto().
											substring(subjectList.get(j).getPhoto().lastIndexOf(".")));
									File file=new File(filePath);
									if ((file.isFile() && file.length()>0)|| t==100){
										t=0;
									//	Log.d(TAG, "file.length():" + file.length()+"   t:"+t);
										break;
									}
								}catch (Exception e){
									filePath=null;
									Log.d(TAG, e.getMessage()+"检测文件是否存在异常");
									break;
								}

							}
							//Log.d(TAG, "((float)j / (float) size * 100):" + ((float)j / (float) size * 100));
							showNotifictionIcon((int) ((float)j / (float) size * 100),"入库中","入库中"+(int) ((float)j / (float) size * 100)+"%");
							if (filePath!=null){
								try {

									FacePassAddFaceResult faceResult= facePassHandler.addFace(BitmapFactory.decodeFile(filePath));
									if (faceResult.result==0){
										//先查询有没有
										Subject ee= subjectBox.get(subjectList.get(j).getId());
										if (ee!=null){
											//重复编辑会导致旷视底库图片增多，所以先删除旷视的
											if (ee.getTeZhengMa()!=null){
											boolean bb=facePassHandler.deleteFace(ee.getTeZhengMa().getBytes());
												Log.d("MyRecriver", "批量入库中,删除已有的底库" + bb);
											}
										}
										facePassHandler.bindGroup(group_name,faceResult.faceToken);
										subjectList.get(j).setTeZhengMa(new String(faceResult.faceToken));
										subjectList.get(j).setDaka(0);
										subjectBox.put(subjectList.get(j));
										stringBuilderId.append(subjectList.get(j).getId());
										stringBuilderId.append(",");

										Log.d(TAG,"批量入库成功："+ subjectList.get(j).getId());

									}else {
										stringBuilder2.append("入库添加图片失败:").append("ID:")
												.append(subjectList.get(j).getId()).append("姓名:")
												.append(subjectList.get(j).getName()).append("时间:")
												.append(DateUtils.time(System.currentTimeMillis() + ""))
												.append("错误码:").append(faceResult.result).append("\n");
									}

								} catch (FacePassException e) {
									e.printStackTrace();
									stringBuilder2.append("入库添加图片失败:").append("ID:")
											.append(subjectList.get(j).getId()).append("姓名:")
											.append(subjectList.get(j).getName()).append("时间:")
											.append(DateUtils.time(System.currentTimeMillis() + ""))
											.append("错误码:").append(e.getMessage()).append("\n");
								}

							}else {
                                stringBuilder2.append("入库失败文件不存在:").append("ID:")
                                        .append(subjectList.get(j).getId()).append("姓名:")
                                        .append(subjectList.get(j).getName()).append("时间:")
                                        .append(DateUtils.time(System.currentTimeMillis() + ""))
                                        .append("\n");
                            }

//							//查询旷视
//							synchronized (subjectList.get(j)) {
//								//link_chaXunRenYuan(okHttpClient, subjectList.get(j),trg,filePath);
//								try {
//									subjectList.get(j).wait();
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//
//							}

						}
						//   Log.d("SheZhiActivity", "循环完了");

						String ss=stringBuilder2.toString();

						if (ss.length()>0){

							try {
								FileUtil.savaFileToSD("失败记录"+DateUtils.timesOne(System.currentTimeMillis()+"")+".txt",ss);
								showNotifictionIcon(0,"入库完成","有失败的记录,已保存到本地根目录");
								stringBuilder2.delete(0, stringBuilder2.length());
							} catch (Exception e) {
								e.printStackTrace();
							}

						}else {
							showNotifictionIcon(0,"入库完成","全部入库成功，没有失败记录");
						}
						if (stringBuilderId.length()>0){
							gengxingzhuangtai();
						}

					}


//	public static final int TIMEOUT2 = 1000 * 100;
//	private void link_P1(final ZhuJiBeanH zhuJiBeanH, String filePath, final Subject subject, final int id) {
//
//		OkHttpClient okHttpClient = new OkHttpClient.Builder()
//				.writeTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//				.connectTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//				.readTimeout(TIMEOUT2, TimeUnit.MILLISECONDS)
//				.cookieJar(new CookiesManager())
//				.retryOnConnectionFailure(true)
//				.build();
//
//		MultipartBody mBody;
//		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//		Log.d("SheZhiActivity", filePath+"图片文件路径");
//
//		final File file=new File(filePath==null?"/a":filePath);
//		RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream"),file);
//
//		builder.addFormDataPart("photo",file.getName(), fileBody1);
//		//builder.addFormDataPart("subject_id","228");
//		mBody = builder.build();
//
//		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.post(mBody)
//				.url(zhuJiBeanH.getHostUrl()+ "/subject/photo");
//
//		// step 3：创建 Call 对象
//		Call call = okHttpClient.newCall(requestBuilder.build());
//
//		//step 4: 开始异步请求
//		call.enqueue(new Callback() {
//			@Override
//			public void onFailure(Call call, IOException e) {
//
//				stringBuilder2.append("上传图片失败记录:")
//						.append("ID").append(subject.getId()).append("姓名:")
//						.append(subject.getName())
//						.append("原因:")
//						.append(e.getMessage())
//						.append("时间:")
//						.append(DateUtils.time(System.currentTimeMillis()+""))
//						.append("\n");
//
//				if (id==-1){
//					//新增
//					link_addPiLiangRenYuan(MyApplication.okHttpClient,subject,0);
//				} else {
//					//更新
//					link_XiuGaiRenYuan(MyApplication.okHttpClient,subject,0,id);
//				}
//
//				Log.d("AllConnects图片上传", "请求识别失败" + e.getMessage());
//			}
//
//			@Override
//			public void onResponse(Call call, Response response) throws IOException {
//
//				//    Log.d("AllConnects", "请求识别成功" + call.request().toString());
//				//获得返回体
//				try {
//					ResponseBody body = response.body();
//					String ss = body.string();
//					Log.d("AllConnects图片上传", "传照片" + ss);
//					int ii=0;
//					JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//					if (jsonObject.get("code").getAsInt()==0){
//
//
//						JsonObject jo=jsonObject.get("data").getAsJsonObject();
//						ii=jo.get("id").getAsInt();
//
//						if (ii!=0) {
//							// ii 照片id
//							if (id == -1) {
//								//新增
//								link_addPiLiangRenYuan(MyApplication.okHttpClient, subject, ii);
//							} else {
//								//更新
//								link_XiuGaiRenYuan(MyApplication.okHttpClient, subject, ii, id);
//							}
//
//						}
//
//					}else {
//						// Log.d("SheZhiActivity333333", jsonObject.get("desc").getAsString());
//						stringBuilder2.append("上传图片失败记录:")
//								.append("ID").append(subject.getId())
//								.append("姓名:")
//								.append(subject.getName())
//								.append("原因:")
//								.append(jsonObject.get("desc").getAsString())
//								.append("时间:")
//								.append(DateUtils.time(System.currentTimeMillis()+"")).append("\n");
//
//						if (id==-1){
//							//新增
//							link_addPiLiangRenYuan(MyApplication.okHttpClient,subject,0);
//						} else {
//							//更新
//							link_XiuGaiRenYuan(MyApplication.okHttpClient,subject,0,id);
//						}
//					}
//				} catch (Exception e) {
//					stringBuilder2.append("上传图片失败记录:").append("ID").
//							append(subject.getId())
//							.append("姓名:")
//							.append(subject.getName())
//							.append("原因:")
//							.append(e.getMessage())
//							.append("时间:").
//							append(DateUtils.time(System.currentTimeMillis()+"")).append("\n");
//					if (id==-1){
//						//新增
//						link_addPiLiangRenYuan(MyApplication.okHttpClient,subject,0);
//					} else {
//						//更新
//						link_XiuGaiRenYuan(MyApplication.okHttpClient,subject,0,id);
//					}
//					Log.d("AllConnects图片上传异常", e.getMessage());
//				}
//			}
//		});
//	}


	public List<Subject> pull2xml(InputStream is) throws Exception {
		//Log.d(TAG, "jiexi 111");
		List<Subject> list  = new ArrayList<>();;
		Subject student = null;
		//创建xmlPull解析器
		XmlPullParser parser = Xml.newPullParser();
		///初始化xmlPull解析器
		parser.setInput(is, "utf-8");
		//读取文件的类型
		int type = parser.getEventType();
		//无限判断文件类型进行读取
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
				//开始标签
				case XmlPullParser.START_TAG:
					if ("Root".equals(parser.getName())) {
//						String id=parser.getAttributeValue(0);
//						if (baoCunBean.getZhanghuId()==null || !baoCunBean.getZhanghuId().equals(id)){
//							Log.d(TAG, "jiexi222 ");
//							showNotifictionIcon(context,0,"解析XML失败","xml账户ID不匹配");
//							Log.d(TAG, "333jiexi ");
//							return null;
//						}

					} else if ("Subject".equals(parser.getName())) {

						student=new Subject();
						student.setId(Long.valueOf(parser.getAttributeValue(0)));
						student.setSid(parser.getAttributeValue(0));

					} else if ("name".equals(parser.getName())) {
						//获取name值
						String name = parser.nextText();
						if (name!=null){
							student.setName(URLDecoder.decode(name, "UTF-8"));
						}

					} else if ("companyId".equals(parser.getName())) {
						//获取nickName值
						String companyId = parser.nextText();
						if (companyId!=null){
							student.setPhone(companyId);
						}
					}else if ("companyName".equals(parser.getName())) {
						//获取nickName值
						String companyName = parser.nextText();
						if (companyName!=null){
							student.setCompanyName(URLDecoder.decode(companyName, "UTF-8"));
						}
					}
					else if ("workNumber".equals(parser.getName())) {
						//获取nickName值
						String workNumber = parser.nextText();
						if (workNumber!=null){
							student.setWorkNumber(URLDecoder.decode(workNumber, "UTF-8"));
						}
					}
					else if ("sex".equals(parser.getName())) {
						//获取nickName值
						String sex = parser.nextText();
						if (sex!=null){
							student.setSex(URLDecoder.decode(sex, "UTF-8"));
						}
					}
					else if ("phone".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setPhone(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("peopleType".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setPeopleType(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("email".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setEmail(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("position".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setPosition(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("employeeStatus".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setEmployeeStatus(Integer.valueOf(nickName));
						}
					}
					else if ("quitType".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setQuitType(Integer.valueOf(nickName));
						}
					}
					else if ("remark".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setRemark(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("photo".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setPhoto(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("storeId".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setStoreId(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("storeName".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setStoreName(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("entryTime".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setEntryTime(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("birthday".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setBirthday(URLDecoder.decode(nickName, "UTF-8"));
						}
					}
					else if ("departmentName".equals(parser.getName())) {
						//获取nickName值
						String nickName = parser.nextText();
						if (nickName!=null){
							student.setDepartmentName(URLDecoder.decode(nickName, "UTF-8"));
						}
					}

					break;
				//结束标签
				case XmlPullParser.END_TAG:
					if ("Subject".equals(parser.getName())) {
						list.add(student);
					}
					break;
			}
			//继续往下读取标签类型
			type = parser.next();
		}
		return list;
	}



	//从老黄后台拿单人信息
	private void link_getHouTaiDanRen(int id, final Context context, final int status){
		final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient=  new OkHttpClient.Builder()
				.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//				.cookieJar(new CookiesManager())
				.retryOnConnectionFailure(true)
				.build();

		RequestBody body = new FormBody.Builder()
				.add("id",id+"")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
				.header("Content-Type", "application/json")
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+"/getAppSubject.do");

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());

		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{

					ResponseBody body = response.body();
					String ss=body.string().trim();
					Log.d("AllConnects", "获取单人信息"+ss);

					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
					Gson gson=new Gson();
					RenYuanInFo renYuanInFo=gson.fromJson(jsonObject,RenYuanInFo.class);

					//保存到本地
//					if (sheBeiInFoBeanDao.load(zhaoPianBean.getId())==null){
//						//新增
//						sheBeiInFoBeanDao.insert(zhaoPianBean);
//					}
					//先登陆
				//	getOkHttpClient(context,status,null,renYuanInFo);
					Log.d("MyReceiver", "登陆");

				}catch (Exception e){
					Log.d("WebsocketPushMsg", e.getMessage()+"gggg");
				}
			}
		});
	}


	/**
	 * 压缩图片（质量压缩）
	 * @param bitmap
	 */
	public static File compressImage(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
			baos.reset();//重置baos即清空baos
			options -= 10;//每次都减少10
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
			//long length = baos.toByteArray().length;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date(System.currentTimeMillis());
		String filename = format.format(date);
		File file = new File(Environment.getExternalStorageDirectory(),filename+".png");
		try {
			FileOutputStream fos = new FileOutputStream(file);
			try {
				fos.write(baos.toByteArray());
				fos.flush();
				fos.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
	//	recycleBitmap(bitmap);
		return file;
	}
//	public static void recycleBitmap(Bitmap... bitmaps) {
//		if (bitmaps==null) {
//			return;
//		}
//		for (Bitmap bm : bitmaps) {
//			if (null != bm && !bm.isRecycled()) {
//				bm.recycle();
//			}
//		}
//	}



	//提交下载状态
	private void link_uplodexiazai(){

		//	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient= new OkHttpClient();
		//RequestBody requestBody = RequestBody.create(JSON, json);
		RequestBody body = new FormBody.Builder()
				.add("id",baoCunBean.getZhanhuiId())
				.add("downloads","1")
				.build();
		Log.d(TAG, baoCunBean.getZhanhuiId()+"展会id");
		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
				//.post(requestBody)
				//.get()
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+"/appSaveExDownloads.do");

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());
		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{

					ResponseBody body = response.body();
					String ss=body.string().trim();
					Log.d("AllConnects", "上传下载次数状态"+ss);

//					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
//					Gson gson=new Gson();
//					final HuiYiInFoBean renShu=gson.fromJson(jsonObject,HuiYiInFoBean.class);


				}catch (Exception e){
					Log.d("WebsocketPushMsg", e.getMessage()+"ttttt");
				}

			}
		});
	}


	//单个访客
	private void link_dangeFangke(final String id, final int status, final String url){

		//	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient= new OkHttpClient();
		//RequestBody requestBody = RequestBody.create(JSON, json);
		RequestBody body = new FormBody.Builder()
				.add("id",id)
			//	.add("downloads","1")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
				//.post(requestBody)
				//.get()
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+url);

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());
		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{

					ResponseBody body = response.body();
					final String ss=body.string().trim();
					Log.d("AllConnects", "单个访客"+ss);
					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
					Gson gson=new Gson();
					final FangKeBean renShu=gson.fromJson(jsonObject,FangKeBean.class);
					if (status!=3){
						Bitmap bitmap=null,bitmapTX=null;
						try {
							bitmap = Glide.with(context).asBitmap()
									.load(baoCunBean.getHoutaiDiZhi().replace("/front","")+renShu.getPhoto())
									// .sizeMultiplier(0.5f)
									.submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
									.get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						if (bitmap!=null) {
							FacePassAddFaceResult faceResult = null;
							try {
								faceResult = facePassHandler.addFace(bitmap);
								if (faceResult.result == 0) {
									//先查询有没有
									Subject ee= subjectBox.get(Long.valueOf(renShu.getId()));
									if (ee!=null){
										//重复编辑会导致旷视底库图片增多，所以先删除旷视的
										if (ee.getTeZhengMa()!=null)
										Log.d("MyReceiver", "删除已有的访客底库"+facePassHandler.deleteFace(ee.getTeZhengMa().getBytes()));
									}
									facePassHandler.bindGroup(group_name, faceResult.faceToken);
									Subject subject = new Subject();
									subject.setTeZhengMa(new String(faceResult.faceToken));
									subject.setId(Long.valueOf(renShu.getId()));
									subject.setPeopleType(renShu.getPeopleType());
									subject.setDaka(0);
									subject.setBirthday(renShu.getBirthday());//访客的过期时间
									subject.setName(renShu.getName());
									subject.setEntryTime(renShu.getEntryTime());//访客的进入时间
									subject.setPhone(renShu.getPhone());
									subject.setEmail(renShu.getEmail());
									subject.setRemark(renShu.getRemark());
									subject.setPosition(renShu.getPosition());
									subject.setWorkNumber(renShu.getWorkNumber());
									subject.setStoreId(renShu.getStoreId());
									subject.setStoreName(renShu.getStoreName());
									subject.setCompanyId(renShu.getCompanyId());
                                   // subject.setDepartmentName(renShu.getDepartmentName());
									if (renShu.getDisplayPhoto()!=null && !renShu.getDisplayPhoto().equals("")){
										try {
											bitmapTX = Glide.with(context).asBitmap()
													.load(baoCunBean.getHoutaiDiZhi().replace("/front","")+renShu.getDisplayPhoto())
													// .sizeMultiplier(0.5f)
													.submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
													.get();
										} catch (InterruptedException | ExecutionException e) {
											e.printStackTrace();
										}
									}
									//保存头像
									String path=null;
									if (bitmapTX!=null){
										String fn = renShu.getId()+".jpg";
										FileUtil.isExists(FileUtil.PATH, fn);
										path=saveBitmap2File2(bitmapTX, FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn, 100);
									}
									subject.setDisplayPhoto(path);
									Log.d("MyReceiver", "单个访客入库成功"+subjectBox.put(subject));
								}else {
									showNotifictionIcon(0,"入库失败","图片不符合入库要求 "+renShu.getName());
								}

								} catch(FacePassException e){
									e.printStackTrace();
								}
						}else {
							showNotifictionIcon(0,"入库失败","下载图片失败 "+renShu.getName());
						}

					}else {
						//删除
						facePassHandler.deleteFace(subjectBox.get(Long.valueOf(id)).getTeZhengMa().getBytes());
						subjectBox.remove(Long.valueOf(id));
						Log.d("MyReceiver", "单个访客删除成功");
					}

				}catch (Exception e){
					showNotifictionIcon(0,"入库出错","出现异常"+e.getMessage());
				}

			}
		});
	}


	//单个员工
	private void link_dangeYuanGong(final String id, final int status, final String url){

		//	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient= new OkHttpClient();
		//RequestBody requestBody = RequestBody.create(JSON, json);
		RequestBody body = new FormBody.Builder()
				.add("id",id)
				//	.add("downloads","1")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
				//.post(requestBody)
				//.get()
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+url);

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());
		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{
					ResponseBody body = response.body();
					final String ss=body.string().trim();
					Log.d("AllConnects", "单个员工"+ss);
					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
					Gson gson=new Gson();
					final LingShiSubject renShu=gson.fromJson(jsonObject,LingShiSubject.class);
					if (status!=3){
						Bitmap bitmap=null,bitmapTX=null;
						try {
							bitmap = Glide.with(context).asBitmap()
									.load(baoCunBean.getHoutaiDiZhi().replace("/front","")+renShu.getPhoto())
									// .sizeMultiplier(0.5f)
									.submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
									.get();
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}

						if (bitmap!=null) {
							FacePassAddFaceResult faceResult = null;
							try {
								faceResult = facePassHandler.addFace(bitmap);
								if (faceResult.result == 0) {
									//先查询有没有
									Subject ee= subjectBox.get(renShu.getId());
									if (ee!=null){
										//重复编辑会导致旷视底库图片增多，所以先删除旷视的
										if (ee.getTeZhengMa()!=null)
										Log.d("MyReceiver", "删除已有的员工底库"+facePassHandler.deleteFace(ee.getTeZhengMa().getBytes()));
									}
									facePassHandler.bindGroup(group_name, faceResult.faceToken);
									//Log.d("MyReceiver", "faceResult.faceToken:" + new String(faceResult.faceToken));
									Subject subject = new Subject();
									subject.setTeZhengMa(new String(faceResult.faceToken));
									subject.setId(renShu.getId());
									subject.setPeopleType(renShu.getPeopleType());
									subject.setDaka(0);
									subject.setBirthday(renShu.getBirthday());
									subject.setName(renShu.getName());
									subject.setEntryTime(renShu.getEntryTime());
									subject.setPhone(renShu.getPhone());
									subject.setEmail(renShu.getEmail());
									subject.setRemark(renShu.getRemark());
									subject.setPosition(renShu.getPosition());
									subject.setWorkNumber(renShu.getWorkNumber());
									subject.setStoreId(renShu.getStoreId());
									subject.setStoreName(renShu.getStoreName());
									subject.setCompanyId(renShu.getCompanyId());
                                    subject.setDepartmentName(renShu.getDepartmentName());
									if (renShu.getDisplayPhoto()!=null && !renShu.getDisplayPhoto().equals("")){
										try {
											bitmapTX = Glide.with(context).asBitmap()
													.load(baoCunBean.getHoutaiDiZhi().replace("/front","")+renShu.getDisplayPhoto())
													// .sizeMultiplier(0.5f)
													.submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
													.get();
										} catch (InterruptedException | ExecutionException e) {
											e.printStackTrace();
										}
									}
									//保存头像
									String path=null;
									if (bitmapTX!=null){
										String fn = renShu.getId()+".jpg";
										FileUtil.isExists(FileUtil.PATH, fn);
										path=saveBitmap2File2(bitmapTX, FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn, 100);
									}
									subject.setDisplayPhoto(path);
									subjectBox.put(subject);
									Log.d("MyReceiver", "单个员工入库成功");
								}else {
									showNotifictionIcon(0,"入库失败","图片不符合入库要求 "+renShu.getName());
								}

							} catch(FacePassException e){
								e.printStackTrace();
							}
						}else {
							showNotifictionIcon(0,"入库失败","下载图片失败 "+renShu.getName());
						}

					}else {
						//删除
						try {
							facePassHandler.deleteFace(subjectBox.get(Long.valueOf(id)).getTeZhengMa().getBytes());
						} catch (FacePassException e) {
							e.printStackTrace();
						}
						subjectBox.remove(Long.valueOf(id));
						Log.d("MyReceiver", "单个员工删除成功");
					}

				}catch (Exception e){
					Log.d("MyReceiver", "dddd");
					showNotifictionIcon( 0,"入库出错","出现异常"+e.getMessage());
				}

			}
		});
	}



	//关怀
	private void link_guanhuai(final String id, final int status, final String url){
		//	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient= new OkHttpClient();
		//RequestBody requestBody = RequestBody.create(JSON, json);
		RequestBody body = new FormBody.Builder()
				.add("id",id)
				//	.add("downloads","1")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
				//.post(requestBody)
				//.get()
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+url);

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());
		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}
			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{
					ResponseBody body = response.body();
					final String ss=body.string().trim();
					Log.d("AllConnects", "节日"+ss);

					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
					Gson gson=new Gson();
					// 1生日提醒 2入职关怀 3节日关怀）
					final GuanHuai youJuBean=gson.fromJson(jsonObject,GuanHuai.class);
					if (status!=3){
					//添加
						Log.d("MyReceiver", "关怀入库:" + guanHuaiBox.put(youJuBean));
					}else {
						//删除
						guanHuaiBox.remove(youJuBean.getId());
						Log.d("MyReceiver", "删除关怀");
					}

				}catch (Exception e){
					showNotifictionIcon( 0,"节日","出现异常"+e.getMessage());
				}

			}
		});
	}

	//关怀
	private void link_youju(final String id, final int status, final String url){
		//	final MediaType JSON=MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient= new OkHttpClient();
		//RequestBody requestBody = RequestBody.create(JSON, json);
		RequestBody body = new FormBody.Builder()
				.add("id",id)
				//	.add("downloads","1")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
//				.header("Content-Type", "application/json")
//				.header("user-agent","Koala Admin")
				//.post(requestBody)
				//.get()
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi()+url);

		// step 3：创建 Call 对象
		Call call = okHttpClient.newCall(requestBuilder.build());
		//step 4: 开始异步请求
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				Log.d("AllConnects", "请求失败"+e.getMessage());
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.d("AllConnects", "请求成功"+call.request().toString());
				//获得返回体
				try{
					ResponseBody body = response.body();
					final String ss=body.string().trim();
					Log.d("AllConnects", "邮局"+ss);
					JsonObject jsonObject= GsonUtil.parse(ss).getAsJsonObject();
					Gson gson=new Gson();
					final GuanHuai youJuBean=gson.fromJson(jsonObject,GuanHuai.class);
					if (status!=3){
						Log.d("MyReceiver", "邮局入库:" + guanHuaiBox.put(youJuBean));
					}else {
						//删除
						guanHuaiBox.remove(youJuBean.getId());
						Log.d("MyReceiver", "删除邮局");
					}
				}catch (Exception e){
					showNotifictionIcon( 0,"邮局","出现异常"+e.getMessage());
				}

			}
		});
	}


	//从老黄后台拿批量信息
	private void link_getHouTaiPiLiang(final int id, final Context context, final int status){
		if (status==3){
			//删除
//			BenDiQianDaoDao dao= MyApplication.myApplication.getDaoSession().getBenDiQianDaoDao();
//			dao.deleteAll();
//			getOkHttpClient2(context,3);

		}else {
			//新增
			final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
			OkHttpClient okHttpClient = new OkHttpClient.Builder()
					.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
					.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
					.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//				.cookieJar(new CookiesManager())
					.retryOnConnectionFailure(true)
					.build();

			RequestBody body = new FormBody.Builder()
					.add("companyName", baoCunBean.getZhanghuId())
					.add("cardNumber", id + "")
					.build();
			Request.Builder requestBuilder = new Request.Builder()
					.header("Content-Type", "application/json")
					.post(body)
					.url(baoCunBean.getHoutaiDiZhi() + "/queryMeetingSubject.do");

			// step 3：创建 Call 对象
			Call call = okHttpClient.newCall(requestBuilder.build());

			//step 4: 开始异步请求
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
					Log.d("AllConnects", "请求失败" + e.getMessage());
					stringBuilder.append("从后台获取人员信息失败记录:")
							.append("ID").append(id)
							.append("时间:")
							.append(DateUtils.time(System.currentTimeMillis()+""))
							.append("\n");
				}

				@Override
				public void onResponse(Call call, Response response) throws IOException {
					Log.d("AllConnects", "请求成功" + call.request().toString());
					//获得返回体
					try {
						//没了删除，所有在添加前要删掉所有
//						renYuanInFoDao.deleteAll();
//						ResponseBody body = response.body();
//						String ss = body.string().trim();
//						Log.d("AllConnects", "获取批量人员信息" + ss);
//
//						JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//						Gson gson = new Gson();
//						PiLiangBean zhaoPianBean = gson.fromJson(jsonObject, PiLiangBean.class);
//						int size = zhaoPianBean.getObjects().size();
//
//						for (int i = 0; i < size; i++) {
//							PiLiangBean.ObjectsBean bbb = zhaoPianBean.getObjects().get(i);
//							RenYuanInFo inFo = new RenYuanInFo();
//							inFo.setAccountId(bbb.getAccountId());
//							//inFo.setCardNumber(bbb.getCardNumber());
//							inFo.setDepartment(bbb.getDepartment());
//							inFo.setGender(bbb.getGender());
//							inFo.setId((long) bbb.getId());
//							inFo.setName(bbb.getName());
//							inFo.setPhone(bbb.getPhone());
//							inFo.setRemark(bbb.getRemark());
//							inFo.setPhoto_ids(bbb.getPhoto_ids());
//							inFo.setTitle(bbb.getTitle());
//							inFo.setJobStatus(bbb.getJobStatus());
//							inFo.setSourceMeeting(bbb.getSourceMeeting());
//							try {//保存到本地
//								renYuanInFoDao.insert(inFo);
//							} catch (Exception e) {
//								Log.d("MyReceiver", "插入批量到本地异常" + e.getMessage());
//							}
//						}

						//Log.d("MyReceivereee", "DDDDD");
						//先登陆
					//	getOkHttpClient2(context, 1);

					} catch (Exception e) {
						stringBuilder.append("获取后台数据异常记录:")
								.append("ID").append(id)
								.append("时间:")
								.append(DateUtils.time(System.currentTimeMillis()+""))
								.append("\n");
						Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
					}
				}
			});
		}
	}


	private void gengxingzhuangtai(){
		final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
				.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
				.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
//				.cookieJar(new CookiesManager())
				//.retryOnConnectionFailure(true)
				.build();

		RequestBody body = new FormBody.Builder()
				.add("pictureId", stringBuilderId.toString())
				//.add("cardNumber", id + "")
				.build();
		Request.Builder requestBuilder = new Request.Builder()
				//.header("Content-Type", "application/json")
				.post(body)
				.url(baoCunBean.getHoutaiDiZhi() + "/app/employeeStatus");

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
					//没了删除，所有在添加前要删掉所有

						ResponseBody body = response.body();
						String ss = body.string().trim();
						Log.d("AllConnects", "更新后台状态" + ss);

				} catch (Exception e) {

					Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
				}
			}
		});
	}


	private void XiaZaiTuPian(Context context, RenYuanInFo renYuanInFo){
		Glide.with(context).asBitmap().load("").into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

			}

			@Override
			public void onLoadFailed(@Nullable Drawable errorDrawable) {
				super.onLoadFailed(errorDrawable);

			}
		});
		//Log.d("MyReceiver", "图片0");
		Bitmap bitmap=null;
		try {
			  bitmap = Glide.with(context).asBitmap()
					.load(baoCunBean.getHoutaiDiZhi()+"/upload/compare/"+renYuanInFo.getPhoto_ids())
					// .sizeMultiplier(0.5f)
					.submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
					.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			stringBuilder.append("从瑞瞳后台下载图片失败记录:")
					.append("图片地址").append(baoCunBean.getHoutaiDiZhi()).append("/upload/compare/").append(renYuanInFo.getPhoto_ids())
					.append("时间:")
					.append(DateUtils.time(System.currentTimeMillis()+""))
					.append("\n");
		}

		if (bitmap!=null){



		}else {

			Intent intent=new Intent("shoudongshuaxin");
			intent.putExtra("date","登录失败");
			context.sendBroadcast(intent);

		}
	}


	/***
	 *保存bitmap对象到文件中
	 * @param bm
	 * @param path
	 * @param quality
	 * @return
	 */
	public String saveBitmap2File2(Bitmap bm, final String path, int quality) {
		if (null == bm || bm.isRecycled()) {
			Log.d("InFoActivity", "回收|空");
			return null;
		}
		try {
			File file = new File(path);
			if (file.exists()) {
				file.delete();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
			bos.flush();
			bos.close();


		} catch (Exception e) {
			e.printStackTrace();

		} finally {

			if (!bm.isRecycled()) {
				bm.recycle();
			}
			bm = null;
		}
		return path;
	}

}
