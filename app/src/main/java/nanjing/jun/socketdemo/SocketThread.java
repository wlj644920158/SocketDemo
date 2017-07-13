package nanjing.jun.socketdemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by 王黎军 on 2017/7/11.
 */

public class SocketThread extends Thread {

    public interface OnClientListener {
        void onNewMessage(String msg);
    }

    private OnClientListener onClientListener;

    public void setOnClientListener(OnClientListener onClientListener) {
        this.onClientListener = onClientListener;
    }

    private Socket socket;
    private boolean isConnected = false;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public SocketThread(OnClientListener onClientListener) {
        this.onClientListener = onClientListener;

    }

    public void disconnect() {
        try {
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void run() {
        super.run();
        try {
            // 创建一个Socket对象，并指定服务端的IP及端口号
            socket = new Socket("10.137.213.28", 8888);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            System.out.println("~~~~~~~~连接成功~~~~~~~~!");
            isConnected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isConnected) {
            try {
                while (isConnected) {
                    String str = dataInputStream.readUTF();
                    if (str != null) {
                        if (onClientListener != null) {
                            onClientListener.onNewMessage(str);
                        }
                    }
                }
            } catch (EOFException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
        }
    }



    public void sendMessage(String message) {
        try {
            dataOutputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
