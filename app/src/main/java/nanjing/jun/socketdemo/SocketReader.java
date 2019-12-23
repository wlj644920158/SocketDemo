package nanjing.jun.socketdemo;

import android.os.Handler;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SocketReader extends Thread {

    private Handler msgHandler;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    private volatile boolean isConnected = true;

    public SocketReader(Handler msgHandler, DataInputStream dataInputStream, DataOutputStream dataOutputStream) {
        this.msgHandler = msgHandler;
        this.dataInputStream = dataInputStream;
        this.dataOutputStream = dataOutputStream;
    }

    public void disconnect() {
        isConnected = false;
    }

    @Override
    public void run() {
        super.run();
        while (isConnected) {
            try {
                while (isConnected) {
                    String str = dataInputStream.readUTF();
                    if (msgHandler != null) {
                        Message message = Message.obtain();
                        message.what = 2;
                        message.obj = str;
                        msgHandler.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                msgHandler.sendEmptyMessage(3);
            }
        }

    }
}
