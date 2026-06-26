package br.unitins.tp1.sga.resource.ecommerce;

import br.unitins.tp1.sga.dto.CompraDTO;
import br.unitins.tp1.sga.model.Compra;
import br.unitins.tp1.sga.model.ItemCompra;
import br.unitins.tp1.sga.model.StatusCompra;
import br.unitins.tp1.sga.repository.ClienteRepository;
import br.unitins.tp1.sga.repository.CompraRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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

@Path("/ecommerce/v1/compras/{compraId}/pix")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("Cliente")
public class PixResource {

    @Inject
    CompraRepository compraRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    JsonWebToken jwt;

    @GET
    public Response gerarPix(@PathParam("compraId") Long compraId) {
        Compra compra = buscarEValidarPosse(compraId);

        if (compra.getStatus() != StatusCompra.AGUARDANDO_PAGAMENTO) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"erro\": \"Compra não está aguardando pagamento. Status atual: "
                            + compra.getStatus() + "\"}")
                    .build();
        }

        String payload = gerarPayloadPix(compra);
        String expiracao = LocalDateTime.now().plusHours(1)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return Response.ok(new PixResponse(
                payload,
                compra.getPrecoTotal().toString(),
                compra.getNumeroSistema(),
                expiracao,
                "FP Cutelaria Artesanal",
                "Pagamento mock — válido apenas para testes. Integrar com API bancária em produção."
        )).build();
    }

    /**
     * Simula confirmação de pagamento (webhook mock).
     * Revalida o preço de cada ItemCompra antes de confirmar — cobre o caso
     * de o preço do produto ter sido atualizado entre a criação da Compra e o pagamento.
     */
    @POST
    @Path("/confirmar")
    @Transactional
    public Response confirmarPagamento(@PathParam("compraId") Long compraId) {
        Compra compra = buscarEValidarPosse(compraId);

        if (compra.getStatus() != StatusCompra.AGUARDANDO_PAGAMENTO) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"erro\": \"Compra não está aguardando pagamento\"}")
                    .build();
        }

        boolean precoMudou = false;
        for (ItemCompra item : compra.getItens()) {
            if (item.getModeloFaca() == null || !item.getModeloFaca().isAtivo()) {
                return Response.status(Response.Status.GONE)
                        .entity("{\"erro\": \"O modelo '" +
                                (item.getModeloFaca() != null ? item.getModeloFaca().getNome() : "?")
                                + "' não está mais disponível\"}")
                        .build();
            }

            BigDecimal precoAtual = item.getModeloFaca()
                    .calcularPrecoComPersonalizacoes(item.getPersonalizacoes());

            if (precoAtual.compareTo(item.getPrecoTotal()) != 0) {
                item.setPrecoBase(item.getModeloFaca().getPrecoBase());
                item.setCustoPersonalizacoes(precoAtual.subtract(item.getModeloFaca().getPrecoBase()));
                item.setPrecoTotal(precoAtual);
                precoMudou = true;
            }
        }

        if (precoMudou) {
            compra.recalcularTotal();
            compraRepository.persist(compra);
            return Response.status(Response.Status.CONFLICT)
                    .entity(CompraDTO.fromEntity(compra))
                    .build();
        }

        compra.setStatus(StatusCompra.PAGAMENTO_CONFIRMADO);
        compra.setDataPagamento(LocalDateTime.now());
        compraRepository.persist(compra);

        return Response.ok("{\"mensagem\": \"Pagamento confirmado com sucesso\", \"status\": \"PAGAMENTO_CONFIRMADO\"}").build();
    }

    private Compra buscarEValidarPosse(Long compraId) {
        Compra compra = compraRepository.findById(compraId);
        if (compra == null) {
            throw new NotFoundException("Compra não encontrada com ID: " + compraId);
        }
        if (!compra.getCliente().getLogin().equals(jwt.getName())) {
            throw new jakarta.ws.rs.ForbiddenException("Você não tem permissão para acessar esta compra");
        }
        return compra;
    }

    private String gerarPayloadPix(Compra compra) {
        return "00020126580014br.gov.bcb.pix0136fp-cutelaria@pix.com.br" +
               "5204000053039865802BR5925FP Cutelaria Artesanal" +
               "6009PALMAS-TO62070503" + compra.getNumeroSistema().replace("-", "") +
               "6304MOCK";
    }

    public static class PixResponse {
        public String payload;
        public String valor;
        public String numeroPedido;
        public String expiracao;
        public String beneficiario;
        public String observacao;

        public PixResponse(String payload, String valor, String numeroPedido,
                           String expiracao, String beneficiario, String observacao) {
            this.payload = payload;
            this.valor = valor;
            this.numeroPedido = numeroPedido;
            this.expiracao = expiracao;
            this.beneficiario = beneficiario;
            this.observacao = observacao;
        }
    }
}
