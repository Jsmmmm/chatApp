package application.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import application.controller.ClientGUIController;
import javafx.application.Platform;

public class Client {

	private ObjectInputStream input;
	private ObjectOutputStream output;
	Socket socket;
	ClientGUIController gui;
	private String serverIp, username;
	private int port;
	ArrayList<String> users;
	
	public Client(String serverIp, int port, String username, ClientGUIController main){
		this.serverIp = serverIp;
		this.port = port;
		this.username = username;
		this.gui = main;		
	}
	
	
	public boolean start(){
		try{
			socket = new Socket(serverIp, port);					
		}
		catch(Exception e){	
			display("Error connectin to server");
			return false;
		}
		
		String msg = "Connection accepted: "+socket.getInetAddress()+":"+socket.getPort();
		display(msg);//display prints only to clients own textwindow
		try{
			input  = new ObjectInputStream(socket.getInputStream());
			output = new ObjectOutputStream(socket.getOutputStream());

		}catch(Exception e){
			display("Error in creating input and output streams");
			return false;
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
			display("Exception when trying to write to server");
		}
	}
	
	private void display(String msg){
		gui.showMessage(msg);
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
			gui.setConnectionStatusUI(false);
		}			
	}


	//client side thread that listens server
	class ListenFromServer extends Thread{

		public void run(){
			
			while(true){
				
				try{
					try {
						Object object = input.readObject();
						
						if(object instanceof ArrayList){
							users = (ArrayList<String>) object;
							
							//Platform.runLater is used to update ui from different thread
							//http://java-buddy.blogspot.fi/2012/06/update-ui-in-javafx-application-thread.html
							  Platform.runLater(new Runnable(){
	                                @Override
	                                public void run() {
	                                    gui.updateListView(users);
	                                }
	                            });							
						}else{		//if received object is not Arraylist it must be message						
							String message = (String) object;							
							gui.showMessage(message);
						}
						
					} catch (ClassNotFoundException e) {						
						//irrelevant
					}
			
				}catch(IOException e){
					gui.setConnectionStatusUI(false);
					break;
				}
				
					
					
				}
			
		}
	}

	
}


