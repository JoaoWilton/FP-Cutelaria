package br.unitins.tp1.sga.resource.ecommerce;

import br.unitins.tp1.sga.dto.AdicionarListaDesejosDTO;
import br.unitins.tp1.sga.dto.ItemListaDesejosDTO;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.ItemListaDesejos;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.model.OpcaoPersonalizacao;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.ItemListaDesejosRepository;
import br.unitins.tp1.sga.repository.ModeloFacaRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/ecommerce/v1/lista-desejos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Cliente")
public class ListaDesejosResource {

    @Inject
    JsonWebToken jwt;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ModeloFacaRepository modeloFacaRepository;

    @Inject
    ItemListaDesejosRepository itemListaDesejosRepository;

    @GET
    public List<ItemListaDesejosDTO> listar() {
        Cliente cliente = clienteAtual();
        return itemListaDesejosRepository.findByClienteId(cliente.id)
                .stream()
                .map(ItemListaDesejosDTO::fromEntity)
                .toList();
    }

    /**
     * Adiciona um modelo à lista de desejos, opcionalmente com personalizações.
     *
     * Diferente da versão anterior, NÃO há checagem de duplicata: o mesmo
     * modelo pode ser adicionado várias vezes com personalizações diferentes
     * (cada combinação é um item separado, com seu próprio precoCalculado).
     */
    @POST
    @Path("/{modeloId}")
    @Transactional
    public Response adicionar(@PathParam("modeloId") Long modeloId, AdicionarListaDesejosDTO dto) {
        Cliente cliente = clienteAtual();
        ModeloFaca modelo = modeloAtivo(modeloId);

        Map<String, String> personalizacoes = (dto != null && dto.personalizacoes != null)
                ? dto.personalizacoes
                : Map.of();

        validarPersonalizacoes(modelo, personalizacoes);

        ItemListaDesejos item = new ItemListaDesejos();
        item.setCliente(cliente);
        item.setModeloFaca(modelo);
        item.setPersonalizacoes(personalizacoes);
        item.setPrecoCalculado(modelo.calcularPrecoComPersonalizacoes(personalizacoes));

        itemListaDesejosRepository.persist(item);
        return Response.status(Response.Status.CREATED).entity(ItemListaDesejosDTO.fromEntity(item)).build();
    }

    /**
     * Remove um item da lista de desejos pelo seu próprio ID.
     * (Antes era por modeloId — agora que pode haver múltiplos itens do
     * mesmo modelo, é necessário identificar o item exato.)
     */
    @DELETE
    @Path("/{itemId}")
    @Transactional
    public Response remover(@PathParam("itemId") Long itemId) {
        Cliente cliente = clienteAtual();
        ItemListaDesejos item = itemListaDesejosRepository.findById(itemId);
        if (item == null) {
            throw new NotFoundException("Item da lista de desejos não encontrado com ID: " + itemId);
        }
        if (!item.getCliente().id.equals(cliente.id)) {
            throw new ForbiddenException("Este item não pertence ao cliente autenticado");
        }

        itemListaDesejosRepository.delete(item);
        return Response.noContent().build();
    }

    private Cliente clienteAtual() {
        String login = jwt.getName();
        Cliente cliente = clienteRepository.findByLogin(login);
        if (cliente == null) {
            throw new NotFoundException("Cliente autenticado não encontrado");
        }
        return cliente;
    }

    private ModeloFaca modeloAtivo(Long modeloId) {
        ModeloFaca modelo = modeloFacaRepository.findById(modeloId);
        if (modelo == null || !modelo.isAtivo()) {
            throw new NotFoundException("Produto não encontrado com ID: " + modeloId);
        }
        return modelo;
    }

    /**
     * Valida que cada entrada de `personalizacoes` corresponde a uma
     * OpcaoPersonalizacao ativa do modelo (mesmo tipo + mesma opção).
     * Rejeita com 400 se alguma combinação não existir.
     */
    private void validarPersonalizacoes(ModeloFaca modelo, Map<String, String> personalizacoes) {
        for (Map.Entry<String, String> entrada : personalizacoes.entrySet()) {
            String tipo = entrada.getKey();
            String opcao = entrada.getValue();

            boolean existe = modelo.getOpcoesPersonalizacao().stream()
                    .filter(OpcaoPersonalizacao::isAtivo)
                    .anyMatch(op -> op.getTipo() != null
                            && op.getTipo().name().equals(tipo)
                            && op.getOpcao().equals(opcao));

            if (!existe) {
                throw new WebApplicationException(
                        "Personalização inválida para este modelo: " + tipo + " = " + opcao,
                        Response.Status.BAD_REQUEST);
            }
        }
    }
}
