/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.util;

import com.hobba.hobaserver.services.HobaResource;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import sun.misc.BASE64Decoder;

/**
 *
 * @author Fabio Gonçalves
 */
public class Util {

    public static String getHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result
                    += Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static boolean verifySign(byte[] publickey, byte[] sign, byte[] data) {

        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            X509EncodedKeySpec spec = new X509EncodedKeySpec(publickey);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            sig.initVerify(kf.generatePublic(spec));
            sig.update(data);
            return sig.verify(sign);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }

        return false;
    }
    
    public static String getPublicKeyFromPEM(String PEM){
        try {
            String pubKey = PEM;
            
            pubKey = pubKey.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
            
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] keyBytes = decoder.decodeBuffer(pubKey);
            
            return Util.getHexString(keyBytes);
        } catch (IOException ex) {
            Logger.getLogger(HobaResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
