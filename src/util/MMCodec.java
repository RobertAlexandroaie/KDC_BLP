package util;

import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class MMCodec {

    // User to Server
    // encoding
    public static String codeUserToServerMessage(String user, String service, long nonce) {
	return getEncodedWord(user) + getEncodedWord(service) + Long.toString(nonce);
    }

    // decoding
    public static String decodeUserToServerMessage(String message) {
	if (message != null && message.length() > 0) {
	    int index = 0;

	    int sizeOfU = 0;
	    int sizeOfS = 0;
	    int startS = 0;

	    String decodedMessage = "";

	    for (index = 0; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }

	    sizeOfU = Integer.parseInt(message.substring(0, index));
	    decodedMessage += getWord(message, index, sizeOfU);
	    index += sizeOfU;
	    startS = index;

	    for (; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }

	    sizeOfS = Integer.parseInt(message.substring(startS, index));
	    decodedMessage += "-" + getWord(message, index, sizeOfS);
	    index += sizeOfS;

	    decodedMessage += "-" + message.substring(index, message.length());
	    return decodedMessage;
	} else {
	    return "";
	}
    }

    // ---------------------------------------------------------------------------------------

    // Server to User
    // encoding
    public static String codeServerToUserFail() {
	return "0-" + getEncodedWord(Constants.FAIL);
    }

    public static String codeServerToUserMessage(String nonce, int L, String service, String user, Key KUT, Key KST, Cipher cipher) {
	String message = "";
	String encodedKey = getEncodedKey();
	String firstPart = MMCodec.codeServerToUserFirstPart(encodedKey, nonce, L, service);
	String secondPart = MMCodec.codeServerToUserSecondPart(encodedKey, user, L);

	byte[] firstPartEncrypted = Cryptor.encrypt(cipher, firstPart, KUT);
	byte[] secondPartEncrypted = Cryptor.encrypt(cipher, secondPart, KST);

	BASE64Encoder encoder = new BASE64Encoder();
	message += getEncodedWord(encoder.encode(firstPartEncrypted)) + getEncodedWord(encoder.encode(secondPartEncrypted));
	return "1-" + message;
    }

    private static String codeServerToUserFirstPart(String K, String nonce, int L, String service) {
	return getEncodedWord(K) + getEncodedWord(nonce) + getEncodedWord(L) + getEncodedWord(service);
    }

    private static String codeServerToUserSecondPart(String K, String user, int L) {
	return getEncodedWord(K) + getEncodedWord(user) + getEncodedWord(L);
    }

    // decoding
    public static String decodeServerToUserFirstPart(String firstPart, Cipher cipher, Key KUT) {
	String message = new String(Cryptor.decrypt(cipher, firstPart.getBytes(), KUT));
	if (message != null && message.length() > 0) {
	    int index = 0;

	    int sizeOfEncK = 0;
	    int sizeOfNonce = 0;
	    int sizeOfL = 0;
	    int sizeOfS = 0;

	    int startIndex = 0;
	    String decodedMessage = "";

	    for (index = 0; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfEncK = Integer.parseInt(message.substring(0, index));
	    decodedMessage += getWord(message, index, sizeOfEncK);
	    index += sizeOfEncK;
	    startIndex = index;

	    for (; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfNonce = Integer.parseInt(message.substring(startIndex, index));
	    decodedMessage += "-" + getWord(message, index, sizeOfNonce);
	    index += sizeOfNonce;
	    startIndex = index;

	    for (; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfL = Integer.parseInt(message.substring(startIndex, index));
	    decodedMessage += "-" + getWord(message, index, sizeOfL);
	    index += sizeOfL;
	    startIndex = index;

	    for (; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfS = Integer.parseInt(message.substring(startIndex, index));
	    decodedMessage += "-" + getWord(message, index, sizeOfS);
	    index += sizeOfS;
	    startIndex = index;

	    return decodedMessage;
	} else {
	    return "";
	}
    }

    public static String decodeServerToUserMessage(String message) {
	String decodedMessage = "";

	if (message.startsWith("1")) {
	    try {
		int index = 0;

		int sizeOfFirstPart = 0;
		int sizeOfSecondPart = 0;

		int startIndex = 0;
		String firstPartEncoded = "";
		String secondPartEncoded = "";

		for (index = 2; index < message.length(); index++) {
		    if (!Character.isDigit(message.charAt(index))) {
			break;
		    }
		}
		sizeOfFirstPart = Integer.parseInt(message.substring(2, index));
		firstPartEncoded += getWord(message, index, sizeOfFirstPart);
		index += sizeOfFirstPart;
		startIndex = index;

		for (; index < message.length(); index++) {
		    if (!Character.isDigit(message.charAt(index))) {
			break;
		    }
		}
		sizeOfSecondPart = Integer.parseInt(message.substring(startIndex, index));
		secondPartEncoded += "-" + getWord(message, index, sizeOfSecondPart);
		index += sizeOfSecondPart;
		startIndex = index;

		BASE64Decoder decoder = new BASE64Decoder();
		byte[] cryptedFirstPart = decoder.decodeBuffer(firstPartEncoded);
		byte[] cryptedSecondPart = decoder.decodeBuffer(secondPartEncoded);

		decodedMessage += cryptedFirstPart.toString() + "-" + cryptedSecondPart.toString();
		return decodedMessage;
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {
	    decodedMessage = message.substring(2, message.length());
	}

	return decodedMessage;
    }

    // --------------------------------------------------------------------------------------------------------------------------------

    // User to Service
    // encoding
    public static String codeUserToServiceMessage(String firstPart, String encK, String user, String timestamp, String L, Cipher cipher) {
	try {
	    String message = "";
	    BASE64Decoder decoder = new BASE64Decoder();
	    byte[] keyBytes = decoder.decodeBuffer(encK);
	    Key K = new SecretKeySpec(keyBytes, Constants.DES3);

	    String plaintext = codeUserToServiceSecondPart(user, timestamp, L);
	    byte[] secondPart = Cryptor.encrypt(cipher, plaintext, K);

	    BASE64Encoder encoder = new BASE64Encoder();
	    message += getEncodedWord(firstPart) + getEncodedWord(encoder.encode(secondPart));
	    return message;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return "";
    }

    private static String codeUserToServiceSecondPart(String user, String timestamp, String L) {
	return getEncodedWord(user) + getEncodedWord(timestamp) + getEncodedWord(L);
    }

    // decoding
    public static String decodeUserToServiceMessage(String message) {
	String decodedMessage = "";

	try {
	    int index = 0;

	    int sizeOfFirstPart = 0;
	    int sizeOfSecondPart = 0;

	    int startIndex = 0;
	    String firstPartEncoded = "";
	    String secondPartEncoded = "";

	    for (index = 2; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfFirstPart = Integer.parseInt(message.substring(2, index));
	    firstPartEncoded += getWord(message, index, sizeOfFirstPart);
	    index += sizeOfFirstPart;
	    startIndex = index;

	    for (; index < message.length(); index++) {
		if (!Character.isDigit(message.charAt(index))) {
		    break;
		}
	    }
	    sizeOfSecondPart = Integer.parseInt(message.substring(startIndex, index));
	    secondPartEncoded += "-" + getWord(message, index, sizeOfSecondPart);
	    index += sizeOfSecondPart;
	    startIndex = index;

	    BASE64Decoder decoder = new BASE64Decoder();
	    byte[] cryptedFirstPart = decoder.decodeBuffer(firstPartEncoded);
	    byte[] cryptedSecondPart = decoder.decodeBuffer(secondPartEncoded);

	    decodedMessage += cryptedFirstPart.toString() + "-" + cryptedSecondPart.toString();
	    return decodedMessage;
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return decodedMessage;
    }

    public static String decodeUserToServiceFirstPart(Cipher cipher, Key KST, String firstPartCrypted) {
	String decodedMessage = "";
	String firstPartDecrypted = Cryptor.decrypt(cipher, firstPartCrypted.getBytes(), KST);

	int index = 0;
	int sizeOfEncK = 0;
	int sizeOfU = 0;
	int sizeOfL = 0;
	int startIndex = 0;

	for (index = 0; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfEncK = Integer.parseInt(firstPartDecrypted.substring(0, index));
	decodedMessage += getWord(firstPartDecrypted, index, sizeOfEncK);
	index += sizeOfEncK;
	startIndex = index;

	for (; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfU = Integer.parseInt(firstPartDecrypted.substring(startIndex, index));
	decodedMessage += "-" + getWord(firstPartDecrypted, index, sizeOfU);
	index += sizeOfU;
	startIndex = index;

	for (; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfL = Integer.parseInt(firstPartDecrypted.substring(startIndex, index));
	decodedMessage += "-" + getWord(firstPartDecrypted, index, sizeOfL);
	index += sizeOfL;
	return decodedMessage;
    }

    public static String decodeUserToServiceSecondPart(Cipher cipher, Key K, String secondPartCrypted) {
	String decodedMessage = "";
	String firstPartDecrypted = Cryptor.decrypt(cipher, secondPartCrypted.getBytes(), K);

	int index = 0;
	int sizeOfU = 0;
	int sizeOfTimepstamp = 0;
	int sizeOfL = 0;
	int startIndex = 0;

	for (index = 0; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfU = Integer.parseInt(firstPartDecrypted.substring(0, index));
	decodedMessage += getWord(firstPartDecrypted, index, sizeOfU);
	index += sizeOfU;
	startIndex = index;

	for (; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfTimepstamp = Integer.parseInt(firstPartDecrypted.substring(startIndex, index));
	decodedMessage += "-" + getWord(firstPartDecrypted, index, sizeOfTimepstamp);
	index += sizeOfTimepstamp;
	startIndex = index;

	for (; index < firstPartDecrypted.length(); index++) {
	    if (!Character.isDigit(firstPartDecrypted.charAt(index))) {
		break;
	    }
	}
	sizeOfL = Integer.parseInt(firstPartDecrypted.substring(startIndex, index));
	decodedMessage += "-" + getWord(firstPartDecrypted, index, sizeOfL);
	index += sizeOfL;
	return decodedMessage;
    }

    // Service to User
    // encoding
    public static String codeServiceToUserMessage(String timestamp, String L, Cipher cipher, Key K) {
	String codedMessage = "";

	codedMessage = getEncodedWord(timestamp) + getEncodedWord(L);
	byte[] codedMessageBytes = Cryptor.encrypt(cipher, codedMessage, K);

	codedMessage = codedMessageBytes.toString();

	return codedMessage;
    }

    // decoding
    public static String decodeServiceToUserMessage(String messageFromService, Cipher cipher, Key K) {
	String decodedMessage = "";
	String decryptedMessage = Cryptor.decrypt(cipher, messageFromService.getBytes(), K);

	int index = 0;

	int sizeOfTimestamp = 0;
	int sizeOfLifetime = 0;
	int startIndex = 0;

	for (index = 0; index < decryptedMessage.length(); index++) {
	    if (!Character.isDigit(decryptedMessage.charAt(index))) {
		break;
	    }
	}
	sizeOfTimestamp = Integer.parseInt(decryptedMessage.substring(2, index));
	decodedMessage += getWord(decryptedMessage, index, sizeOfTimestamp);
	index += sizeOfTimestamp;
	startIndex = index;

	for (; index < decryptedMessage.length(); index++) {
	    if (!Character.isDigit(decryptedMessage.charAt(index))) {
		break;
	    }
	}
	sizeOfLifetime = Integer.parseInt(decryptedMessage.substring(startIndex, index));
	decodedMessage += "-" + getWord(decryptedMessage, index, sizeOfLifetime);

	return decodedMessage;

    }

    // General
    static private String getWord(String message, int index, int sizeOfWord) {
	String word = "";

	if (index < message.length()) {
	    try {
		word += message.substring(index, index + sizeOfWord);
	    } catch (NumberFormatException e) {

	    }
	}
	return word;
    }

    private static String getEncodedWord(String word) {
	String encodedWord = "";
	encodedWord += Integer.toString(word.length()) + word;
	return encodedWord;
    }

    private static String getEncodedWord(int word) {
	return Integer.toString(Integer.toString(word).length()) + Integer.toString(word);
    }

    private static String getEncodedKey() {
	Random secureRandom = new SecureRandom();
	byte[] keyE = new byte[8];
	secureRandom.nextBytes(keyE);

	Random secureRandom2 = new SecureRandom();
	byte[] keyD = new byte[8];
	secureRandom2.nextBytes(keyD);

	byte[] fullKey = new byte[24];
	for (int i = 0; i < 8; i++) {
	    fullKey[i] = keyE[i];
	    fullKey[i + 16] = keyE[i];
	}
	for (int i = 8; i < 16; i++) {
	    fullKey[i] = keyD[i - 8];
	}

	Key K = new SecretKeySpec(fullKey, Constants.DES3);

	BASE64Encoder encoder = new BASE64Encoder();
	return encoder.encode(K.getEncoded());
    }

}
