package javaserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientThread extends Thread{
	
	JavaServer server;
	Socket socket;
	ObjectInputStream input;
	ObjectOutputStream output;
	
	int id;
	
	String username;
	
	String message;
	
	String date;
	
	ClientThread(Socket socket, JavaServer javaServer){
		id = JavaServer.connectionId++;
		this.socket = socket;
		server = javaServer;
		
		//creating both data stream
		try{
			
			output = new ObjectOutputStream(socket.getOutputStream());
			input = new ObjectInputStream(socket.getInputStream());
			username = (String) input.readObject();
			server.display(username+" connected");
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
	}
	
	//will run forever until logout
	public void run(){
		boolean keepGoing = true;
		
		while(keepGoing){
			try{
				message = (String) input.readObject();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			if(message.equalsIgnoreCase("/quit")){ //user disconnects			
				server.display(username+" disconnected.");
				server.broadcast(username+" disconnected");
				keepGoing = false;
				break;
			}
			
			server.broadcast(username+": "+message);
		}
		
		server.remove(id);
		close();
	}
	
	private void close(){
		try{
			if(output != null){
				output.close();
			}
			if(input != null){
				input.close();
			}
			if(socket != null){
				socket.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//write a string to client outputstream
	protected boolean writeMsg(String msg){
		//if client is still connected send the mesage to it
		if(!socket.isConnected()){
			close();
			return false;
		}
		
		try{
			output.writeObject(msg);
		}catch(IOException e){
			//display("Error sending message to "+username);
		}
		return true;
	}
	
	
	
}
