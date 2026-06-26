package br.unitins.tp1.sga.resource;

import java.util.List;

import br.unitins.tp1.sga.dto.CriarLaminaDTO;
import br.unitins.tp1.sga.model.Lamina;
import br.unitins.tp1.sga.model.Material;
import br.unitins.tp1.sga.repository.LaminaRepository;
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
 * CRUD de Lâminas — componente da faca.
 *
 * Desde a Fase A (remoção de herança JOINED), Lamina é uma entidade
 * independente (extends PanacheEntity) com sua própria tabela e seu
 * próprio repository (LaminaRepository) — sem instanceof/cast contra
 * Componente.
 *
 * GETs públicos (o cuteleiro precisa consultar lâminas existentes ao montar
 * uma Faca, e o catálogo de componentes não é sensível). Escrita restrita a
 * Cuteleiro + Administrador — mesma regra de Material (catálogo
 * compartilhado, ver Fase 4 do plano).
 */
@Path("/administrativo/v1/laminas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LaminaResource {

    @Inject
    LaminaRepository repository;

    @Inject
    MaterialRepository materialRepository;

    @GET
    public List<Lamina> listarTodas() {
        return repository.listAll();
    }

    @HEAD
    public Response listarTodasHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public Lamina buscarPorId(@PathParam("id") Long id) {
        Lamina lamina = repository.findById(id);
        if (lamina == null) {
            throw new NotFoundException("Lâmina não encontrada com ID: " + id);
        }
        return lamina;
    }

    @POST
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response inserir(@Valid CriarLaminaDTO dto) {
        Lamina lamina = new Lamina();
        preencher(lamina, dto);

        repository.persist(lamina);
        return Response.status(Response.Status.CREATED).entity(lamina).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid CriarLaminaDTO dto) {
        Lamina lamina = repository.findById(id);
        if (lamina == null) {
            throw new NotFoundException("Lâmina não encontrada com ID: " + id);
        }
        preencher(lamina, dto);

        repository.persist(lamina);
        return Response.ok(lamina).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response deletar(@PathParam("id") Long id) {
        Lamina lamina = repository.findById(id);
        if (lamina == null) {
            throw new NotFoundException("Lâmina não encontrada com ID: " + id);
        }
        repository.delete(lamina);
        return Response.noContent().build();
    }

    private void preencher(Lamina lamina, CriarLaminaDTO dto) {
        lamina.setDescricao(dto.descricao);
        lamina.setPesoGramas(dto.pesoGramas);
        lamina.setPerfilDesbaste(dto.perfilDesbaste);
        lamina.setEspessuraMm(dto.espessuraMm);

        if (dto.materialId != null) {
            Material material = materialRepository.findById(dto.materialId);
            if (material == null) {
                throw new NotFoundException("Material não encontrado com ID: " + dto.materialId);
            }
            lamina.setMaterial(material);
        } else {
            lamina.setMaterial(null);
        }
    }
}
