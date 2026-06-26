package br.unitins.tp1.sga.resource;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.tp1.sga.dto.AlterarSenhaDTO;
import br.unitins.tp1.sga.dto.AtualizarCuteleiroDTO;
import br.unitins.tp1.sga.dto.CadastroCuteleiroDTO;
import br.unitins.tp1.sga.dto.CuteleiroDTO;
import br.unitins.tp1.sga.model.Cuteleiro;
import br.unitins.tp1.sga.repository.CuteleiroRepository;
import br.unitins.tp1.sga.service.AuthService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * CRUD administrativo de cuteleiros.
 * Toda a manutenção de cuteleiros é feita pelo Administrador.
 * A listagem (GET) é pública pois alimenta vitrines do e-commerce
 * (ex: "cuteleiros parceiros").
 */
@Path("/administrativo/v1/cuteleiros")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CuteleiroResource {

    @Inject
    CuteleiroRepository repository;

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    @GET
    public List<CuteleiroDTO> listarTodos(@QueryParam("especialidade") String especialidade) {
        List<Cuteleiro> lista = (especialidade != null && !especialidade.isEmpty())
                ? repository.findByEspecialidade(especialidade)
                : repository.listAll();
        return lista.stream().map(CuteleiroDTO::fromEntity).collect(Collectors.toList());
    }

    @HEAD
    public Response listarTodosHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/buscar")
    public List<CuteleiroDTO> buscarPorNome(@QueryParam("nome") String nome) {
        List<Cuteleiro> lista = (nome == null || nome.isEmpty())
                ? repository.listAll()
                : repository.findByNome(nome);
        return lista.stream().map(CuteleiroDTO::fromEntity).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public CuteleiroDTO buscarPorId(@PathParam("id") Long id) {
        Cuteleiro entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cuteleiro não encontrado com ID: " + id);
        }
        return CuteleiroDTO.fromEntity(entity);
    }

    @POST
    @Transactional
    @RolesAllowed("Administrador")
    public Response inserir(@Valid CadastroCuteleiroDTO dto) {
        if (repository.findByLogin(dto.login) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Já existe um usuário com este login")
                    .build();
        }

        Cuteleiro cuteleiro = new Cuteleiro();
        cuteleiro.setNome(dto.nome);
        cuteleiro.setEmail(dto.email);
        cuteleiro.setTelefone(dto.telefone);
        cuteleiro.setLogin(dto.login);
        cuteleiro.setSenha(authService.hashSenha(dto.senha));
        cuteleiro.setTipoUsuario(br.unitins.tp1.sga.model.TipoUsuario.CUTELEIRO);
        cuteleiro.setEspecialidade(dto.especialidade);
        cuteleiro.setDataContratacao(dto.dataContratacao);

        repository.persist(cuteleiro);
        return Response.status(Response.Status.CREATED).entity(CuteleiroDTO.fromEntity(cuteleiro)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed("Administrador")
    public Response alterar(@PathParam("id") Long id, @Valid AtualizarCuteleiroDTO dto) {
        Cuteleiro entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cuteleiro não encontrado com ID: " + id);
        }

        entity.setNome(dto.nome);
        entity.setEmail(dto.email);
        entity.setTelefone(dto.telefone);
        entity.setLogin(dto.login);
        entity.setEspecialidade(dto.especialidade);
        entity.setDataContratacao(dto.dataContratacao);
        // Senha não é alterada por este endpoint (mesmo motivo do ClienteResource).

        repository.persist(entity);
        return Response.ok(CuteleiroDTO.fromEntity(entity)).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed("Administrador")
    public Response deletar(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Cuteleiro não encontrado com ID: " + id);
        }
        return Response.noContent().build();
    }

    /**
     * Alteração de senha do próprio cuteleiro.
     * O administrador também pode alterar a senha de qualquer cuteleiro.
     */
    @PATCH
    @Path("/{id}/senha")
    @Transactional
    @RolesAllowed({"Cuteleiro", "Administrador"})
    public Response alterarSenha(@PathParam("id") Long id, @Valid AlterarSenhaDTO dto) {
        Cuteleiro entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cuteleiro não encontrado com ID: " + id);
        }
        // Cuteleiro só altera a própria senha; admin pode alterar qualquer uma
        if (!jwt.getGroups().contains("Administrador")
                && !jwt.getName().equals(entity.getLogin())) {
            throw new jakarta.ws.rs.ForbiddenException("Você não tem permissão para alterar esta senha");
        }
        if (!BcryptUtil.matches(dto.senhaAtual, entity.getSenha())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Senha atual incorreta").build();
        }
        entity.setSenha(authService.hashSenha(dto.novaSenha));
        repository.persist(entity);
        return Response.ok("{\"mensagem\": \"Senha alterada com sucesso\"}").build();
    }
}
