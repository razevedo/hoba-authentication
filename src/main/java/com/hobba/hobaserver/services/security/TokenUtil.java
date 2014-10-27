/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.security;

import com.hobba.hobaserver.resources.HobaDevices;
import com.hobba.hobaserver.resources.HobaKeys;
import com.hobba.hobaserver.resources.HobaToken;
import com.hobba.hobaserver.resources.HobaUser;
import com.hobba.hobaserver.services.service.HobaDevicesFacadeREST;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaTokenFacadeREST;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Fabio Gonçalves
 */
public class TokenUtil {

    public String getToken(String kid, String expiration_time) {
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);
        HobaDevices hd = hk.getIdDevices();
        HobaUser hu = hd.getIduser();

        SecureRandom random = new SecureRandom();
        String rand = new BigInteger(256, random).toString(32);
        HobaToken ht = new HobaToken();
        ht.setToken(rand);
        long time = 0;
        try {
            time = Long.parseLong(expiration_time);
            if (time > 0) {
                Date date = new Date(new Date().getTime() + (time * 1000));
                ht.setExpiration(date);
            } else {
                ht.setExpiration(null);
            }
        } catch (Exception e) {
            ht.setExpiration(null);
        }
        ht.setIsValid(Boolean.TRUE);
        ht.setIdUser(hu);

        HobaTokenFacadeREST htfrest = new HobaTokenFacadeREST();
        ht = htfrest.create(ht);
        String token = kid + ":" + rand;
        byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
        token = new String(encodedBytes);
        return token;
    }

    public boolean authenticateToken(String token, String kid) {
        byte[] decodedToken = Base64.decodeBase64(token.getBytes());
        String decodedTokenString = new String(decodedToken);
        String[] fields = decodedTokenString.split(":");

        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(fields[0]);
        int userID = hk.getIdDevices().getIduser().getIdUser();
        HobaUser hu = hk.getIdDevices().getIduser();

        HobaTokenFacadeREST htfrest = new HobaTokenFacadeREST();
        HobaToken ht = htfrest.findTokenbyToken(fields[1]);
        Date date = new Date();
        
        if (ht.getExpiration() != null) {
            
            if (date.after(ht.getExpiration())) {
                return false;
            }
        }else{
            if (!ht.getIsValid()) {
                return false;
            }
            ht.setIsValid(Boolean.FALSE);
        }

        HobaUser hu1 = ht.getIdUser();
        if (hu.getIdUser() != userID) {
            return false;
        }
        hk = hkfrest.findHKIDbyKID(kid);
        HobaDevices hd = hk.getIdDevices();
        hd.setIduser(hu);
        HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
        htfrest.create(ht);
        hdfrest.create(hd);
        return true;

    }
}
