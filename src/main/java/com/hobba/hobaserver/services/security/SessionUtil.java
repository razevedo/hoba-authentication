/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.security;

import com.hobba.hobaserver.resources.HobaDevices;
import com.hobba.hobaserver.resources.HobaSession;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaSessionFacadeREST;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Fabio Gonçalves
 */
public class SessionUtil {

    public boolean setSession(String kid) {
        try {
            HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
            HobaDevices hd = hkfrest.findHKIDbyKID(kid).getIdDevices();
            Collection<HobaSession> hss = hd.getHobaSessionCollection();
            HobaSession hobaSession;
            if (hss.isEmpty()) {
                hobaSession = new HobaSession();

                hobaSession.setIdDevices(hd);
            } else {
                hobaSession = hd.getHobaSessionCollection().iterator().next();
            }
            hobaSession.setIsvalid(Boolean.TRUE);
            
            long dateL = new Date().getTime();
            Date initDate = new Date(dateL+(60000*30));
            
            hobaSession.setSessionIni(initDate);
            HobaSessionFacadeREST hsfrest = new HobaSessionFacadeREST();
            hsfrest.create(hobaSession);

            return true;

        } catch (Exception e) {
            return false;
        }

    }

    public boolean isSessionValid(String kid) {

        try {
            HobaSessionFacadeREST hsfrest = new HobaSessionFacadeREST();

            HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
            HobaSession hobaSession;

            hobaSession = hkfrest.findHKIDbyKID(kid).getIdDevices().getHobaSessionCollection().iterator().next();

            if (hobaSession.getIsvalid() && hobaSession.getSessionIni().after(new Date())) {
                return true;
            }
        } catch (Exception e) {

        }

        return false;
    }
    
    public boolean invalidateSession(String kid){
        System.out.println("kid sess: "+kid);
        try {
            HobaSessionFacadeREST hsfrest = new HobaSessionFacadeREST();

            HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
            HobaSession hobaSession;

            hobaSession = hkfrest.findHKIDbyKID(kid).getIdDevices().getHobaSessionCollection().iterator().next();

            hobaSession.setIsvalid(Boolean.FALSE);
            hsfrest.create(hobaSession);
            
           return true;
                   
        } catch (Exception e) {

        }

        return false;
    }

}
