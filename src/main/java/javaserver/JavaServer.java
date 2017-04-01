package javaserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JavaServer{
	
	protected static int connectionId;
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
				Socket socket = serverSocket.accept();	//accept connection
				
				if(!serverOn){
					break;
				}
				
				ClientThread clientThread = new ClientThread(socket, this);	//make a thread of it
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
	protected void display(String message){
		
		String messageTime = time.format(new Date())+" "+message;
		System.out.println(messageTime);
	}
	
	
	//broadcast message to all clients
	protected synchronized void broadcast(String msg){
		String messageTime = time.format(new Date());
		String message = messageTime+" "+msg+"\n";
		
		display("Server console:"+message);//print to server console
		
		//loop in reverse order in case we would have to remove a client because it has disconnected
		for(int i = al.size();--i >= 0;){
			ClientThread cT = al.get(i);
			if(!cT.writeMsg(message)){
				al.remove(i);
				display("Disconnected client removed from list");
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
	
	public static void main(String[]args){
		
	int portNumber = 25000;
	
	JavaServer javaServer = new JavaServer(portNumber);
	javaServer.start();

	}
}
