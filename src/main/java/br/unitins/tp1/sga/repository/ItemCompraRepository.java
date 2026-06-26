package br.unitins.tp1.sga.repository;

import br.unitins.tp1.sga.model.ItemCompra;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class ItemCompraRepository implements PanacheRepository<ItemCompra> {

    public List<ItemCompra> findByCompraId(Long compraId) {
        return find("compra.id = ?1", compraId).list();
    }

    /**
     * Verifica se existe algum ItemCompra de um dado modeloFaca
     * pertencente a uma Compra do cliente — usado em AvaliacaoModeloResource
     * para marcar avaliação como "verificado".
     */
    public boolean existsCompraConfirmadaByClienteAndModelo(Long clienteId, Long modeloFacaId) {
        return count(
            "compra.cliente.id = ?1 AND modeloFaca.id = ?2 AND compra.status <> br.unitins.tp1.sga.model.StatusCompra.CANCELADO AND compra.status <> br.unitins.tp1.sga.model.StatusCompra.AGUARDANDO_PAGAMENTO",
            clienteId, modeloFacaId
        ) > 0;
    }
}
