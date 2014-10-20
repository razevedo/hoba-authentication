/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hobba.hobaserver.services.service;

import com.hobba.hobaserver.entitymanager.EntityManagerListener;
import com.hobba.hobaserver.services.HobaDevices;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 *
 * @author Fabio Gonçalves
 */
@Stateless
@Path("com.hobba.hobaserver.services.hobadevices")
public class HobaDevicesFacadeREST extends AbstractFacade<HobaDevices> {
    @PersistenceContext(unitName = "com.hobba_HOBAServer_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public HobaDevicesFacadeREST() {
        super(HobaDevices.class);
    }

    @POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public HobaDevices create(HobaDevices entity) {
        return super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, HobaDevices entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({"application/xml", "application/json"})
    public HobaDevices find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<HobaDevices> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<HobaDevices> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return EntityManagerListener.createEntityManager();
    }
    
}
