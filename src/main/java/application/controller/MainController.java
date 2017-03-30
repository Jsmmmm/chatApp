package application.controller;

import application.sockets.ClientSocket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class MainController implements Runnable{

	

    @FXML
    private TextField hostAddress;

    @FXML
    private TextField hostPort;

    @FXML
    private Button connect;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button login;

    @FXML
    private Button signup;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField message;

    @FXML
    private Button send;
    
    @FXML
    private Button disconnect;
    
    ClientSocket clientSocket = null;
    
    private volatile String receivedMessage;
    
    
    
    public void run(){
    	textArea.appendText(receivedMessage+"\n");
    }
    
    
    
    private synchronized void setReceivedMessage(String message){
    	this.receivedMessage = message;
    }
    
    
    public void updateReceivedMessage(String message){
    	
    	setReceivedMessage(message);
    	run();
    }
   
    
    @FXML
    private void connect(ActionEvent e){
    	
    	if(clientSocket == null){
    		
    		clientSocket = new ClientSocket();
    		if(clientSocket != null){
    			
    			clientSocket.setMainController(this);
    			   			
    			clientSocket.connectToServer(hostAddress.getText(), Integer.parseInt(hostPort.getText()));
    			clientSocket.start();    			
    		}
    	}
    	//connect.setDisable(true);
    	//send.setDisable(false);
    }
    
    @FXML
    private void disconnectFromServer(ActionEvent e){
    	clientSocket.disconnect();
    	clientSocket = null;
    }
    
    @FXML
    private void signUp(){
    	
    }
    
    
    @FXML
    private void login(){
    	
    }
    
    
    @FXML
    private void sendMessage(){
    	clientSocket.sendMessage(message.getText());
    	message.setText("");    	
    }
    
    
}
