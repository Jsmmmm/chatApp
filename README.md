# chatApp

This is multithreaded chat and server application. 
The server application doesn't have graphical interface and is run from command line. Server listens by default port 25000,
but it can be given an integer value by command line start up argument and it will use that port number instead.
The Server waits for connections to server socket and starts a new thread for every connecting client and stores them in arraylist.
Server listens for messages from clients and broadcasts them to all connected clients. Server also sends and updates
ArrayList of usernames online to connected clients.

The chat client GUI is created with JavaFX. After connecting to server, the chat client creates new thread that
listens server and writes received messages to textarea in GUI. The client also shows users online in server in ListView.

Login and signup features are not implemented yet.


http://imgur.com/a/XIf8p