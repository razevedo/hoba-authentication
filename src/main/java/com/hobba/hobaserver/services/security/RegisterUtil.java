/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.security;

import com.hobba.hobaserver.services.HobaDevices;
import com.hobba.hobaserver.services.HobaKeys;
import com.hobba.hobaserver.services.HobaUser;
import com.hobba.hobaserver.services.service.HobaDevicesFacadeREST;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaUserFacadeREST;
import com.hobba.hobaserver.services.util.Util;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 *
 * @author Fabio Gonçalves
 */
public class RegisterUtil {
    public boolean userRegister(String did, String didType, String kid, String kidType, String pub, HttpServletRequest request){
        
        HobaUser hu = createUser();
        if(hu == null){
            return false;
        }
        
        HobaDevices hd = createDevice(did, didType, hu, request);
        if(hd == null){
            return false;
        }
        
        HobaKeys hk = createKeys(hd, kid, kidType, pub);
        if(hk == null){
            return false;
        }
        
        return true;
    }
    
    public HobaKeys createKeys(HobaDevices hd, String kid, String kidType, String pub){
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = new HobaKeys();
        try {
            hk.setIdDevices(hd);
            hk.setKid(kid);
            hk.setKidtype(kidType);
            hk.setPub(Util.getPublicKeyFromPEM(pub));

            hk = hkfrest.create(hk);
            return hk;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private HobaUser createUser(){
        HobaUserFacadeREST hufrest = new HobaUserFacadeREST();
        HobaUser hu = new HobaUser();
        try {
            hu = hufrest.create(hu);
            return hu;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private HobaDevices createDevice(String did, String didType,HobaUser hu, HttpServletRequest request){
        HobaDevices hd = new HobaDevices();
        HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
        try {
            hd.setDid(did);
            hd.setDidtype(didType);
            hd.setIduser(hu);
            hd.setLastDate(new Date());
            hd.setIpAddress(request.getRemoteAddr());
            hd = hdfrest.create(hd);
            return hd;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}
