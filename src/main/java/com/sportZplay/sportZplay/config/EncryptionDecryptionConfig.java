package com.sportZplay.sportZplay.config;

import com.sportZplay.sportZplay.Utils.SZP_Constants;
import org.springframework.context.annotation.Configuration;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class EncryptionDecryptionConfig {


    private static final String KEY = "anfe85k8kd1nlkbfp23krl0trl54sp8d";

    /**
     * Method to generate Secret KEY
     *
     * @return
     * @throws Exception
     */
    public static SecretKey generateKey() throws Exception {
        byte[] decodedKey = Base64.getDecoder().decode(KEY);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, SZP_Constants.ENCODED_DECODED_ALGORITHM);
    }

    /**
     * Method to convert secret key to String for storage and transfer
     *
     * @param secretKey
     * @return
     */
    public static String keyToString(SecretKey secretKey){
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Method to convert String to secret Key
     *
     * @param secretKey
     * @return
     */
    public static SecretKey stringToKey(String secretKey){
        byte[] decodedKey =  Base64.getDecoder().decode(secretKey);
        return new SecretKeySpec(decodedKey,0,decodedKey.length,SZP_Constants.ENCODED_DECODED_ALGORITHM);
    }

    /**
     * Method to encrypt the data
     *
     * @param data
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static String encrypt(String data,SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(SZP_Constants.ENCODED_DECODED_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] encrptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrptedData);
    }

    /**
     * Method to decrypt the data
     *
     * @param encryptedData
     * @param secretKey
     * @return
     * @throws Exception
     */
    public static  String decrypt(String encryptedData, SecretKey secretKey) throws Exception{
        Cipher cipher = Cipher.getInstance(SZP_Constants.ENCODED_DECODED_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte[] decodedData = Base64.getDecoder().decode(encryptedData);
        byte[] decyptedData = cipher.doFinal(decodedData);
        return new String(decyptedData);
    }
}
