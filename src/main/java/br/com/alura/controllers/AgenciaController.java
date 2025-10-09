package br.com.alura.controllers;

import br.com.alura.domain.Agencia;
import br.com.alura.service.AgenciaService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/agencias")
public class AgenciaController {

    @Inject
    private AgenciaService agenciaService;

    // RestResponse -> Tipificado || Response não
    @POST
    @Transactional
    public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo){
        this.agenciaService.cadastrar(agencia);
        return RestResponse.created(uriInfo.getAbsolutePath());
    }

    @GET
    @Path("{id}")
    @Transactional
    public RestResponse<Agencia> buscarPorId(Long id){
        Agencia agencia = this.agenciaService.buscarPorId(id);
        return RestResponse.ok(agencia);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public RestResponse<String> deletar(Long id){
        this.agenciaService.deletar(id);
        return RestResponse.ok("Agência com ID " + id + " deletada com sucesso!");
    }

    @PUT
    @Transactional
    public RestResponse<Agencia> alterar(Agencia agencia){
        return RestResponse.ok(agenciaService.alterar(agencia));
    }
}
