package br.unitins.tp1.sga.resource;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.tp1.sga.dto.AdministradorDTO;
import br.unitins.tp1.sga.dto.AlterarSenhaDTO;
import br.unitins.tp1.sga.dto.CadastroAdministradorDTO;
import br.unitins.tp1.sga.model.Administrador;
import br.unitins.tp1.sga.repository.AdministradorRepository;
import br.unitins.tp1.sga.service.AuthService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * CRUD de administradores. Todos os endpoints exigem role "Administrador" —
 * ou seja, apenas um administrador já existente pode criar outro.
 *
 * Bootstrap: o primeiro administrador ("admin") é criado pelo DataSeeder no
 * startup da aplicação (login: admin / senha: 123456 / senhaAdministrativa:
 * admin123), já que não existe nenhum outro jeito de obter o primeiro token
 * com role Administrador.
 */
@Path("/administrativo/v1/administradores")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Administrador")
public class AdministradorResource {

    @Inject
    AdministradorRepository repository;

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    @GET
    public List<AdministradorDTO> listarTodos() {
        return repository.listAll().stream()
                .map(AdministradorDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @HEAD
    public Response listarTodosHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/{id}")
    public AdministradorDTO buscarPorId(@PathParam("id") Long id) {
        Administrador entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Administrador não encontrado com ID: " + id);
        }
        return AdministradorDTO.fromEntity(entity);
    }

    /**
     * Cria um novo administrador. Apenas um administrador autenticado pode
     * chamar este endpoint (ver @RolesAllowed na classe).
     */
    @POST
    @Transactional
    public Response inserir(@Valid CadastroAdministradorDTO dto) {
        if (repository.findByLogin(dto.login) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Já existe um usuário com este login")
                    .build();
        }

        Administrador admin = new Administrador();
        admin.setNome(dto.nome);
        admin.setEmail(dto.email);
        admin.setTelefone(dto.telefone);
        admin.setLogin(dto.login);
        admin.setSenha(authService.hashSenha(dto.senha));
        admin.setSenhaAdministrativa(authService.hashSenha(dto.senhaAdministrativa));
        admin.setTipoUsuario(br.unitins.tp1.sga.model.TipoUsuario.ADMINISTRADOR);

        repository.persist(admin);
        return Response.status(Response.Status.CREATED).entity(AdministradorDTO.fromEntity(admin)).build();
    }

    /**
     * Alteração da própria senha (login e-commerce). Para alterar a
     * senhaAdministrativa, seria necessário um endpoint dedicado — fora do
     * escopo desta fase.
     */
    @PATCH
    @Path("/{id}/senha")
    @Transactional
    public Response alterarSenha(@PathParam("id") Long id, @Valid AlterarSenhaDTO dto) {
        Administrador entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Administrador não encontrado com ID: " + id);
        }
        if (!jwt.getName().equals(entity.getLogin())) {
            throw new jakarta.ws.rs.ForbiddenException("Você só pode alterar a própria senha");
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
