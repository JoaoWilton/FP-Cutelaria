package br.unitins.tp1.sga.resource;

import br.unitins.tp1.sga.dto.CriarOpcaoPersonalizacaoDTO;
import br.unitins.tp1.sga.dto.OpcaoPersonalizacaoDTO;
import br.unitins.tp1.sga.model.Cuteleiro;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.repository.CuteleiroRepository;
import br.unitins.tp1.sga.repository.ModeloFacaRepository;
import br.unitins.tp1.sga.repository.OpcaoPersonalizacaoRepository;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;

@Path("/administrativo/v1/facas/{modeloId}/opcoes-personalizacao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OpcaoPersonalizacaoResource {

    @Inject OpcaoPersonalizacaoRepository opcaoRepository;
    @Inject ModeloFacaRepository modeloFacaRepository;
    @Inject CuteleiroRepository cuteleiroRepository;
    @Inject JsonWebToken jwt;

    @GET
    @PermitAll
    public List<OpcaoPersonalizacaoDTO> listar(@PathParam("modeloId") Long modeloId) {
        ModeloFaca modelo = buscarModelo(modeloId);
        return opcaoRepository.findAtivasByModeloFacaId(modelo.id)
                .stream().map(OpcaoPersonalizacaoDTO::fromEntity).toList();
    }

    @POST
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response criar(@PathParam("modeloId") Long modeloId,
                          @Valid CriarOpcaoPersonalizacaoDTO dto) {
        ModeloFaca modelo = buscarEValidarPosse(modeloId);

        OpcaoPersonalizacao opcao = new OpcaoPersonalizacao();
        opcao.setTipo(dto.tipo);
        opcao.setOpcao(dto.opcao);
        opcao.setCustoAdicional(dto.custoAdicional);
        opcao.setAtivo(true);
        opcao.setModeloFaca(modelo);

        opcaoRepository.persist(opcao);
        return Response.status(Response.Status.CREATED)
                .entity(OpcaoPersonalizacaoDTO.fromEntity(opcao)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response atualizar(@PathParam("modeloId") Long modeloId,
                              @PathParam("id") Long id,
                              @Valid CriarOpcaoPersonalizacaoDTO dto) {
        buscarEValidarPosse(modeloId);

        OpcaoPersonalizacao opcao = opcaoRepository.findById(id);
        if (opcao == null || !opcao.getModeloFaca().id.equals(modeloId)) {
            throw new NotFoundException("Opção não encontrada com ID: " + id);
        }

        opcao.setTipo(dto.tipo);
        opcao.setOpcao(dto.opcao);
        opcao.setCustoAdicional(dto.custoAdicional);

        opcaoRepository.persist(opcao);
        return Response.ok(OpcaoPersonalizacaoDTO.fromEntity(opcao)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response deletar(@PathParam("modeloId") Long modeloId,
                            @PathParam("id") Long id) {
        buscarEValidarPosse(modeloId);

        OpcaoPersonalizacao opcao = opcaoRepository.findById(id);
        if (opcao == null || !opcao.getModeloFaca().id.equals(modeloId)) {
            throw new NotFoundException("Opção não encontrada com ID: " + id);
        }

        opcaoRepository.delete(opcao);
        return Response.noContent().build();
    }

    // -------------------------------------------------------------------------

    private boolean isAdmin() {
        return jwt.getGroups() != null && jwt.getGroups().contains("Administrador");
    }

    private ModeloFaca buscarModelo(Long modeloId) {
        ModeloFaca modelo = modeloFacaRepository.findById(modeloId);
        if (modelo == null) {
            throw new NotFoundException("Faca não encontrada com ID: " + modeloId);
        }
        return modelo;
    }

    /**
     * Admin pode modificar qualquer modelo. Cuteleiro só o próprio.
     */
    private ModeloFaca buscarEValidarPosse(Long modeloId) {
        ModeloFaca modelo = buscarModelo(modeloId);
        if (!isAdmin()) {
            String login = jwt.getName();
            Cuteleiro cuteleiro = cuteleiroRepository.findByLogin(login);
            if (cuteleiro == null || modelo.getCriador() == null
                    || !modelo.getCriador().id.equals(cuteleiro.id)) {
                throw new ForbiddenException("Você não tem permissão para modificar opções desta faca");
            }
        }
        return modelo;
    }
}
