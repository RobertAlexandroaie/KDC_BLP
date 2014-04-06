package main;

import model.Server;

public class ServerThread extends Thread {
    
    private Server server;
    
    public ServerThread(Server server){
	this.server = server;
    }
    
    @Override
    public void run() {
	server.startListening();
    }
}
