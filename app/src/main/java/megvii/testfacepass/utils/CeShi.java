package megvii.testfacepass.utils;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketExtension;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CeShi {

  private  boolean  isLink=true;

    public void statrt(){

         WebSocket ws = null;

        try {
            ws = new WebSocketFactory().setConnectionTimeout(10*1000) .createSocket("ws://192.168.2.189:9090/message");
        } catch (IOException e) {
           Log.d("CeShi", e.getMessage()+"soket连接异常");
        }
        final String m= "";
        try {
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.d("CeShi", "成功:" + websocket);

                       WebSocket socket=   websocket.sendText("mc:555");
                        isLink=false;

                }

                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    Log.d("CeShi", "收到消息:" + websocket);
                    Log.d("CeShi", message);
                }

                @Override
                public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    Log.d("CeShi", "连接异常:" + websocket);
                    websocket.disconnect();
                    try {
                        Thread.sleep( 3000);//休眠1分钟重新连接
                        websocket.recreate().connect();
                        Log.d("CeShi", "正在重连"+DateUtils.time(System.currentTimeMillis()+""));
                    } catch (InterruptedException | IOException | WebSocketException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) {
                    Log.d("CeShi", "断开连接:" + websocket);
                    isLink=true;
                    while (isLink){
                        websocket.disconnect();
                        if (!closedByServer) {
                            try {
                                Thread.sleep(3000);//休眠5秒重新连接
                                websocket.recreate().connect();
                                Log.d("CeShi", "正在重连onDisconnected"+DateUtils.time(System.currentTimeMillis()+""));
                            } catch (InterruptedException | WebSocketException | IOException e) {
                                Log.d("CeShi", e.getMessage()+"重连异常");
                            }
                        }
                    }

                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) {
                   Log.d("CeShi", "cause.getError():" + cause.getError());
                }

            }).addExtension(WebSocketExtension.PERMESSAGE_DEFLATE).setPingInterval(60 * 1000).connect();
        } catch (WebSocketException e) {

           Log.d("CeShi", "e.getError():" + e.getError());
        }


    }


}
