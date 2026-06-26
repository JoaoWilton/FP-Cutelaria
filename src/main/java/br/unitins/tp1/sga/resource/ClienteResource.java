package br.unitins.tp1.sga.resource;

import java.util.List;
import java.util.stream.Collectors;

import br.unitins.tp1.sga.dto.AlterarSenhaDTO;
import br.unitins.tp1.sga.dto.AtualizarClienteDTO;
import br.unitins.tp1.sga.dto.CadastroClienteDTO;
import br.unitins.tp1.sga.dto.ClienteDTO;
import br.unitins.tp1.sga.dto.CompletarCadastroDTO;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.EnderecoCliente;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.EnderecoClienteRepository;
import br.unitins.tp1.sga.service.AuthService;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/clientes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClienteResource {

    @Inject
    ClienteRepository repository;

    @Inject
    EnderecoClienteRepository enderecoRepository;

    @Inject
    AuthService authService;

    @Inject
    JsonWebToken jwt;

    // -------------------------------------------------------------------------
    // Endpoints administrativos
    // -------------------------------------------------------------------------

    @GET
    @RolesAllowed("Administrador")
    public List<ClienteDTO> listarTodos() {
        return repository.listAll().stream().map(ClienteDTO::fromEntity).collect(Collectors.toList());
    }

    @HEAD
    @RolesAllowed("Administrador")
    public Response listarTodosHead() {
        return Response.ok().build();
    }

    @GET
    @Path("/buscar")
    @RolesAllowed("Administrador")
    public List<ClienteDTO> buscarPorNome(@QueryParam("nome") String nome) {
        List<Cliente> lista = (nome == null || nome.isEmpty())
                ? repository.listAll()
                : repository.findByNome(nome);
        return lista.stream().map(ClienteDTO::fromEntity).collect(Collectors.toList());
    }

    @GET
    @Path("/cpf/{cpf}")
    @RolesAllowed("Administrador")
    public ClienteDTO buscarPorCpf(@PathParam("cpf") String cpf) {
        Cliente entity = repository.findByCpf(cpf);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com CPF: " + cpf);
        }
        return ClienteDTO.fromEntity(entity);
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed("Administrador")
    public Response deletar(@PathParam("id") Long id) {
        boolean deleted = repository.deleteById(id);
        if (!deleted) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        return Response.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Endpoints de cliente (próprio registro) ou admin
    // -------------------------------------------------------------------------

    @GET
    @Path("/{id}")
    @RolesAllowed({"Cliente", "Administrador"})
    public Response buscarPorId(@PathParam("id") Long id) {
        Cliente entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        verificarAcessoProprioOuAdmin(entity);
        return Response.ok(ClienteDTO.fromEntity(entity)).build();
    }

    @HEAD
    @Path("/{id}")
    @RolesAllowed({"Cliente", "Administrador"})
    public Response buscarPorIdHead(@PathParam("id") Long id) {
        Cliente entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        verificarAcessoProprioOuAdmin(entity);
        return Response.ok().build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @RolesAllowed({"Cliente", "Administrador"})
    public Response alterar(@PathParam("id") Long id, @Valid AtualizarClienteDTO dto) {
        Cliente entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        verificarAcessoProprioOuAdmin(entity);

        entity.setNome(dto.nome);
        entity.setEmail(dto.email);
        entity.setTelefone(dto.telefone);
        // CPF e data nascimento não são alterados aqui — usar PATCH /completar-cadastro
        // Senha não é alterada aqui — usar PATCH /{id}/senha

        repository.persist(entity);
        return Response.ok(ClienteDTO.fromEntity(entity)).build();
    }

    // -------------------------------------------------------------------------
    // Cadastro público — permite cadastro simples (só nome, email, login, senha)
    // -------------------------------------------------------------------------

    /**
     * Cadastro simples: aceita apenas nome, email, login e senha.
     * CPF, data de nascimento e endereço são opcionais neste passo.
     * Se não fornecidos, cadastroCompleto = false e o checkout será bloqueado
     * até que o cliente complete via PATCH /{id}/completar-cadastro.
     */
    @POST
    @Transactional
    public Response inserir(@Valid CadastroClienteDTO dto) {
        if (repository.findByLogin(dto.login) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Já existe um usuário com este login").build();
        }
        if (repository.findByEmail(dto.email) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Já existe um usuário com este email").build();
        }

        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome);
        cliente.setEmail(dto.email);
        cliente.setTelefone(dto.telefone);
        cliente.setLogin(dto.login);
        cliente.setSenha(authService.hashSenha(dto.senha));
        cliente.setCpf(dto.cpf);
        cliente.setDataNascimento(dto.dataNascimento);

        // Se CPF e dataNascimento foram fornecidos, marca cadastro completo
        boolean temCpf = dto.cpf != null && !dto.cpf.isBlank();
        boolean temDataNasc = dto.dataNascimento != null;
        cliente.setCadastroCompleto(temCpf && temDataNasc);

        repository.persist(cliente);
        return Response.status(Response.Status.CREATED).entity(ClienteDTO.fromEntity(cliente)).build();
    }

    // -------------------------------------------------------------------------
    // PATCH: alteração de senha
    // -------------------------------------------------------------------------

    /**
     * Altera a senha do cliente. Exige a senha atual para confirmação.
     */
    @PATCH
    @Path("/{id}/senha")
    @Transactional
    @RolesAllowed({"Cliente", "Administrador"})
    public Response alterarSenha(@PathParam("id") Long id, @Valid AlterarSenhaDTO dto) {
        Cliente entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        verificarAcessoProprioOuAdmin(entity);

        if (!BcryptUtil.matches(dto.senhaAtual, entity.getSenha())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Senha atual incorreta").build();
        }

        entity.setSenha(authService.hashSenha(dto.novaSenha));
        repository.persist(entity);
        return Response.ok("{\"mensagem\": \"Senha alterada com sucesso\"}").build();
    }

    // -------------------------------------------------------------------------
    // PATCH: completar cadastro
    // -------------------------------------------------------------------------

    /**
     * Completa o cadastro do cliente com CPF, data de nascimento, telefone
     * e opcionalmente um endereço principal. Após este passo, cadastroCompleto = true
     * e o cliente poderá realizar pedidos.
     */
    @PATCH
    @Path("/{id}/completar-cadastro")
    @Transactional
    @RolesAllowed({"Cliente", "Administrador"})
    public Response completarCadastro(@PathParam("id") Long id, @Valid CompletarCadastroDTO dto) {
        Cliente entity = repository.findById(id);
        if (entity == null) {
            throw new NotFoundException("Cliente não encontrado com ID: " + id);
        }
        verificarAcessoProprioOuAdmin(entity);

        // Verificar unicidade do CPF (outro cliente pode já ter este CPF)
        Cliente existenteCpf = repository.findByCpf(dto.cpf);
        if (existenteCpf != null && !existenteCpf.id.equals(entity.id)) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("CPF já cadastrado por outro cliente").build();
        }

        entity.setCpf(dto.cpf);
        entity.setDataNascimento(dto.dataNascimento);
        if (dto.telefone != null && !dto.telefone.isBlank()) {
            entity.setTelefone(dto.telefone);
        }

        // Se um endereço foi enviado, persiste como principal
        if (dto.endereco != null) {
            EnderecoCliente endereco = new EnderecoCliente();
            endereco.setCep(dto.endereco.cep);
            endereco.setRua(dto.endereco.rua);
            endereco.setNumero(dto.endereco.numero);
            endereco.setComplemento(dto.endereco.complemento);
            endereco.setBairro(dto.endereco.bairro);
            endereco.setCidade(dto.endereco.cidade);
            endereco.setEstado(dto.endereco.estado);
            endereco.setPrincipal(true);
            endereco.setAtivo(true);
            endereco.setCliente(entity);
            enderecoRepository.persist(endereco);
        }

        entity.setCadastroCompleto(true);
        repository.persist(entity);

        return Response.ok("{\"mensagem\": \"Cadastro completado com sucesso\"}").build();
    }

    // -------------------------------------------------------------------------
    // Utilitário
    // -------------------------------------------------------------------------

    private void verificarAcessoProprioOuAdmin(Cliente entity) {
        if (jwt.getGroups() != null && jwt.getGroups().contains("Administrador")) {
            return;
        }
        if (!jwt.getName().equals(entity.getLogin())) {
            throw new jakarta.ws.rs.ForbiddenException("Você não tem permissão para acessar este recurso");
        }
    }
}
