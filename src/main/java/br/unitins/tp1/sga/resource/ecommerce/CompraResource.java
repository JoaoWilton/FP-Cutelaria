package br.unitins.tp1.sga.resource.ecommerce;

import br.unitins.tp1.sga.dto.CompraDTO;
import br.unitins.tp1.sga.dto.CriarCompraDTO;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.Compra;
import br.unitins.tp1.sga.model.EnderecoCliente;
import br.unitins.tp1.sga.model.ItemCompra;
import br.unitins.tp1.sga.model.ItemListaDesejos;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.model.StatusCompra;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.CompraRepository;
import br.unitins.tp1.sga.repository.EnderecoClienteRepository;
import br.unitins.tp1.sga.repository.ItemListaDesejosRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Path("/ecommerce/v1/compras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Cliente")
public class CompraResource {

    @Inject
    CompraRepository compraRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    EnderecoClienteRepository enderecoRepository;

    @Inject
    ItemListaDesejosRepository listaDesejosRepository;

    @Inject
    JsonWebToken jwt;

    /**
     * Checkout: cria uma Compra a partir de itens selecionados da lista de desejos.
     *
     * Regras:
     * 1. Cliente deve ter cadastro completo.
     * 2. Endereço deve pertencer ao cliente e estar ativo.
     * 3. Todos os itemListaDesejosIds devem pertencer ao cliente.
     * 4. Para cada item: revalida modelo ativo e recalcula preço do zero.
     * 5. Remove os itens da lista de desejos (foram "convertidos").
     */
    @POST
    @Transactional
    public Response criar(@Valid CriarCompraDTO dto) {
        Cliente cliente = clienteRepository.findByLogin(jwt.getName());
        if (cliente == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Cliente não encontrado").build();
        }

        if (!cliente.isCadastroCompleto()) {
            return Response.status(422)
                    .entity("Cadastro incompleto. Complete seu perfil antes de realizar uma compra.")
                    .build();
        }

        EnderecoCliente endereco = enderecoRepository.findById(dto.enderecoEntregaId);
        if (endereco == null || !endereco.isAtivo()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Endereço de entrega não encontrado ou inativo").build();
        }
        if (!endereco.getCliente().id.equals(cliente.id)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Endereço de entrega não pertence ao cliente autenticado").build();
        }

        // Valida e converte cada item da lista de desejos
        List<ItemListaDesejos> itensLista = new ArrayList<>();
        for (Long itemId : dto.itemListaDesejosIds) {
            ItemListaDesejos itemLista = listaDesejosRepository.findById(itemId);
            if (itemLista == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Item da lista de desejos não encontrado: " + itemId).build();
            }
            if (!itemLista.getCliente().id.equals(cliente.id)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Item " + itemId + " não pertence ao cliente autenticado").build();
            }
            itensLista.add(itemLista);
        }

        // Cria a Compra
        Compra compra = new Compra();
        compra.setCliente(cliente);
        compra.setEnderecoEntrega(endereco);
        compra.setStatus(StatusCompra.AGUARDANDO_PAGAMENTO);
        compra.setNumeroSistema(gerarNumeroSistema());

        // Converte cada ItemListaDesejos em ItemCompra, recalculando preço
        for (ItemListaDesejos itemLista : itensLista) {
            ModeloFaca modelo = itemLista.getModeloFaca();
            if (modelo == null || !modelo.isAtivo()) {
                return Response.status(Response.Status.GONE)
                        .entity("Modelo não está mais disponível: " +
                                (modelo != null ? modelo.getNome() : "id=" + itemLista.id))
                        .build();
            }

            BigDecimal precoBase = modelo.getPrecoBase();
            BigDecimal precoTotal = modelo.calcularPrecoComPersonalizacoes(itemLista.getPersonalizacoes());
            BigDecimal custoPersonalizacoes = precoTotal.subtract(precoBase);

            ItemCompra item = new ItemCompra();
            item.setModeloFaca(modelo);
            item.setCompra(compra);
            item.setPrecoBase(precoBase);
            item.setCustoPersonalizacoes(custoPersonalizacoes);
            item.setPrecoTotal(precoTotal);
            item.setPersonalizacoes(itemLista.getPersonalizacoes());

            compra.getItens().add(item);

            // Remove o item da lista de desejos — foi "convertido" em compra
            listaDesejosRepository.delete(itemLista);
        }

        compra.recalcularTotal();
        compraRepository.persist(compra);

        return Response.status(Response.Status.CREATED)
                .entity(CompraDTO.fromEntity(compra))
                .build();
    }

    @GET
    public List<CompraDTO> listarMinhasCompras() {
        Cliente cliente = clienteRepository.findByLogin(jwt.getName());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }
        return compraRepository.findByClienteId(cliente.id)
                .stream()
                .map(CompraDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public CompraDTO buscarPorId(@PathParam("id") Long id) {
        Compra compra = compraRepository.findById(id);
        if (compra == null) {
            throw new NotFoundException("Compra não encontrada com ID: " + id);
        }
        if (!compra.getCliente().getLogin().equals(jwt.getName())) {
            throw new jakarta.ws.rs.ForbiddenException("Você não tem permissão para acessar esta compra");
        }
        return CompraDTO.fromEntity(compra);
    }

    @HEAD
    public Response head() {
        return Response.ok().build();
    }

    private String gerarNumeroSistema() {
        String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String aleatorio = String.format("%08d", new Random().nextInt(100_000_000));
        return "FP-" + data + "-" + aleatorio;
    }
}
