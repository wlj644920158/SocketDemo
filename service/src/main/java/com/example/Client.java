package com.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable{

	private Socket s;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private boolean bConnected = false;
	private Server server;

	public Socket getSocket() {
		return s;
	}

	public Client(Socket s, Server ser) {
		this.s=s;
		this.server = ser;
		try {
			dis = new DataInputStream(s.getInputStream());
			dos = new DataOutputStream(s.getOutputStream());
			bConnected = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String str) {
		try {
			dos.writeUTF(str);
		} catch (IOException e) {
			server.removeClient(this);
		
		}
	}

	public void run() {
		try {
			while (bConnected) {
				String str = dis.readUTF();
				server.newMessage(str,this);
			}
		} catch (EOFException e) {
			System.out.println("Client closed!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dis != null)
					dis.close();
				if (dos != null)
					dos.close();
				if (s != null) {
					server.removeClient(this);
					s.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Client client = (Client) o;
		return s.equals(client.s);
	}

	@Override
	public int hashCode() {
		return s.hashCode();
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return s.toString();
	}
}
