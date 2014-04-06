package util;

import java.security.InvalidKeyException;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class Cryptor {
    public static byte[] encrypt(Cipher cipher, String plaintext, Key key) {
	try {
	    cipher.init(Cipher.ENCRYPT_MODE, key);
	    byte[] inputBytes = plaintext.getBytes();
	    return cipher.doFinal(inputBytes);
	} catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BadPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }

    public static String decrypt(Cipher cipher, byte[] criptotext, Key key) {
	try {
	    cipher.init(Cipher.DECRYPT_MODE, key);
	    byte[] decriptedBytes = cipher.doFinal(criptotext);
	    String decriptedText = new String(decriptedBytes);
	    return decriptedText;
	} catch (InvalidKeyException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalBlockSizeException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (BadPaddingException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return null;
    }
}
