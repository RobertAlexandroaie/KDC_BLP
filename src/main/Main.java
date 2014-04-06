/**
 * 
 */
package main;

import model.Server;

/**
 * @author Robert
 * 
 */
public class Main {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
	Server server = new Server();
	KDCSystem system = new KDCSystem(server);
	
	new Thread(new ServerThread(server)).start();
	system.userAccessService("Robert", "Service1");
    }
}
