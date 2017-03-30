package application.sockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import application.controller.MainController;

public class ClientSocket extends Thread{

	
	private Socket socket = null;
	private BufferedReader bufferedReader = null;
	private PrintWriter printWriter = null;
	
	MainController main;
	
	public void setMainController(MainController main){
		this.main = main;
	}
	
	
	public void connectToServer(final String address, final int port){
		try{
			if(socket == null){
				socket = new Socket(address, port);
			}
			
			if(bufferedReader == null){
				bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			if(printWriter == null){
				printWriter = new PrintWriter(socket.getOutputStream(), true);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void sendMessage(String message){
		printWriter.println(message);
	}
	
	
	public String receiveMessage(){
		
		String message = "";
		
		try{
			message = bufferedReader.readLine();
		}catch(IOException e){
			e.printStackTrace();
		}
		return message;		
	}
	
	
	public void run(){
		
		while(!isInterrupted()){
			
			try{
				String message = receiveMessage();
				main.updateReceivedMessage(message);
				Thread.sleep(10);
				
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
	}
	
	public void disconnect(){
		
		try{
			this.interrupt();
			
			if(printWriter != null){
				
				Thread.sleep(500);
				printWriter.close();
			}
			if(bufferedReader != null){
				bufferedReader.close();
			}
			if(socket != null){
				socket.close();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	
}
