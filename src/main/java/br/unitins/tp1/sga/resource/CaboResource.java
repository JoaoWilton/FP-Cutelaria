package br.unitins.tp1.sga.resource;

import java.util.List;

import br.unitins.tp1.sga.dto.CriarCaboDTO;
import br.unitins.tp1.sga.model.Cabo;
import br.unitins.tp1.sga.model.Material;
import br.unitins.tp1.sga.repository.CaboRepository;
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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * CRUD de Cabos — componente da faca.
 *
 * Desde a Fase A (remoção de herança JOINED), Cabo é uma entidade
 * independente (extends PanacheEntity) com sua própria tabela e seu
 * próprio repository (CaboRepository) — sem instanceof/cast contra
 * Componente. Mesma estrutura de LaminaResource, adaptada para
 * Cabo/CriarCaboDTO.
 */
@Path("/administrativo/v1/cabos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CaboResource {

    @Inject
    CaboRepository repository;

    @Inject
    MaterialRepository materialRepository;

    @GET
    public List<Cabo> listarTodos() {
        return repository.listAll();
    }

    @HEAD
    public Response listarTodosHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Cabo buscarPorId(@PathParam("id") Long id) {
        Cabo cabo = repository.findById(id);
        if (cabo == null) {
            throw new NotFoundException("Cabo não encontrado com ID: " + id);
        }
        return cabo;
    }

    @POST
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response inserir(@Valid CriarCaboDTO dto) {
        Cabo cabo = new Cabo();
        preencher(cabo, dto);

        repository.persist(cabo);
        return Response.status(Response.Status.CREATED).entity(cabo).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid CriarCaboDTO dto) {
        Cabo cabo = repository.findById(id);
        if (cabo == null) {
            throw new NotFoundException("Cabo não encontrado com ID: " + id);
        }
        preencher(cabo, dto);

        repository.persist(cabo);
        return Response.ok(cabo).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response deletar(@PathParam("id") Long id) {
        Cabo cabo = repository.findById(id);
        if (cabo == null) {
            throw new NotFoundException("Cabo não encontrado com ID: " + id);
        }
        repository.delete(cabo);
        return Response.noContent().build();
    }

    private void preencher(Cabo cabo, CriarCaboDTO dto) {
        cabo.setDescricao(dto.descricao);
        cabo.setPesoGramas(dto.pesoGramas);
        cabo.setFormatoErgonomico(dto.formatoErgonomico);
        cabo.setPinoOculto(dto.pinoOculto);

        if (dto.materialId != null) {
            Material material = materialRepository.findById(dto.materialId);
            if (material == null) {
                throw new NotFoundException("Material não encontrado com ID: " + dto.materialId);
            }
            cabo.setMaterial(material);
        } else {
            cabo.setMaterial(null);
        }
    }
}
