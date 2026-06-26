package br.unitins.tp1.sga.resource;

import br.unitins.tp1.sga.dto.AtualizarModeloFacaDTO;
import br.unitins.tp1.sga.dto.CriarModeloFacaDTO;
import br.unitins.tp1.sga.dto.ModeloFacaDTO;
import br.unitins.tp1.sga.model.Cabo;
import br.unitins.tp1.sga.model.Cuteleiro;
import br.unitins.tp1.sga.model.Guarnicao;
import br.unitins.tp1.sga.model.Lamina;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.repository.CaboRepository;
import br.unitins.tp1.sga.repository.CuteleiroRepository;
import br.unitins.tp1.sga.repository.GuarnicaoRepository;
import br.unitins.tp1.sga.repository.LaminaRepository;
import br.unitins.tp1.sga.repository.ModeloFacaRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.ArrayList;
import java.util.List;

@Path("/administrativo/v1/facas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed({"Cuteleiro", "Administrador"})
public class ModeloFacaResource {

    @Inject ModeloFacaRepository modeloFacaRepository;
    @Inject CuteleiroRepository cuteleiroRepository;
    @Inject LaminaRepository laminaRepository;
    @Inject CaboRepository caboRepository;
    @Inject GuarnicaoRepository guarnicaoRepository;
    @Inject JsonWebToken jwt;

    @HEAD
    public Response head() {
        return Response.ok().build();
    }

    /**
     * Admin vê todos os modelos; Cuteleiro vê só os seus.
     */
    @GET
    public List<ModeloFacaDTO> listar() {
        if (isAdmin()) {
            return modeloFacaRepository.listAll()
                    .stream().map(ModeloFacaDTO::resumo).toList();
        }
        Cuteleiro cuteleiro = cuteleiroAutenticado();
        return modeloFacaRepository.findByCuteleiroId(cuteleiro.id)
                .stream().map(ModeloFacaDTO::resumo).toList();
    }

    @GET
    @Path("/{id}")
    public ModeloFacaDTO buscarPorId(@PathParam("id") Long id) {
        ModeloFaca modelo = buscarEValidarPosse(id);
        return ModeloFacaDTO.detalhes(modelo, null, null);
    }

    @POST
    @Transactional
    public Response criar(@Valid CriarModeloFacaDTO dto) {
        // Admin pode especificar o cuteleiro via dto.cuteleiroId, ou criar sem criador.
        // Cuteleiro autenticado sempre vira o criador.
        Cuteleiro cuteleiro;
        if (isAdmin()) {
            if (dto.cuteleiroId != null) {
                cuteleiro = cuteleiroRepository.findById(dto.cuteleiroId);
                if (cuteleiro == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"erro\": \"Cuteleiro não encontrado com ID: " + dto.cuteleiroId + "\"}").build();
                }
            } else {
                cuteleiro = null; // admin criando sem cuteleiro específico
            }
        } else {
            cuteleiro = cuteleiroAutenticado();
        }

        Lamina lamina = laminaRepository.findById(dto.laminaPadraoId);
        if (lamina == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"Lâmina não encontrada com ID: " + dto.laminaPadraoId + "\"}").build();
        }

        Cabo cabo = caboRepository.findById(dto.caboPadraoId);
        if (cabo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"Cabo não encontrado com ID: " + dto.caboPadraoId + "\"}").build();
        }

        ModeloFaca modelo = new ModeloFaca();
        modelo.setNome(dto.nome);
        modelo.setDescricao(dto.descricao);
        modelo.setPrecoBase(dto.precoBase);
        modelo.setAtivo(true);
        modelo.setCriador(cuteleiro);
        modelo.setLaminaPadrao(lamina);
        modelo.setCaboPadrao(cabo);

        if (dto.guarnicoesPadraoIds != null && !dto.guarnicoesPadraoIds.isEmpty()) {
            List<Guarnicao> guarnicoes = new ArrayList<>();
            for (Long gId : dto.guarnicoesPadraoIds) {
                Guarnicao guarnicao = guarnicaoRepository.findById(gId);
                if (guarnicao == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"erro\": \"Guarnição não encontrada com ID: " + gId + "\"}").build();
                }
                guarnicoes.add(guarnicao);
            }
            modelo.setGuarnicoesPadrao(guarnicoes);
        }

        modeloFacaRepository.persist(modelo);
        return Response.status(Response.Status.CREATED).entity(ModeloFacaDTO.resumo(modelo)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") Long id, @Valid AtualizarModeloFacaDTO dto) {
        ModeloFaca modelo = buscarEValidarPosse(id);

        Lamina lamina = laminaRepository.findById(dto.laminaPadraoId);
        if (lamina == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"Lâmina não encontrada com ID: " + dto.laminaPadraoId + "\"}").build();
        }

        Cabo cabo = caboRepository.findById(dto.caboPadraoId);
        if (cabo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"erro\": \"Cabo não encontrado com ID: " + dto.caboPadraoId + "\"}").build();
        }

        modelo.setNome(dto.nome);
        modelo.setDescricao(dto.descricao);
        modelo.setPrecoBase(dto.precoBase);
        modelo.setLaminaPadrao(lamina);
        modelo.setCaboPadrao(cabo);

        if (dto.guarnicoesPadraoIds != null) {
            List<Guarnicao> guarnicoes = new ArrayList<>();
            for (Long gId : dto.guarnicoesPadraoIds) {
                Guarnicao guarnicao = guarnicaoRepository.findById(gId);
                if (guarnicao == null) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("{\"erro\": \"Guarnição não encontrada com ID: " + gId + "\"}").build();
                }
                guarnicoes.add(guarnicao);
            }
            modelo.setGuarnicoesPadrao(guarnicoes);
        }

        modeloFacaRepository.persist(modelo);
        return Response.ok(ModeloFacaDTO.resumo(modelo)).build();
    }

    @PATCH
    @Path("/{id}/desativar")
    @Transactional
    public Response desativar(@PathParam("id") Long id) {
        ModeloFaca modelo = buscarEValidarPosse(id);
        modelo.desativar();
        modeloFacaRepository.persist(modelo);
        return Response.ok("{\"mensagem\": \"Faca desativada com sucesso\"}").build();
    }

    // -------------------------------------------------------------------------

    private boolean isAdmin() {
        return jwt.getGroups() != null && jwt.getGroups().contains("Administrador");
    }

    /**
     * Retorna o Cuteleiro autenticado. Só chamar quando !isAdmin().
     */
    private Cuteleiro cuteleiroAutenticado() {
        String login = jwt.getName();
        Cuteleiro cuteleiro = cuteleiroRepository.findByLogin(login);
        if (cuteleiro == null) {
            throw new NotFoundException("Cuteleiro autenticado não encontrado para login: " + login);
        }
        return cuteleiro;
    }

    /**
     * Admin pode acessar qualquer modelo. Cuteleiro só acessa o próprio.
     */
    private ModeloFaca buscarEValidarPosse(Long id) {
        ModeloFaca modelo = modeloFacaRepository.findById(id);
        if (modelo == null) {
            throw new NotFoundException("Faca não encontrada com ID: " + id);
        }
        if (!isAdmin()) {
            Cuteleiro cuteleiro = cuteleiroAutenticado();
            if (modelo.getCriador() == null || !modelo.getCriador().id.equals(cuteleiro.id)) {
                throw new ForbiddenException("Você não tem permissão para acessar esta faca");
            }
        }
        return modelo;
    }
}
