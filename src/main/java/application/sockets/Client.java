package application.sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import application.controller.MainController;

public class Client {

	private ObjectInputStream input;
	private ObjectOutputStream output;
	Socket socket;
	MainController main;
	private String serverIp, username;
	private int port;
	
	
	public Client(String serverIp, int port, String username, MainController main){
		this.serverIp = serverIp;
		this.port = port;
		this.username = username;
		this.main = main;		
	}
	
	
	public boolean start(){
		try{
			socket = new Socket(serverIp, port);					
		}
		catch(Exception e){			
		}
		
		String msg = "Connection accepted: "+socket.getInetAddress()+":"+socket.getPort();
		display(msg);//pitäsköhä tää lukee clientin sijasta myös muille käyttäjille eli broadcastata?
		try{
			input  = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());

		}catch(Exception e){
			
		}
		
		//creates thread to listen from server
		new ListenFromServer().start();
		
		try	{
			output.writeObject(username);
		}
		catch(IOException e){
			disconnect();
			return false;
		}
		
		return true;
	}
	
	public void sendMessage(String message){		
		
		try{
			output.writeObject(message);			
		}catch(IOException e){
			
		}
	}
	
	private void display(String msg){
		main.showMessage(msg);
	}
	
	public void disconnect(){
		if(input != null){
			try {
				input.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
		if(output!=null){
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
	}
	
	//inner class: client side thread that listens server
	class ListenFromServer extends Thread{

		public void run(){
			
			while(true){
				
				try{
					try {
						String message = (String) input.readObject();
						main.showMessage(message);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}catch(IOException e){
					//main.kirjota textarea server has closed connection
					break;
				}
			}
		}
	}

	
}


