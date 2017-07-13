package com.example;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    public interface OnServiceListener {
        void onClientChanged(List<Client> clients);

        void onNewMessage(String message, Client client);
    }

    private OnServiceListener listener;

    public void setOnServiceListener(OnServiceListener listener) {
        this.listener = listener;
    }


    boolean started = false;
    ServerSocket ss = null;
    List<Client> clients = new ArrayList<Client>();

    @Override
    public void run() {
        // TODO Auto-generated method stub
        super.run();
        try {
            ss = new ServerSocket(8888);
            started = true;
            System.out.println("server is started");
        } catch (BindException e) {
            System.out.println("port is not available....");
            System.out.println("please restart");
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            while (started) {
                Socket s = ss.accept();
                Client c = new Client(s, Server.this);
                System.out.println("a client connected!");
                new Thread(c).start();
                addClient(c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                ss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public synchronized void snedMessage(String msg) {
        for (Client client1 : clients) {
            client1.send(msg);
        }
    }

    public synchronized void newMessage(String msg, Client client) {
        if (listener != null) {
            listener.onNewMessage(msg, client);
            for (Client client1 : clients) {
                if (!client1.equals(client)) {
                    client1.send(client1.getSocket().getInetAddress() + "#" + msg);
                }
            }
        }
    }

    public synchronized void addClient(Client client) {
        clients.add(client);
        if (listener != null) {
            listener.onClientChanged(clients);
        }
    }


    public synchronized void removeClient(Client client) {
        clients.remove(client);
        if (listener != null) {
            listener.onClientChanged(clients);
        }
    }

}
