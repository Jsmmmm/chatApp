package application.controller;


import application.sockets.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MainController{

	
	@FXML
	private Circle circle;
	
    @FXML
    private TextField hostAddress;

    @FXML
    private TextField hostPort;

    @FXML
    private Button connectButton;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private TextArea textArea;

    @FXML
    private TextField messageField;

    @FXML
    private Button sendButton;
    
    @FXML
    private Button disconnectButton;
    
    Client client;
    
   // private boolean connected;
    
    @FXML
    private void initialize(){
    	setConnectionStatusUI(false);
    }
    
    public void showMessage(String message){
    	textArea.appendText(message+"\n");   
    }
   
    public void setConnectionStatusUI(Boolean connected){
    	if(connected){
    		circle.setFill(Color.GREEN);
    		connectButton.setDisable(true);   		
    		disconnectButton.setDisable(false);
    		username.setDisable(true);
    		password.setDisable(true);
    		
    	}else{
    		circle.setFill(Color.RED);
    		disconnectButton.setDisable(true);
    		connectButton.setDisable(false);
    		username.setDisable(false);
    		password.setDisable(false);
    	}
    	
    }
    
    @FXML
    private void connect(ActionEvent e){
    	client = new Client(hostAddress.getText(), Integer.parseInt(hostPort.getText()), username.getText(), this);
    	if(!client.start()){
    		return; //     <- test if we can start the client
    	}   	
    	//connected = true;
    	setConnectionStatusUI(true);
    }
    
    @FXML
    private void disconnectFromServer(ActionEvent e){
    	client.sendMessage("/quit");
    	
    	//connected = false;
    	client.disconnect();
    	setConnectionStatusUI(false);
    }
    
    @FXML
    private void signUp(){
    	
    }
    
    
    @FXML
    private void login(){
    	
    }
    
    
    @FXML
    private void sendMessage(){
    	String msg = messageField.getText();
    
    	if((client != null) && (!msg.isEmpty())){
    		client.sendMessage(msg);
    		messageField.setText("");
    	}
    }
    
    
}
