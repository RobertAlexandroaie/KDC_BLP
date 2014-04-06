/**
 * 
 */
package model.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import main.KDCSystem;
import sun.misc.BASE64Decoder;
import util.Constants;
import util.MMCodec;

/**
 * @author Robert
 * 
 */
@SuppressWarnings("restriction")
public class UserSvc extends Thread {

    private Key KUT;
    private String serviceName;
    private String userName;
    private KDCSystem system;

    public UserSvc(Key KUT, String serviceName, String userName, KDCSystem system) {
	this.KUT = KUT;
	this.serviceName = serviceName;
	this.userName = userName;
	this.system = system;
    }

    @Override
    public void run() {
	startCommunications();
    }

    public void startCommunications() {
	Cipher cipher;
	Socket clientSrvSocket;
	Socket clientSvcSocket;
	ServerSocket serverSocket;
	BufferedReader inSrv = null;
	BufferedWriter outSrv = null;
	BufferedReader inSvc = null;
	BufferedWriter outSvc = null;
	try {
	    clientSrvSocket = new Socket(Constants.IP, Constants.SERVER_PORT);
	    cipher = Cipher.getInstance(Constants.DES3);

	    String messageFromServer = null;

	    inSrv = new BufferedReader(new InputStreamReader(clientSrvSocket.getInputStream()));
	    outSrv = new BufferedWriter(new OutputStreamWriter(clientSrvSocket.getOutputStream()));

	    long nonce = 10000000 + (long) (Math.random() * 99999999);
	    String messageToServer = MMCodec.codeUserToServerMessage(userName, serviceName, nonce);
	    outSrv.write(messageToServer);
	    outSrv.flush();

	    messageFromServer = inSrv.readLine();
	    String message = MMCodec.decodeServerToUserMessage(messageFromServer);
	    if (!message.contains(Constants.FAIL)) {
		String[] data = message.split("-");
		String firstPart = data[0];
		String secondPart = data[1];

		String[] firstPartData = MMCodec.decodeServerToUserFirstPart(firstPart, cipher, KUT).split("-");

		if (serviceName.toLowerCase().equals(firstPartData[3].toLowerCase()) && nonce == Long.parseLong(firstPartData[1])) {
		    Calendar calendar = Calendar.getInstance();
		    SimpleDateFormat sdf = new SimpleDateFormat(Constants.PREF_DATE_FORMAT);
		    String timestamp = sdf.format(calendar.getTime());

		    serverSocket = new ServerSocket(Constants.USER_PORT);
		    clientSvcSocket = serverSocket.accept();

		    Key KST = system.serviceKeyByServiceName(serviceName);
		    ServiceSvc serviceSvc = new ServiceSvc(KST);
		    try {
			Thread.sleep(500);
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    new Thread(serviceSvc).start();

		    inSvc = new BufferedReader(new InputStreamReader(clientSvcSocket.getInputStream()));
		    outSvc = new BufferedWriter(new OutputStreamWriter(clientSvcSocket.getOutputStream()));

		    String encK = firstPartData[0];
		    String L = firstPartData[2];
		    String messageToService = MMCodec.codeUserToServiceMessage(secondPart, encK, userName, timestamp, L, cipher);

		    outSvc.write(messageToService);
		    outSvc.flush();

		    String messageFromService = inSvc.readLine();
		    if (messageFromService != null && messageFromService.length() > 0) {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] key = decoder.decodeBuffer(encK);
			Key K = new SecretKeySpec(key, Constants.DES3);

			String[] confirmationData = (MMCodec.decodeServiceToUserMessage(messageFromService, cipher, K)).split("-");
			if (Short.parseShort(confirmationData[1]) >= 0) {
			    System.out.println("User successfuly accessed service.");
			}
		    }
		}
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (NoSuchPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		if (inSrv != null) {
		    inSrv.close();
		}
		if (outSrv != null) {
		    outSrv.close();
		}
		if (outSvc != null) {
		    outSvc.close();
		}
		if (inSvc != null) {
		    inSvc.close();
		}
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
