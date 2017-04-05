package application.controller;


import java.util.ArrayList;

import application.client.Client;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class ClientGUIController{

	
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
    
    @FXML
    private ListView<String> usersListView;
    
    Client client;
    
    ArrayList<String> users;
    
    private boolean connected;
    
    @FXML
    private void initialize(){
    	setConnectionStatusUI(false);
    }
    
    public void showMessage(String message){
    	textArea.appendText(message+"\n");   
    }
    
    //rename
    void connectionFailed(){
    	circle.setFill(Color.RED);
		disconnectButton.setDisable(true);
		connectButton.setDisable(false);
		username.setDisable(false);
		password.setDisable(false);
		connected = false;
    }
   
    public void setConnectionStatusUI(Boolean isConnected){
    	if(isConnected){
    		circle.setFill(Color.GREEN);
    		connectButton.setDisable(true);   		
    		disconnectButton.setDisable(false);
    		username.setDisable(true);
    		password.setDisable(true);
    		connected = true;
    		
    	}else{
    		circle.setFill(Color.RED);
    		disconnectButton.setDisable(true);
    		connectButton.setDisable(false);
    		username.setDisable(false);
    		password.setDisable(false);
    		connected = false;
    		usersListView.getItems().clear();
    		
    	}
    	
    }
    
    @FXML
    private void connect(ActionEvent e){
	
    	client = new Client(hostAddress.getText(), Integer.parseInt(hostPort.getText()), username.getText(), this);
    	if(!client.start()){
    		return; //     <- test if we can start the client
    	}   	
    	connected = true;
    	setConnectionStatusUI(true);
    }
    
    @FXML
    private void disconnectFromServer(ActionEvent e){
    	if(connected){
    		client.sendMessage("/quit");
        	
        	//connected = false;
        	//client.disconnect();
        	setConnectionStatusUI(false);
    	}  	
    }
    
    @FXML
    private void signUp(){
    	
    }
    
    
    @FXML
    private void login(){
    	
    }
    
   
    public void updateListView(ArrayList<String> users){
    	ObservableList<String> items = FXCollections.observableArrayList(users);
    	if(users.isEmpty()){
    		usersListView.getItems().clear();
    	}else{
    		usersListView.setItems(items);
    	}
    	
    }
    
    @FXML
    private void sendMessage(){
    	if(connected){
    		String msg = messageField.getText();
    		if((client != null) && (!msg.isEmpty())){
        		client.sendMessage(msg);
        		messageField.setText("");
        	}
    	}
    	
    
    	
    }
    
    
}
