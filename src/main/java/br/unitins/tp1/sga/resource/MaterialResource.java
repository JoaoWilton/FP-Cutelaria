package br.unitins.tp1.sga.resource;

import java.util.List;

import br.unitins.tp1.sga.dto.MaterialDTO;
import br.unitins.tp1.sga.model.Material;
import br.unitins.tp1.sga.repository.MaterialRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/administrativo/v1/materiais")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaterialResource {

    @Inject
    MaterialRepository repository;

    @GET
    public List<Material> listarTodos(@QueryParam("nome") String nome, @QueryParam("tipo") String tipoMaterial) {
        if (nome != null && !nome.isEmpty()) {
            return repository.findByNome(nome);
        }
        if (tipoMaterial != null && !tipoMaterial.isEmpty()) {
            return repository.findByTipoMaterial(tipoMaterial);
        }
        return repository.listAll();
    }
    @HEAD
    public Response listarTodosHead() {
        return Response.ok().build();
    }


    @GET
    @Path("/{id}")
    public Material buscarPorId(@PathParam("id") Long id) {
        Material entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Material não encontrado com ID: " + id);
        }
        return entity;
    }

    @POST
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response inserir(@Valid MaterialDTO dto) {
        Material material = new Material();
        material.setNome(dto.nome);
        material.setTipoMaterial(dto.tipoMaterial);
        material.setCustoUnitario(dto.custoUnitario);

        repository.persist(material);
        return Response.status(Response.Status.CREATED).entity(material).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid MaterialDTO dto) {
        Material entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Material não encontrado com ID: " + id);
        }

        entity.setNome(dto.nome);
        entity.setTipoMaterial(dto.tipoMaterial);
        entity.setCustoUnitario(dto.custoUnitario);

        repository.persist(entity);
        return Response.ok(entity).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response deletar(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Material não encontrado com ID: " + id);
        }
        return Response.noContent().build();
    }
}