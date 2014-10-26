/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services;

import com.hobba.hobaserver.entitymanager.EntityManagerListener;
import com.hobba.hobaserver.services.security.ChallengeUtil;
import com.hobba.hobaserver.services.security.RegisterUtil;
import com.hobba.hobaserver.services.security.TokenUtil;
import com.hobba.hobaserver.services.service.HobaChallengesFacadeREST;
import com.hobba.hobaserver.services.service.HobaDevicesFacadeREST;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaTokenFacadeREST;
import com.hobba.hobaserver.services.service.HobaUserFacadeREST;
import com.hobba.hobaserver.services.util.Util;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;

/**
 * REST Web Service
 *
 * @author Fabio GonÃ§alves
 */
@Path("/")
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
    public String getJSON() {
        //TODO return proper representation object
        return "Hello World";
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
        ChallengeUtil challengeUtil = new ChallengeUtil();
        long expirationTime = 10;
        String challenge = challengeUtil.getChallenge(kid, 10);
        return Response.status(Response.Status.OK).header("Authentication", "HOBA").header("challenge", challenge).header("max-age", 10).build();

    }

    @Path("auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response athenticateUA(@Context HttpServletRequest request) {
        ChallengeUtil challengeUtil = new ChallengeUtil();
        em.getEntityManagerFactory().getCache().evictAll();
        if (challengeUtil.isChallengeValid(request)) {
            return Response.status(Response.Status.OK).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @Path("token")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@FormParam("kid") String kid, @FormParam("expiration_time") String expiration_time) {
        TokenUtil tokenUtil = new TokenUtil();

        String token = tokenUtil.getToken(kid, expiration_time);
        return Response.status(Response.Status.OK).entity(token).build();
    }

    @Path("token_auth")
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

    @POST
    @Path("uas")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUAS(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();

        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);
        HobaUser hu = hk.getIdDevices().getIduser();

        List<HobaDevices> hds = new ArrayList<>(hu.getHobaDevicesCollection());
        ResponseObject responseObject = new ResponseObject();
        responseObject.setList(hds);
        StringBuffer buffer = new StringBuffer();

        for (HobaDevices hd : hds) {
            buffer.append(hd.getDidtype()).append("?");
            buffer.append(hd.getIpAddress()).append("?");
            Collection<HobaKeys> hkeys = hd.getHobaKeysCollection();
            for (HobaKeys hkaux : hkeys) {
                buffer.append(hkaux.getKid()).append(":");
            }
            buffer.append("?");
            buffer.append(hd.getLastDate());
            buffer.append("*");
        }

        return Response.status(Response.Status.OK).entity(buffer.toString()).build();
    }

    @Path("user_data")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUserData(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();

        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);

        HobaUser hu = hk.getIdDevices().getIduser();

        if (hu == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String userData = "";

        if (hu.getField1() != null) {
            userData = userData + hu.getField1() + "*";
        }
        if (hu.getField2() != null) {
            userData = userData + hu.getField2() + "*";
        }
        if (hu.getField3() != null) {
            userData = userData + hu.getField3();
        }

        return Response.status(Response.Status.OK).entity(userData).build();
    }

    @Path("user_set")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response serUserData(
            @FormParam("kid") String kid,
            @FormParam("field1") String field1,
            @FormParam("field2") String field2,
            @FormParam("field3") String field3) {
        em.getEntityManagerFactory().getCache().evictAll();

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
