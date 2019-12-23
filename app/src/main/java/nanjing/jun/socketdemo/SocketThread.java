package nanjing.jun.socketdemo;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by 王黎军 on 2017/7/11.
 */

public class SocketThread extends Thread {

    private Socket socket;
    private boolean isConnected = false;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private Handler uiHandler;//UIHandler的what值1.代表socket收到新消息,2连接成功,断链
    private Handler msgHandler;//这个是当前SocketThread子线程的handler1.代表UI线程要发送一个消息,2代表reader线程读取到一个消息,3代表断链
    private SocketReader socketReader;

    public SocketThread(Handler uiHandler) {
        this.uiHandler = uiHandler;
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void run() {
        super.run();
        Looper.prepare();
        try {
            // 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket("192.168.20.167", 8887);//这里的ip应该是你的服务的ip
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("~~~~~~~~连接成功~~~~~~~~!");
            isConnected = true;
            uiHandler.sendEmptyMessage(2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        msgHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    //发送一个消息
                    try {
                        dataOutputStream.writeUTF((String) msg.obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == 2) {
                    //读取到一个消息
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = msg.obj;
                    uiHandler.sendMessage(message);
                } else if (msg.what == 3) {
                    disconnect();
                    uiHandler.sendEmptyMessage(3);
                }
            }
        };

        socketReader = new SocketReader(msgHandler, dataInputStream, dataOutputStream);
        socketReader.start();

        Looper.loop();
    }

    public void disconnect() {
        socketReader.disconnect();
        try {
            if (dataInputStream != null)
                dataInputStream.close();
            if (dataOutputStream != null)
                dataOutputStream.close();
            if (socket != null) {
                socket.close();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (msgHandler != null) {
            Message m = Message.obtain();
            m.what = 1;
            m.obj = message;
            msgHandler.sendMessage(m);
        }
    }

}
