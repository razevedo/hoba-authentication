/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.service;

import com.hobba.hobaserver.entitymanager.EntityManagerListener;
import com.hobba.hobaserver.resources.HobaConnections;
import com.hobba.hobaserver.resources.HobaDevices;
import com.hobba.hobaserver.resources.HobaEndpoints;
import com.hobba.hobaserver.resources.HobaKeys;
import com.hobba.hobaserver.resources.HobaUser;
import com.hobba.hobaserver.resources.ResponseObject;
import com.hobba.hobaserver.services.security.ChallengeUtil;
import com.hobba.hobaserver.services.security.RegisterUtil;
import com.hobba.hobaserver.services.security.SessionUtil;
import com.hobba.hobaserver.services.security.TokenUtil;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaUserFacadeREST;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author Fabio GonÃƒÂ§alves
 */
@Path("/hoba/")
public class HobaResource {

    @Context
    private UriInfo context;

    private EntityManager em = getEntityManager();

    /**
     * Creates a new instance of HobaResource
     */
    public HobaResource() {
    }

    /**
     * Retrieves representation of an instance of
     * com.hobba.hobaserver.services.HobaResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List getJSON() {
        //TODO return proper representation object
        
        List<HobaEndpoints> hobaEndpointses = new ArrayList<HobaEndpoints>();
        HobaEndpoints endpoints;
        
        endpoints = new HobaEndpoints("register", ".well-known/hoba/register", "POST", "FormParametes","");
        endpoints.addParameter("pub");
        endpoints.addParameter("kidtype");
        endpoints.addParameter("kid");
        endpoints.addParameter("didtype");
        endpoints.addParameter("did");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("getchal", ".well-known/hoba/getchal", "POST", "FormParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("authentication", ".well-known/hoba/auth", "POST", "HeaderParameters","Authorized");
        endpoints.addParameter("HOBA-RES");
        hobaEndpointses.add(endpoints);
        
        
        endpoints = new HobaEndpoints("logout", ".well-known/hoba/logout", "POST", "FormParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("deleteKey", ".well-known/hoba/key", "DELETE", "FormParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("getAuthenticationToken", ".well-known/hoba/token", "GET", "QueryParametes","");
        endpoints.addParameter("kid");
        endpoints.addParameter("expiration_time");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("authenticateToken", ".well-known/hoba/token", "POST", "FormParametes","");
        endpoints.addParameter("kid");
        endpoints.addParameter("token");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("getUAs", ".well-known/hoba/uas", "GET", "QueryParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("getUserData", ".well-known/hoba/user", "GET", "QueryParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("setUserData", ".well-known/hoba/user", "POST", "FormParametes","");
        endpoints.addParameter("kid");
        endpoints.addParameter("field1");
        endpoints.addParameter("field2");
        endpoints.addParameter("field3");
        hobaEndpointses.add(endpoints);
        
        endpoints = new HobaEndpoints("deleteUser", ".well-known/hoba/user", "DELETE", "FormParametes","");
        endpoints.addParameter("kid");
        hobaEndpointses.add(endpoints);
        
        
        return hobaEndpointses;
    }

    @Path("register")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response register(
            @FormParam("pub") String pub,
            @FormParam("kidtype") String kidType,
            @FormParam("kid") String kid,
            @FormParam("didtype") String didType,
            @FormParam("did") String did,
            @Context HttpServletRequest request
    ) {
        RegisterUtil registerUtil = new RegisterUtil();
        if (registerUtil.userRegister(did, didType, kid, kidType, pub, request)) {
            return Response.status(Response.Status.OK).header("Hobareg-val", "regok").entity("register").build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity("USER_EXISTS").build();

    }

    @Path("getchal")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getChalenge(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        ChallengeUtil challengeUtil = new ChallengeUtil();
        long expirationTime = 10;
        String challenge = challengeUtil.getChallenge(kid, 10);
        return Response.status(Response.Status.OK).header("Authentication", "HOBA").header("challenge", challenge).header("max-age", 10).build();

    }
    
    @Path("logout")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response logout(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        SessionUtil sessionUtil = new SessionUtil();
        
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        
        if(!sessionUtil.invalidateSession(kid)){
            return Response.status(Response.Status.BAD_REQUEST).build();
        }else{
            return Response.status(Response.Status.OK).build();
        }

    }
    
    @Path("key")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteKey(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaDevices hd = hkfrest.findHKIDbyKID(kid).getIdDevices();
        
        HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
        hdfrest.remove(hd);
        
        return Response.status(Response.Status.OK).build();

    }
    
    @Path("user")
    @DELETE
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteAll(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaUser hu = hkfrest.findHKIDbyKID(kid).getIdDevices().getIduser();
        
        HobaUserFacadeREST hufrest = new HobaUserFacadeREST();
        hufrest.remove(hu);
        
        return Response.status(Response.Status.OK).build();

    }

    @Path("auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response athenticateUA(@Context HttpServletRequest request) {
        ChallengeUtil challengeUtil = new ChallengeUtil();
        em.getEntityManagerFactory().getCache().evictAll();
        if (challengeUtil.isChallengeValid(request)) {
            
            SessionUtil sessionUtil = new SessionUtil();
            
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @Path("token")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@QueryParam("kid") String kid, @QueryParam("expiration_time") String expiration_time) {
        
        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        
        em.getEntityManagerFactory().getCache().evictAll();
        TokenUtil tokenUtil = new TokenUtil();

        String token = tokenUtil.getToken(kid, expiration_time);
        return Response.status(Response.Status.OK).entity(token).build();
    }

    @Path("token")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response authToken(@FormParam("token") String token, @FormParam("kid") String kid) {
        
        

        em.getEntityManagerFactory().getCache().evictAll();

        TokenUtil tokenUtil = new TokenUtil();
        if (tokenUtil.authenticateToken(token, kid)) {
            return Response.status(Response.Status.OK).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @GET
    @Path("uas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUAS(@QueryParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        
        
        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);
        HobaUser hu = hk.getIdDevices().getIduser();

        Collection<HobaDevices> hds = hu.getHobaDevicesCollection();
        List<HobaConnections> hcs = new ArrayList<HobaConnections>();
        HobaConnections hc;
        for(HobaDevices hd: hds){
            List<HobaKeys> aux = new ArrayList<HobaKeys>(hd.getHobaKeysCollection());
            hc = new HobaConnections(hd.getDidtype(), hd.getIpAddress(), hd.getLastDate(), aux.get(aux.size()-1).getKid());
            hcs.add(hc);
        }

        return Response.status(Response.Status.OK).entity(hcs).build();
    }

    @Path("user")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserData(@QueryParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();

        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }
        
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);

        HobaUser hu = hk.getIdDevices().getIduser();

        if (hu == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.status(Response.Status.OK).entity(hu).build();
    }

    @Path("user")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response setUserData(
            @FormParam("kid") String kid,
            @FormParam("field1") String field1,
            @FormParam("field2") String field2,
            @FormParam("field3") String field3) {
        em.getEntityManagerFactory().getCache().evictAll();
        
        SessionUtil sessionUtil = new SessionUtil();
        if(!sessionUtil.isSessionValid(kid)){
            return Response.status(Response.Status.UNAUTHORIZED).header("Authenticate", "HOBA").build();
        }

        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);

        HobaUser hu = hk.getIdDevices().getIduser();

        if (hu == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        hu.setField1(field1);
        hu.setField2(field2);
        hu.setField3(field3);

        HobaUserFacadeREST hufrest = new HobaUserFacadeREST();
        hufrest.create(hu);

        return Response.status(Response.Status.OK).build();
    }

    /**
     * PUT method for updating or creating an instance of HobaResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }

    protected EntityManager getEntityManager() {
        return EntityManagerListener.createEntityManager();
    }
}
