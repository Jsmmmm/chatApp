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
	private ArrayList<ClientThread> al;	
	private SimpleDateFormat time;
	private int port;	
	private boolean serverOn;
	
	
	public JavaServer(int port){
		this.port = port;
		time = new SimpleDateFormat("HH:mm:ss");
		al = new ArrayList<ClientThread>();		
	}
	
	
	public void start(){
		
		serverOn = true;
		
		try{
			ServerSocket serverSocket = new ServerSocket(port);
						
			while(serverOn){
				System.out.println("Server is waiting for clients on port: "+port);
				Socket clientSocket = serverSocket.accept();	//accept connection
				
				if(!serverOn){
					break;
				}
				
				ClientThread clientThread = new ClientThread(clientSocket);	//make a thread of it
				al.add(clientThread);	//save it in the arraylist
				clientThread.start();			
				
			}
			
			try{
				serverSocket.close();
				for(int i = 0; i<al.size(); i++){
					ClientThread cT = al.get(i);
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
			
		}catch(Exception e){
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
		
		display("Server console:"+message);//print to server console
		
		//loop in reverse order in case we would have to remove a client because it has disconnected
		for(int i = al.size();--i >= 0;){
			ClientThread cT = al.get(i);
			if(!cT.writeMsg(message)){
				al.remove(i);				
			}
		}		
	}
	
	//for client who logoff using the logout message
	synchronized void remove(int id){
		for(ClientThread cT : al){
			if(cT.id == id){
				al.remove(cT);
				return;
			}
		}
	}
	
	protected void stop(){
		serverOn = false;
		
	}
	
	public static void main(String[] args){
	
	int portNumber = 25000;
		
	JavaServer javaServer = new JavaServer(portNumber);
	javaServer.start();

	}
	
	
	//this thread will run for each client
	class ClientThread extends Thread{
		
		
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		
		int id;
		
		String username;
		
		String message;
		
		String date;
		
		ClientThread(Socket socket){
			id = connectionId++;
			this.socket = socket;
			
			
			//creating both data stream
			try{				
				output = new ObjectOutputStream(socket.getOutputStream());
				input = new ObjectInputStream(socket.getInputStream());
				username = (String) input.readObject();				
			}catch(Exception e){
				e.printStackTrace();
				return;
			}
			
		}
		
		//will run forever until logout
		public void run(){
			boolean clientConnectionOn = true;
			//broadcast(username+" connected");
			while(clientConnectionOn){
				try{
					message = (String) input.readObject();
				}
				catch(Exception e){
					e.printStackTrace();
				}
				if(message.equalsIgnoreCase("/quit")){ //user disconnects			
					display(username+" disconnected.");
					broadcast(username+" disconnected");
					clientConnectionOn = false;
					
					break;
				}
				
				broadcast(username+": "+message);
			}
			
			remove(id);
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
}
