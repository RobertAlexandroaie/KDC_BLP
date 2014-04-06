/**
 * 
 */
package model.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import util.Constants;
import util.MMCodec;

/**
 * @author Robert
 * 
 */
@SuppressWarnings("restriction")
public class ServiceSvc extends Thread {

    Key KST;

    public ServiceSvc(Key KST) {
	this.KST = KST;
    }

    public void startCommunications() {
	Cipher cipher;
	Socket clientUsrSocket;
	BufferedReader inUsr = null;
	BufferedWriter outUsr = null;
	try {
	    cipher = Cipher.getInstance(Constants.DES3);
	    clientUsrSocket = new Socket(Constants.IP, Constants.USER_PORT);

	    inUsr = new BufferedReader(new InputStreamReader(clientUsrSocket.getInputStream()));
	    outUsr = new BufferedWriter(new OutputStreamWriter(clientUsrSocket.getOutputStream()));

	    String messageFromUser = inUsr.readLine();
	    if (messageFromUser != null && messageFromUser.length() > 0) {
		String data[] = MMCodec.decodeUserToServiceMessage(messageFromUser).split("-");
		String firstPartCrypted = data[0];
		String secondPartCrypted = data[1];

		String firstPart = MMCodec.decodeUserToServiceFirstPart(cipher, KST, firstPartCrypted);
		String firstPartData[] = firstPart.split("-");

		BASE64Decoder decoder = new BASE64Decoder();
		Key K = new SecretKeySpec(decoder.decodeBuffer(firstPartData[0]), Constants.DES3);
		String secondPart = MMCodec.decodeUserToServiceSecondPart(cipher, K, secondPartCrypted);
		String secondPartData[] = secondPart.split("-");

		if (firstPartData[1].toLowerCase().equals(secondPartData[0].toLowerCase())) {
		    Calendar timestampCalendar = Calendar.getInstance();
		    Calendar currentCalendar = Calendar.getInstance();

		    int difference = timestampCalendar.get(Calendar.SECOND) - currentCalendar.get(Calendar.SECOND);
		    if (difference < Short.parseShort(firstPartData[2])) {
			outUsr.write(MMCodec.codeServiceToUserMessage(secondPartData[1], Short.toString(Short.parseShort(firstPartData[2])), cipher,
				K));
			outUsr.flush();
		    }
		}
	    }

	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
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
		if (outUsr != null) {
		    outUsr.close();
		}
		if (inUsr != null) {
		    inUsr.close();
		}
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    @Override
    public void run() {
	startCommunications();
    }
}
