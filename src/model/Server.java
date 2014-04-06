/**
 * 
 */
package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import main.KDCSystem;
import util.Constants;
import util.MMCodec;

/**
 * @author Robert
 * 
 */

public class Server extends Thread {

    private Socket clientUsrSocket = null;
    private ServerSocket serverSocket = null;
    private Cipher cipher = null;
    private KDCSystem system = null;

    public Server() {
	try {
	    cipher = Cipher.getInstance(Constants.DES3);
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void run() {
	startCommunication();
    }

    private void startCommunication() {
	String messageFromUser = null;
	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(clientUsrSocket.getInputStream()));
	    PrintWriter out = new PrintWriter(new OutputStreamWriter(clientUsrSocket.getOutputStream()));

	    messageFromUser = in.readLine();
	    if (messageFromUser != null && messageFromUser.length() > 0) {
		String data[] = MMCodec.decodeUserToServerMessage(messageFromUser).split("-");
		String user = data[0];
		String service = data[1];
		String nonce = data[2];

		if (system.containsUser(user) && system.containsService(service) && system.isPermitted(user, service)) {
		    int L = Constants.LIFETIME;
		    Key KUT = system.userKeyByUserName(user);
		    Key KST = system.serviceKeyByServiceName(service);

		    out.print(MMCodec.codeServerToUserMessage(nonce, L, service, user, KUT, KST, cipher));
		    out.flush();
		} else {
		    out.write(MMCodec.codeServerToUserFail());
		}
	    }
	} catch (IOException e) {
	    System.err.println("Eroare de citire/scriere \n" + e);
	} finally {
	    try {
		clientUsrSocket.close();
	    } catch (IOException e) {
		System.err.println("Socketul nu poate fi inchis \n" + e);
	    }
	}
    }

    public void setSystem(KDCSystem system) {
	this.system = system;
    }

    public void startListening() {
	try {
	    serverSocket = new ServerSocket(Constants.SERVER_PORT);
	    while (true) {
		clientUsrSocket = serverSocket.accept();
		new Thread(this).start();
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
