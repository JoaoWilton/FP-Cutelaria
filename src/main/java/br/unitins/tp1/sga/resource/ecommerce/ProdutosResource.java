package br.unitins.tp1.sga.resource.ecommerce;

import br.unitins.tp1.sga.dto.ModeloFacaDTO;
import br.unitins.tp1.sga.dto.PaginatedResponseDTO;
import br.unitins.tp1.sga.model.ModeloFaca;
import br.unitins.tp1.sga.repository.AvaliacaoModeloRepository;
import br.unitins.tp1.sga.repository.ModeloFacaRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

@Path("/ecommerce/v1/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutosResource {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    @Inject
    ModeloFacaRepository modeloFacaRepository;

    @Inject
    AvaliacaoModeloRepository avaliacaoModeloRepository;

    @GET
    public PaginatedResponseDTO<ModeloFacaDTO> listar(
            @QueryParam("page") Integer page,
            @QueryParam("size") Integer size,
            @QueryParam("nome") String nome,
            @QueryParam("precoMin") BigDecimal precoMin,
            @QueryParam("precoMax") BigDecimal precoMax) {

        int pagina = normalizarPage(page);
        int tamanho = normalizarSize(size);
        List<ModeloFacaDTO> modelos = modeloFacaRepository
                .findAtivosFiltrados(nome, precoMin, precoMax, pagina, tamanho)
                .stream()
                .map(ModeloFacaDTO::resumo)
                .toList();
        long total = modeloFacaRepository.countAtivosFiltrados(nome, precoMin, precoMax);
        return new PaginatedResponseDTO<>(modelos, total, pagina, tamanho);
    }

    @HEAD
    public Response listarProdutosHead() {
        return Response.ok().build();
    }


    @GET
    @Path("/{id}")
    public ModeloFacaDTO buscarPorId(@PathParam("id") Long id) {
        ModeloFaca modelo = modeloFacaRepository.findById(id);
        if (modelo == null || !modelo.isAtivo()) {
            throw new NotFoundException("Produto não encontrado com ID: " + id);
        }

        Double media = avaliacaoModeloRepository.mediaByModeloFacaId(id);
        Long totalAvaliacoes = avaliacaoModeloRepository.count("modeloFaca.id", id);
        return ModeloFacaDTO.detalhes(modelo, media, totalAvaliacoes);
    }

    private int normalizarPage(Integer page) {
        if (page == null || page < 0) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizarSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }
}
