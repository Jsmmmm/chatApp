# chatApp

This is simple multithreaded chat and server application that is created using JavaFX.
The server application doesn't have graphical interface and is run from commandline. Server listens by default port 25000
but it can be given another port by giving it on startup an integer value by commandline argument. Server starts new thread for every
client and stores them in arraylist. The chat client creates new thread that listens server. Server broadcasts messages to all connected clients.

Login and signup features are not implemented yet.


http://imgur.com/a/XIf8p