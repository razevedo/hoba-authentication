package com.hobba.hobaserver.application;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author Fabio Gonçalves
 */
@javax.ws.rs.ApplicationPath("hoba")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
         try {
            Class jacksonProvider = Class.forName("org.codehaus.jackson.jaxrs.JacksonJsonProvider");
            resources.add(jacksonProvider);
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        addRestResourceClasses(resources);
        
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.hobba.hobaserver.services.service.HobaChallengesFacadeREST.class);
        resources.add(com.hobba.hobaserver.services.service.HobaDevicesFacadeREST.class);
        resources.add(com.hobba.hobaserver.services.service.HobaKeysFacadeREST.class);
        resources.add(com.hobba.hobaserver.services.service.HobaResource.class);
        resources.add(com.hobba.hobaserver.services.service.HobaTokenFacadeREST.class);
        resources.add(com.hobba.hobaserver.services.service.HobaUserFacadeREST.class);
    }
    
}
