package br.unitins.tp1.sga.resource.ecommerce;

import br.unitins.tp1.sga.dto.AvaliacaoModeloDTO;
import br.unitins.tp1.sga.dto.CriarAvaliacaoDTO;
import br.unitins.tp1.sga.model.AvaliacaoModelo;
import br.unitins.tp1.sga.model.Cliente;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.repository.AvaliacaoModeloRepository;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.ItemCompraRepository;
import br.unitins.tp1.sga.repository.ModeloFacaRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Avaliações de modelos de faca (notas 1-5 + comentário opcional).
 *
 * A média e a contagem já eram consumidas em ProdutosResource via
 * AvaliacaoModeloRepository — este resource adiciona o endpoint que faltava
 * para o cliente efetivamente criar/gerenciar avaliações.
 */
@Path("/ecommerce/v1/produtos/{modeloId}/avaliacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AvaliacaoModeloResource {

    @Inject
    AvaliacaoModeloRepository avaliacaoRepository;

    @Inject
    ModeloFacaRepository modeloFacaRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ItemCompraRepository itemCompraRepository;

    @Inject
    JsonWebToken jwt;

    /**
     * Lista as avaliações públicas de um modelo (ordenadas da mais recente).
     * Não exige autenticação — alimenta a página de produto no e-commerce.
     */
    @GET
    public List<AvaliacaoModeloDTO> listar(@PathParam("modeloId") Long modeloId) {
        ModeloFaca modelo = modeloFacaRepository.findById(modeloId);
        if (modelo == null) {
            throw new NotFoundException("Modelo de faca não encontrado com ID: " + modeloId);
        }
        return avaliacaoRepository.findByModeloFacaId(modeloId)
                .stream()
                .map(AvaliacaoModeloDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Cria uma avaliação para o modelo.
     *
     * Regras:
     * - Exige autenticação como Cliente.
     * - O cliente só pode avaliar um modelo que já tenha comprado
     *   (pedido com status diferente de AGUARDANDO_PAGAMENTO/CANCELADO),
     *   o que marca a avaliação como "verificado = true" (compra verificada).
     * - Um cliente pode ter apenas uma avaliação por modelo — se já existir,
     *   esta chamada atualiza a avaliação existente em vez de criar outra.
     */
    @POST
    @Transactional
    @RolesAllowed("Cliente")
    public Response avaliar(@PathParam("modeloId") Long modeloId, @Valid CriarAvaliacaoDTO dto) {
        ModeloFaca modelo = modeloFacaRepository.findById(modeloId);
        if (modelo == null) {
            throw new NotFoundException("Modelo de faca não encontrado com ID: " + modeloId);
        }

        Cliente cliente = clienteRepository.findByLogin(jwt.getName());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }

        // Verifica se o cliente já comprou este modelo via Compra (avaliação verificada)
        boolean comprouModelo = itemCompraRepository
                .existsCompraConfirmadaByClienteAndModelo(cliente.id, modeloId);

        // Verifica se já existe uma avaliação deste cliente para este modelo
        AvaliacaoModelo avaliacao = avaliacaoRepository
                .find("cliente.id = ?1 and modeloFaca.id = ?2", cliente.id, modeloId)
                .firstResult();

        boolean novaAvaliacao = avaliacao == null;
        if (novaAvaliacao) {
            avaliacao = new AvaliacaoModelo();
            avaliacao.setCliente(cliente);
            avaliacao.setModeloFaca(modelo);
        }

        avaliacao.setNota(dto.nota);
        avaliacao.setComentario(dto.comentario);
        avaliacao.setVerificado(comprouModelo);

        avaliacaoRepository.persist(avaliacao);

        Response.Status status = novaAvaliacao ? Response.Status.CREATED : Response.Status.OK;
        return Response.status(status).entity(AvaliacaoModeloDTO.fromEntity(avaliacao)).build();
    }

    /**
     * Remove a avaliação do próprio cliente para este modelo.
     */
    @DELETE
    @Transactional
    @RolesAllowed("Cliente")
    public Response remover(@PathParam("modeloId") Long modeloId) {
        Cliente cliente = clienteRepository.findByLogin(jwt.getName());
        if (cliente == null) {
            throw new NotFoundException("Cliente não encontrado");
        }

        AvaliacaoModelo avaliacao = avaliacaoRepository
                .find("cliente.id = ?1 and modeloFaca.id = ?2", cliente.id, modeloId)
                .firstResult();

        if (avaliacao == null) {
            throw new NotFoundException("Você ainda não avaliou este modelo");
        }

        avaliacaoRepository.delete(avaliacao);
        return Response.noContent().build();
    }
}
