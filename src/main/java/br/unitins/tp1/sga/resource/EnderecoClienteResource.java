package br.unitins.tp1.sga.resource;

import br.unitins.tp1.sga.dto.EnderecoClienteCreateDTO;
import br.unitins.tp1.sga.dto.EnderecoClienteDTO;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.EnderecoCliente;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.EnderecoClienteRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import java.util.stream.Collectors;

@Path("/clientes/me/enderecos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Cliente")
public class EnderecoClienteResource {

    @Inject
    EnderecoClienteRepository enderecoRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    JsonWebToken jwt;

    /**
     * Lista todos os endereços ativos do cliente autenticado.
     */
    @GET
    public List<EnderecoClienteDTO> listar() {
        Cliente cliente = clienteAutenticado();
        return enderecoRepository.findAtivosByClienteId(cliente.id)
                .stream()
                .map(EnderecoClienteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Adiciona um novo endereço para o cliente autenticado.
     * Se for o primeiro endereço, é marcado como principal automaticamente.
     */
    @POST
    @Transactional
    public Response inserir(@Valid EnderecoClienteCreateDTO dto) {
        Cliente cliente = clienteAutenticado();

        EnderecoCliente endereco = new EnderecoCliente();
        preencher(endereco, dto);
        endereco.setCliente(cliente);
        endereco.setAtivo(true);

        // Se não tem nenhum endereço ativo, este vira o principal
        List<EnderecoCliente> existentes = enderecoRepository.findAtivosByClienteId(cliente.id);
        if (existentes.isEmpty()) {
            endereco.setPrincipal(true);
        }

        enderecoRepository.persist(endereco);
        return Response.status(Response.Status.CREATED)
                .entity(EnderecoClienteDTO.fromEntity(endereco))
                .build();
    }

    /**
     * Atualiza um endereço do cliente autenticado.
     */
    @PUT
    @Path("/{id}")
    @Transactional
    public Response alterar(@PathParam("id") Long id, @Valid EnderecoClienteCreateDTO dto) {
        EnderecoCliente endereco = buscarEValidarPosse(id);
        preencher(endereco, dto);

        enderecoRepository.persist(endereco);
        return Response.ok(EnderecoClienteDTO.fromEntity(endereco)).build();
    }

    /**
     * Define um endereço como principal (remove o principal anterior).
     */
    @PUT
    @Path("/{id}/principal")
    @Transactional
    public Response definirPrincipal(@PathParam("id") Long id) {
        Cliente cliente = clienteAutenticado();
        EnderecoCliente novo = buscarEValidarPosse(id);

        // Remove o principal atual
        enderecoRepository.findAtivosByClienteId(cliente.id).forEach(e -> {
            if (e.isPrincipal()) {
                e.setPrincipal(false);
                enderecoRepository.persist(e);
            }
        });

        novo.setPrincipal(true);
        enderecoRepository.persist(novo);
        return Response.ok(EnderecoClienteDTO.fromEntity(novo)).build();
    }

    /**
     * Desativa (soft delete) um endereço do cliente autenticado.
     * Usa soft delete para preservar referências em pedidos antigos.
     */
    @DELETE
    @Path("/{id}")
    @Transactional
    public Response desativar(@PathParam("id") Long id) {
        EnderecoCliente endereco = buscarEValidarPosse(id);

        if (endereco.isPrincipal()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Não é possível remover o endereço principal. Defina outro como principal primeiro.")
                    .build();
        }

        endereco.setAtivo(false);
        enderecoRepository.persist(endereco);
        return Response.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Métodos utilitários privados
    // -------------------------------------------------------------------------

    private void preencher(EnderecoCliente endereco, EnderecoClienteCreateDTO dto) {
        endereco.setCep(dto.cep);
        endereco.setRua(dto.rua);
        endereco.setNumero(dto.numero);
        endereco.setComplemento(dto.complemento);
        endereco.setBairro(dto.bairro);
        endereco.setCidade(dto.cidade);
        endereco.setEstado(dto.estado);
    }

    private Cliente clienteAutenticado() {
        Cliente cliente = clienteRepository.findByLogin(jwt.getName());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }
        return cliente;
    }

    private EnderecoCliente buscarEValidarPosse(Long id) {
        EnderecoCliente endereco = enderecoRepository.findById(id);
        if (endereco == null || !endereco.isAtivo()) {
            throw new NotFoundException("Endereço não encontrado com ID: " + id);
        }
        if (!endereco.getCliente().getLogin().equals(jwt.getName())) {
            throw new jakarta.ws.rs.ForbiddenException("Você não tem permissão para acessar este endereço");
        }
        return endereco;
    }
}
