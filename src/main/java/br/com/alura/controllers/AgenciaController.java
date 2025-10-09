package br.com.alura.controllers;

import br.com.alura.domain.Agencia;
import br.com.alura.service.AgenciaService;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
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
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Void>> cadastrar(Agencia agencia, @Context UriInfo uriInfo){
        return this.agenciaService.cadastrar(agencia).replaceWith(RestResponse.created(uriInfo.getAbsolutePath()));
    }

    @GET
    @Path("{id}")
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Agencia>> buscarPorId(Long id){
        return this.agenciaService.buscarPorId(id).onItem().transform(RestResponse::ok);
    }

    @DELETE
    @Path("{id}")
    @NonBlocking
    @Transactional
    public Uni<RestResponse<String>> deletar(Long id){
        return this.agenciaService.deletar(id).replaceWith(RestResponse.ok("Agência com ID " + id + " deletada com sucesso!"));
    }

    @PUT
    @NonBlocking
    @Transactional
    public Uni<RestResponse<Agencia>> alterar(Agencia agencia){
        return agenciaService.alterar(agencia).onItem().transform(RestResponse::ok);
    }
}
