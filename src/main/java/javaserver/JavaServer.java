package javaserver;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JavaServer{
	
	private static int connectionId;
	private ArrayList<ClientThread> clientThreadsList;	
	private SimpleDateFormat time;
	private int port;	
	private boolean serverOn;
	
	
	public JavaServer(int port){
		this.port = port;
		time = new SimpleDateFormat("HH:mm:ss");
		clientThreadsList = new ArrayList<ClientThread>();		
	}
	
	public static void main(String[] args){
		
		int portNumber = 25000;
		
		switch(args.length) {
		
		case 1: try{
					portNumber = Integer.parseInt(args[0]);
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
		case 0:
			break;					
		}

		
		JavaServer javaServer = new JavaServer(portNumber);
		javaServer.start();

	}
	
	
	
	public void start(){
		
		serverOn = true;
		
		try{
			ServerSocket serverSocket = new ServerSocket(port);
						
			while(serverOn){
				display("Server is waiting for clients on port: "+port);
				
				Socket clientSocket = serverSocket.accept();	//accept connection
				
				if(!serverOn){
					break;
				}
				
				ClientThread clientThread = new ClientThread(clientSocket);	//make a thread of it, ClientThread is inner class
				clientThreadsList.add(clientThread);	//save it in the arraylist
				clientThread.start();
				refreshUsersOnlineToClients(); 
			}
			
			//was asked to stop
			try{
				serverSocket.close();
				for(int i = 0; i < clientThreadsList.size(); i++){
					ClientThread cT = clientThreadsList.get(i);
					try{
						cT.input.close();
						cT.output.close();
						cT.socket.close();
					}catch(IOException e){
						e.printStackTrace();
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * display an event(not message) to the server console
	 */
	private void display(String message){
		
		String messageTime = time.format(new Date())+" "+message;
		System.out.println(messageTime);
	}
	
	
	//broadcast message to all clients
	private synchronized void broadcast(String msg){
		String messageTime = time.format(new Date());
		String message = messageTime+" "+msg+"\n";
		
		//print to server console
		System.out.println(message);
		
		//loop in reverse order in case we would have to remove a client because it has disconnected
		
		for(int i = clientThreadsList.size(); --i >= 0;){			
			ClientThread cT = clientThreadsList.get(i);
			if(!cT.writeMsg(message)){
				clientThreadsList.remove(i);
				display("Disconnected client "+cT.username + "removed from chat");
				//refreshUsersOnlineToClients(); USERS GUI
			}
		}		
	}
	
	//for client who logoff using the logout message
	synchronized void remove(int id){
		for(ClientThread clientThread : clientThreadsList){
			if(clientThread.id == id){
				clientThreadsList.remove(clientThread);
				return;
			}
		}
		
	}
	
	protected void stop(){
		serverOn = false;
		
	}
	
	//testi oo valmis poistamaan
	private synchronized void refreshUsersOnlineToClients(){
		
		//arraylist of usernames online
		ArrayList<String> users = new ArrayList();
		
		
		for(int k = clientThreadsList.size(); --k >= 0;){
			users.add(clientThreadsList.get(k).username);			
		}		
		

		for(int i = clientThreadsList.size(); --i >= 0;){
			ClientThread cT = clientThreadsList.get(i);
			cT.sendClientArrayListOfUsersOnline(users);											
		}		
	}
	
	
	
	//this thread will run for each client
	class ClientThread extends Thread{
		
		
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		
		int id;
		
		String username;
		
		String chatMessage;
		
		String date;
		
		ClientThread(Socket socket){
			id = ++connectionId;
			this.socket = socket;
			
			//poista
			System.out.println("Thread trying to create object input/outputs streams");
			//creating both data stream
			try{				
				output = new ObjectOutputStream(socket.getOutputStream());
				input = new ObjectInputStream(socket.getInputStream());
				username = (String) input.readObject();
				//display(username+" connected");
				broadcast(username+" connected");
			}catch(Exception e){
				e.printStackTrace();
				return;
			}			
		}
		
		//will run until logout
		public void run(){
			
			boolean clientConnectionOn = true;
			
			while(clientConnectionOn){
				try{
					chatMessage = (String) input.readObject();
				}
				catch(IOException e){
					display(username + "exception reading streams: "+e);
					break;
				}
				catch(ClassNotFoundException e){
					//its (String), should work..
					break;
				}
				
				if(chatMessage.equalsIgnoreCase("/quit")){ //user disconnects			
					//display(username+" disconnected.");
					broadcast(username+" disconnected");
					clientConnectionOn = false;
					
					
				}else{
					broadcast(username+": "+chatMessage);
					
				}				
			}
			
			remove(id);	//remove disconnected clientThread by id from arraylist
			close();
			refreshUsersOnlineToClients();
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
		
		
		//write a string to this client threads outputstream
		protected boolean writeMsg(String msg){
			//if client is still connected send message to it
			if(!socket.isConnected()){
				close();
				return false;
			}
			
			try{
				output.writeObject(msg);
			}catch(IOException e){
				display("Error sending message to "+username);
			}
			return true;
		}
		
		//testi, oo valmis poistamaan LAITA TÄÄ METODI LÄHETTÄMÄÄN ARRAY LÄSNÄOLIJOISTA KAIKILLE LÄSNÄOLIJOILLE
		protected boolean sendClientArrayListOfUsersOnline(ArrayList<String> users){
			if(!socket.isConnected()){
				close();
				return false;
			}
			try{
				output.writeObject(users);
			}catch(Exception e){
				e.printStackTrace();
			}
			return true;
		}
	}
}
