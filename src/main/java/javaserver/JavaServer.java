package javaserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class JavaServer extends Thread{

	private ServerSocket serverSocket;
	private Socket clientSocket;
	//private boolean keepGoingServer=true;
	
	
	
	public JavaServer(int port){
		try{
			serverSocket = new ServerSocket(port);
			clientSocket = null;			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void run(){
		
		while(true){
			
			if(serverSocket == null){
				return;
			}
			
			try{
				clientSocket = serverSocket.accept();
					
				InputStreamReader input = new InputStreamReader(clientSocket.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(input);				
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
								
				
				while(true){
					welcomeMessage(out);
					String line = bufferedReader.readLine();
					
					if(line.length()>=0){
						
						if(line.equals("quit")){
							break;
						}
						System.out.println("printtaa server konsoliin: "+line);
						out.println(line);
					}
				}
				out.println("Closing connection");
				input.close();
				bufferedReader.close();
				out.close();
				clientSocket.close();
			//	System.exit(0);
				
				}catch(IOException io){
					io.printStackTrace();
				}
			}				
		}
	
	public static void main(String[] args){
		JavaServer server = new JavaServer(25000);
		server.start();
	}
	
	private void welcomeMessage(PrintWriter out){
		if(out != null)
		out.println("Server: Welcome");
	}
	
	/*private void closeConnection(){
		input.close();
		bufferedReader.close();
		clientSocket.close();
		System.exit(0);
	}*/
}
