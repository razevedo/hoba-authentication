/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.security;

import com.hobba.hobaserver.resources.HobaChallenges;
import com.hobba.hobaserver.resources.HobaDevices;
import com.hobba.hobaserver.resources.HobaKeys;
import com.hobba.hobaserver.services.service.HobaChallengesFacadeREST;
import com.hobba.hobaserver.services.service.HobaDevicesFacadeREST;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.util.Util;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Fabio Gonçalves
 */
public class ChallengeUtil {

    public String getChallenge(String kid, long expirationTime) {
        HobaKeys hk = new HobaKeys();
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaChallengesFacadeREST hcfrest = new HobaChallengesFacadeREST();
        hk = hkfrest.findHKIDbyKID(kid);

        SecureRandom random = new SecureRandom();
        String rand = new BigInteger(130, random).toString(32);

        HobaChallenges hc = new HobaChallenges();

        hc.setIdKeys(hk);
        hc.setChalenge(rand);
        if (expirationTime > 0) {
            Date date = new Date(new Date().getTime() + (expirationTime * 100));
            hc.setExpiration(date);
        }
        hc.setExpiration(null);
        hc.setIsValid(Boolean.TRUE);
        hcfrest.create(hc);

        return new String(Base64.encodeBase64(rand.getBytes()));
    }

    public boolean isChallengeValid(HttpServletRequest request) {

        HobaKeys hk = new HobaKeys();
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        
        hk = hkfrest.findHKIDbyKID(getKID(request));
        
        HobaChallengesFacadeREST hcfrest = new HobaChallengesFacadeREST();
        
        String challenge = getChallenge(request);
        if(challenge == null){
            return false;
        }
        
        HobaChallenges hc = hcfrest.findChallengebyChallenge(challenge);
        
        if(!hc.getIsValid()){
            return false;
        }
        if(hc.getExpiration() != null){
           if(hc.getExpiration().after(new Date())){
               return false;
           } 
        }
        
        try {

            HobaDevices hd = hk.getIdDevices();

            if (verifySignature(request, hk)) {
                hd.setIpAddress(request.getRemoteAddr());
                hd.setLastDate(new Date());
                HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
                hdfrest.create(hd);
                
                if(hc.getExpiration() == null){
                    hc.setIsValid(Boolean.FALSE);
                    hcfrest.edit(hc);
                }
                
                SessionUtil sessionUtil = new SessionUtil();
                if(!sessionUtil.setSession(getKID(request))){
                    return false;
                }
                
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private String getChallenge(HttpServletRequest request){
        String header = request.getHeader("Authorized");
        String[] headerParams = header.split("[.]");
        String chalenge = headerParams[1];
        
        return new String(Base64.decodeBase64(chalenge));
    }

    private boolean verifySignature(HttpServletRequest request, HobaKeys hk) {
        byte[] publicKey = Util.hexStringToByteArray(hk.getPub());
        
        byte[] sign = Util.hexStringToByteArray(getSignature(request));
        String hobaTbs = getHobaTBS(request);
        byte[] chalengeBytes = hobaTbs.getBytes();

        boolean verify = Util.verifySign(publicKey, sign, chalengeBytes);
        return verify;
    }

    private String getHobaTBS(HttpServletRequest request) {
        String header = request.getHeader("Authorized");
        String[] headerParams = header.split("[.]");
        String kid = headerParams[0];
        kid = new String(Base64.encodeBase64(kid.getBytes()));
        String chalenge = headerParams[1];
        String nonce = headerParams[2];

        String alg = "1";
        String origin = request.getRequestURL().toString();
        origin = origin.split("/")[0] + "//" + origin.split("/")[2] + "/";
        String hobaTbs = nonce + " " + alg + " " + origin + " " + kid + " " + chalenge;
        return hobaTbs;
    }

    private String getSignature(HttpServletRequest request) {
        String header = request.getHeader("Authorized");
        String[] headerParams = header.split("[.]");
        String signBase64 = headerParams[3];
        
        byte[] decodedToken = Base64.decodeBase64(signBase64.getBytes());
        String decodedSign = new String(decodedToken);
        return decodedSign;
    }

    private String getKID(HttpServletRequest request) {
        String header = request.getHeader("Authorized");
        String[] headerParams = header.split("[.]");
        String kid = headerParams[0];
        return kid;
        
        
    }

}
