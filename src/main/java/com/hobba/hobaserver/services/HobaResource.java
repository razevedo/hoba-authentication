/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services;

import com.hobba.hobaserver.entitymanager.EntityManagerListener;
import com.hobba.hobaserver.services.service.HobaChalengesFacadeREST;
import com.hobba.hobaserver.services.service.HobaDevicesFacadeREST;
import com.hobba.hobaserver.services.service.HobaKeysFacadeREST;
import com.hobba.hobaserver.services.service.HobaTokenFacadeREST;
import com.hobba.hobaserver.services.service.HobaUserFacadeREST;
import com.hobba.hobaserver.services.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Decoder;

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
        HobaKeys hk = new HobaKeys();
        HobaDevices hd = new HobaDevices();
        HobaUser hu = new HobaUser();

        HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaUserFacadeREST hufrest = new HobaUserFacadeREST();

        try {
            System.out.println("hu: " + hu.toString() + " " + hu.getIdUser());
            hu = hufrest.create(hu);
        } catch (Exception e) {
            System.out.println("user exists1");
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).entity("USER_EXISTS").build();
        }

        try {
            hd.setDid(did);
            hd.setDidtype(didType);
            hd.setIduser(hu);
            hd.setLastDate(new Date());
            hd.setIpAddress(request.getRemoteAddr());
            hd = hdfrest.create(hd);
        } catch (Exception e) {
            System.out.println("user exists2");
            return Response.status(Response.Status.BAD_REQUEST).entity("USER_EXISTS").build();
        }

        try {
            hk.setIdDevices(hd);
            hk.setKid(kid);
            hk.setKidtype(kidType);

            hk.setPub(Util.getPublicKeyFromPEM(pub));

            hk = hkfrest.create(hk);
        } catch (Exception e) {
            System.out.println("user exists3");
            return Response.status(Response.Status.BAD_REQUEST).entity("USER_EXISTS").build();
        }
        
        
        return Response.status(Response.Status.OK).header("Hobareg-val", "regok").entity("register").build();
    }

    @Path("getchal")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getChalenge(@FormParam("kid") String kid) {
        HobaKeys hk = new HobaKeys();
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        hk = hkfrest.findHKIDbyKID(kid);

        SecureRandom random = new SecureRandom();
        String rand = new BigInteger(130, random).toString(32);
        HobaChalenges hc = new HobaChalenges();

        hc.setIdKeys(hk);
        hc.setChalenge(rand);
        hc.setExpiration(null);

        HobaChalengesFacadeREST hcfrest = new HobaChalengesFacadeREST();
        hcfrest.create(hc);
        return Response.status(Response.Status.OK).header("Authentication", "HOBA").header("challenge", rand).header("max-age", 0).build();

    }

    @Path("auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response athenticateUA(@Context HttpServletRequest request) {

        String header = request.getHeader("Authorized");
        System.out.println("header: " + header);
        String[] headerParams = header.split("[.]");
        System.out.println("params: " + headerParams.length);
        String kid = headerParams[0];
        String chalenge = headerParams[1];
        String nonce = headerParams[2];
        String signBase64 = headerParams[3];
        String alg = "1";
        String origin = request.getRequestURL().toString();
        origin = origin.split("/")[0] + "//" + origin.split("/")[2] + "/";

        byte[] decodedToken = Base64.decodeBase64(signBase64.getBytes());
        String decodedSign = new String(decodedToken);
        System.out.println("signature: " + decodedSign);
        String hobaTbs = nonce + " " + alg + " " + origin + " " + kid + " " + chalenge;

        HobaKeys hk = new HobaKeys();
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();

        hk = hkfrest.findHKIDbyKID(kid);
        HobaChalenges hc = new HobaChalenges();
        for (HobaChalenges hb_chalenge : hk.getHobaChalengesCollection()) {
            hc = hb_chalenge;
        }

        HobaChalengesFacadeREST hcfrest = new HobaChalengesFacadeREST();
        List list = hcfrest.findAll();
        hc = (HobaChalenges) list.get(list.size() - 1);
        for (int i = 0; i < list.size(); i++) {
            hc = (HobaChalenges) list.get(i);
        }
        try {

            byte[] publicKey = Util.hexStringToByteArray(hk.getPub());
            byte[] sign = Util.hexStringToByteArray(decodedSign);
            byte[] chalengeBytes = hobaTbs.getBytes();
            System.out.println(hobaTbs);
            System.out.println("len: "+hobaTbs.length());
            boolean verify = Util.verifySign(publicKey, sign, chalengeBytes);
            System.out.println("verify: "+verify);
            HobaDevices hd = hk.getIdDevices();
            if (verify) {
                hd.setIpAddress(request.getRemoteAddr());
                hd.setLastDate(new Date());
                HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
                hdfrest.create(hd);
                return Response.status(Response.Status.OK).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();

    }

    @Path("token")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getToken(@FormParam("kid") String kid) {
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);
        HobaDevices hd = hk.getIdDevices();
        HobaUser hu = hd.getIduser();

        SecureRandom random = new SecureRandom();
        String rand = new BigInteger(256, random).toString(32);
        System.out.println("rand: " + rand);
        HobaToken ht = new HobaToken();
        ht.setToken(rand);

        ht.setIdUser(hu);

        HobaTokenFacadeREST htfrest = new HobaTokenFacadeREST();
        ht = htfrest.create(ht);
        String token = kid + ":" + rand;
        byte[] encodedBytes = Base64.encodeBase64(token.getBytes());
        token = new String(encodedBytes);

        return Response.status(Response.Status.OK).entity(token).build();
    }

    @Path("token_auth")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response authToken(@FormParam("token") String token, @FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        byte[] decodedToken = Base64.decodeBase64(token.getBytes());
        String decodedTokenString = new String(decodedToken);
        System.out.println("decoded: " + decodedTokenString);
        String[] fields = decodedTokenString.split(":");
        System.out.println("fields: " + fields[0] + " " + fields[1]);

        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(fields[0]);
        int userID = hk.getIdDevices().getIduser().getIdUser();
        HobaUser hu = hk.getIdDevices().getIduser();

        HobaTokenFacadeREST htfrest = new HobaTokenFacadeREST();
        HobaToken ht = htfrest.findTokenbyToken(fields[1]);

        HobaUser hu1 = ht.getIdUser();
        System.out.println("kid: " + kid + " user: " + userID + " " + hu.getIdUser());
        if (hu.getIdUser() == userID) {
            hk = hkfrest.findHKIDbyKID(kid);
            System.out.println("hk: " + hk.getKid());
            HobaDevices hd = hk.getIdDevices();
            hd.setIduser(hu);
            HobaDevicesFacadeREST hdfrest = new HobaDevicesFacadeREST();
            hdfrest.create(hd);
            return Response.status(Response.Status.OK).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).build();

    }

    @Path("uas")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response getUAS(@FormParam("kid") String kid) {
        em.getEntityManagerFactory().getCache().evictAll();
        System.out.println("hellio");
        HobaKeysFacadeREST hkfrest = new HobaKeysFacadeREST();
        HobaKeys hk = hkfrest.findHKIDbyKID(kid);
        HobaUser hu = hk.getIdDevices().getIduser();
        Collection<HobaDevices> hds = hu.getHobaDevicesCollection();
        System.out.println("hu: " + hds.size());
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
