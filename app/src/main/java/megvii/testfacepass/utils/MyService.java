package megvii.testfacepass.utils;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import org.greenrobot.eventbus.EventBus;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import megvii.testfacepass.ui.MainActivity203;


public class MyService extends Service {

    private MyBinder binder = new MyBinder();
    private static boolean  isLink=false;
    private static boolean  isRun=true;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createErrorNotification();

        Log.d("MyService", "启动服务中.......");

        new Thread(new Runnable() {
            @Override
            public void run() {
                WebSocket ws = null;
                try {
                    ws = new WebSocketFactory().setConnectionTimeout(10*1000) .createSocket("ws://192.168.2.189:9000/message");
                } catch (IOException e) {
                    Log.d("MyService", e.getMessage()+"soket连接异常");
                    EventBus.getDefault().post("wangluoyichang_flase");
                }

                try {
                    if (ws != null) {
                        ws.addListener(new WebSocketAdapter() {
                            @Override
                            public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                                isLink=true;
                                Log.d("MyService", "成功:" + websocket);
                                WebSocket socket=   websocket.sendText("mc:"+(FileUtil.getSerialNumber(MyService.this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(MyService.this)));
                                EventBus.getDefault().post("wangluoyichang_true");

                            }

                            @Override
                            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                                Log.d("MyService","收到消息:" + message);


                            }

                            @Override
                            public void onCloseFrame(final WebSocket websocket, WebSocketFrame frame) throws Exception {
                                Log.d("MyService", "连接异常:" + websocket);
                                EventBus.getDefault().post("wangluoyichang_flase");
                                isLink=false;
                                chonglian(websocket);

                            }

                            @Override
                            public void onDisconnected(final WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                                Log.d("MyService", "断开连接:" + websocket);
                                EventBus.getDefault().post("wangluoyichang_flase");
                                isLink=false;
                                chonglian(websocket);

                            }

                            @Override
                            public void onError(WebSocket websocket, WebSocketException cause) {
                                EventBus.getDefault().post("wangluoyichang_flase");
                                Log.d("MyService", "cause.getError():" + cause.getError());

                            }

                        }).addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).setPingInterval(60 * 1000).connect();
                    }
                } catch (WebSocketException e) {
                    EventBus.getDefault().post("wangluoyichang_flase");
                    Log.d("MyService", "e.getError():" + e.getError());
                }

            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    private void createErrorNotification() {

        //启用前台服务，主要是startForeground()
        Notification.Builder notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,"本地通讯服务");
        }else {
            //公共的属性都写到了这里避免代码重复
            notification = new Notification.Builder(this);//创建builder对象
            //指定点击通知后的动作，此处跳到我的博客

        }
        //  设置通知默认效果
         startForeground(1, notification.build());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);

    }

    public class MyBinder extends Binder {
        /** * 获取Service的方法 * @return 返回PlayerService */
        public MyService getService(){
            return MyService.this;
        }
    }

    private void chonglian(final WebSocket webSocket){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    webSocket.disconnect();
                    Thread.sleep(15000);//休眠10秒重新连接
                    if (!isLink){
                        webSocket.recreate().connect();
                    }
                    Log.d("MyService", "正在重连onDisconnected"+DateUtils.time(System.currentTimeMillis()+""));
                } catch (InterruptedException | WebSocketException | IOException e) {
                    Log.d("MyService", e.getMessage()+"重连异常2");
                    chonglian(webSocket);
                }
            }
        }).start();

    }

}
