package br.unitins.tp1.sga.resource;

import java.util.List;

import br.unitins.tp1.sga.dto.CriarGuarnicaoDTO;
import br.unitins.tp1.sga.model.Guarnicao;
import br.unitins.tp1.sga.model.Material;
import br.unitins.tp1.sga.repository.GuarnicaoRepository;
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
 * CRUD de Guarnições — componente da faca.
 *
 * Desde a Fase A (remoção de herança JOINED), Guarnicao é uma entidade
 * independente (extends PanacheEntity) com sua própria tabela e seu
 * próprio repository (GuarnicaoRepository) — sem instanceof/cast contra
 * Componente. Mesma estrutura de LaminaResource, adaptada para
 * Guarnicao/CriarGuarnicaoDTO.
 */
@Path("/administrativo/v1/guarnicoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GuarnicaoResource {

    @Inject
    GuarnicaoRepository repository;

    @Inject
    MaterialRepository materialRepository;

    @GET
    public List<Guarnicao> listarTodas() {
        return repository.listAll();
    }

    @HEAD
    public Response listarTodasHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Guarnicao buscarPorId(@PathParam("id") Long id) {
        Guarnicao guarnicao = repository.findById(id);
        if (guarnicao == null) {
            throw new NotFoundException("Guarnição não encontrada com ID: " + id);
        }
        return guarnicao;
    }

    @POST
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response inserir(@Valid CriarGuarnicaoDTO dto) {
        Guarnicao guarnicao = new Guarnicao();
        preencher(guarnicao, dto);

        repository.persist(guarnicao);
        return Response.status(Response.Status.CREATED).entity(guarnicao).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid CriarGuarnicaoDTO dto) {
        Guarnicao guarnicao = repository.findById(id);
        if (guarnicao == null) {
            throw new NotFoundException("Guarnição não encontrada com ID: " + id);
        }
        preencher(guarnicao, dto);

        repository.persist(guarnicao);
        return Response.ok(guarnicao).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response deletar(@PathParam("id") Long id) {
        Guarnicao guarnicao = repository.findById(id);
        if (guarnicao == null) {
            throw new NotFoundException("Guarnição não encontrada com ID: " + id);
        }
        repository.delete(guarnicao);
        return Response.noContent().build();
    }

    private void preencher(Guarnicao guarnicao, CriarGuarnicaoDTO dto) {
        guarnicao.setDescricao(dto.descricao);
        guarnicao.setPesoGramas(dto.pesoGramas);
        guarnicao.setTipoPeca(dto.tipoPeca);
        guarnicao.setPolimentoRealizado(dto.polimentoRealizado);

        if (dto.materialId != null) {
            Material material = materialRepository.findById(dto.materialId);
            if (material == null) {
                throw new NotFoundException("Material não encontrado com ID: " + dto.materialId);
            }
            guarnicao.setMaterial(material);
        } else {
            guarnicao.setMaterial(null);
        }
    }
}
